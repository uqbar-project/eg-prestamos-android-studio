package ar.edu.uqbar.eg_prestamos_android_studio;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.URLEncoder;

import ar.edu.uqbar.prestamos.adapter.PrestamoAdapter;
import ar.edu.uqbar.prestamos.config.PrestamosAppBootstrap;
import ar.edu.uqbar.prestamos.config.PrestamosConfig;
import ar.edu.uqbar.prestamos.model.Prestamo;
import ar.edu.uqbar.prestamos.persistence.RepoPrestamos;

public class MainActivity extends Activity implements ActionMode.Callback, AdapterView.OnItemClickListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 200;

    // nuevo
    ActionMode mActionMode;
    RepoPrestamos repoPrestamos = PrestamosConfig.getRepoPrestamos(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestContactPermission();
        super.onCreate(savedInstanceState);
        PrestamosAppBootstrap.initialize(this);
        setContentView(R.layout.activity_main);
    }

    /***
     * A partir de la SDK 23 los permisos no se pueden manejar con
     * configuraciones en el AndroidManifest.xml, entonces hay que hacerlo programaticamente,
     * pidiendo acceso desde la app al usuario
     *
     * https://developer.android.com/training/permissions/requesting.html (el ejemplo usa App.Compat)
     * https://github.com/nilsorathiya/RuntimePermissionForAndroidMPlus
     */
    private void requestContactPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.llenarPrestamosPendientes();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.llenarPrestamosPendientes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void navigate(Class<?> classActivity) {
        Intent intent = new Intent(this, classActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.prestamo_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Log.w("Librex", "Item clicked");
        int posicion = Integer.parseInt(mActionMode.getTag().toString());
        Prestamo prestamo = repoPrestamos.getPrestamosPendientes().get(posicion);
        Log.w("Seleccionado", prestamo.toString());
        switch (item.getItemId()) {
            case R.id.action_call_contact:
                llamar(prestamo.getTelefono());
            case R.id.action_email_contact:
                enviarMail(prestamo);
            default:
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nuevo_prestamo: navigate(NuevoPrestamoActivity.class);
        }
        return true;
    }

    private boolean llamar(String telefono) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + telefono));
        try {
            Log.w("Voy a llamar", "Intent iniciado");
            startActivity(callIntent);
        } catch (Exception e) {
            Log.e("ERROR al llamar ", e.getMessage());
            return false;
        }
        return true;
    }

    private boolean enviarMail(Prestamo prestamo) {
        String uriText = "mailto:" + prestamo.getContactoMail() + "?subject=" +
                URLEncoder.encode("Libro " + prestamo.getLibro().getTitulo()) + "&body=" +
                URLEncoder.encode("Por favor te pido que me devuelvas el libro");
        Uri uri = Uri.parse(uriText);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uri);
        startActivity(Intent.createChooser(sendIntent, "Enviar mail"));
        return true;
    }

    private void llenarPrestamosPendientes() {
        ListView lvPrestamos = (ListView) findViewById(R.id.lvPrestamos);
        PrestamoAdapter prestamoAdapter = new PrestamoAdapter(this, repoPrestamos.getPrestamosPendientes());
        lvPrestamos.setAdapter(prestamoAdapter);
        lvPrestamos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // lv.multiChoiceModeListener = new PrestamoModeListener(this)
        //lvPrestamos.setLongClickable(true);
        lvPrestamos.setOnItemClickListener(this);
        lvPrestamos.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Log.w("Log prestamos", "" + position);
            if (mActionMode != null) {
                return false;
            }
            mActionMode = this.startActionMode(this);
            mActionMode.setTag(position);
            view.setSelected(true);
            return true;
        });
        registerForContextMenu(lvPrestamos);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.w("Aca", "Va");
    }
}
