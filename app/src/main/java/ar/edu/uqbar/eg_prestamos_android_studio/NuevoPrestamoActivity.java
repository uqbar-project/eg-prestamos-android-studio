package ar.edu.uqbar.eg_prestamos_android_studio;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.uqbar.prestamos.config.PrestamosConfig;
import ar.edu.uqbar.prestamos.model.BusinessException;
import ar.edu.uqbar.prestamos.model.Contacto;
import ar.edu.uqbar.prestamos.model.Libro;
import ar.edu.uqbar.prestamos.model.Prestamo;
import ar.edu.uqbar.prestamos.persistence.RepoLibros;
import ar.edu.uqbar.prestamos.persistence.RepoPrestamos;
import ar.edu.uqbar.prestamos.util.ImageUtil;

public class NuevoPrestamoActivity extends Activity implements TextWatcher {

    public static final int PICK_CONTACT = 1;

    AutoCompleteTextView txtLibroAutocomplete;
    Map<String, Libro> mapaLibros = new HashMap<String, Libro>();
    Libro libroSeleccionado;
    Contacto contacto;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nuevo_prestamo, menu);
        List<Libro> libros = PrestamosConfig.getRepoLibros(this).getLibrosPrestables();
        for (Libro libro: libros) {
            mapaLibros.put(libro.toString(), libro);
        }
        txtLibroAutocomplete = (AutoCompleteTextView) findViewById(R.id.txtLibroTituloAutocomplete);
        txtLibroAutocomplete.setAdapter(new ArrayAdapter<Libro>(this, android.R.layout.simple_dropdown_item_1line, libros));
        txtLibroAutocomplete.addTextChangedListener(this);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_prestamo);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    seleccionarContacto(data);
                }
        }
    }

    /************************************************************************
     * Metodos requeridos por TextWatcher
     * ***********************************************************************
     */

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        libroSeleccionado = mapaLibros.get(txtLibroAutocomplete.getText().toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * Click del boton Buscar contacto
     */
    public void buscarContacto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    /**
     * Click del boton Prestar
     */
    public void prestar(View view) {
        try {
            Prestamo prestamo = new Prestamo();
            prestamo.setLibro(libroSeleccionado);
            prestamo.setContacto(contacto);
            prestamo.prestar();
            getRepoPrestamos().addPrestamo(prestamo);
            getRepoLibros().updateLibro(libroSeleccionado);
            this.finish();
        } catch (BusinessException be) {
            Toast.makeText(this, be.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Crear prestamo", e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Ocurrió un error. Consulte con el administrador del sistema.", Toast.LENGTH_SHORT).show();
        }
    }

    /************************************************************************
     * METODOS PRIVADOS
     * ***********************************************************************
     */
    private void seleccionarContacto(Intent data) {
        CursorLoader loader = new CursorLoader(this, data.getData(), null, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            Contacto contactoBuscar = new Contacto();
            contactoBuscar.setNombre(name);
            // http://developer.android.com/reference/android/os/StrictMode.html
            contacto = PrestamosConfig.getRepoContactos(this).getContacto(contactoBuscar);
            TextView txtContacto = (TextView) findViewById(R.id.txtContacto);
            txtContacto.setText(contacto.getNombre());
            ImageView imgContacto = (ImageView) findViewById(R.id.imgContactoAPrestar);
            ImageUtil.assignImage(contacto, imgContacto);
        }
    }

    public RepoPrestamos getRepoPrestamos() {
        return PrestamosConfig.getRepoPrestamos(this);
    }
    public RepoLibros getRepoLibros() {
        return PrestamosConfig.getRepoLibros(this);
    }
}
