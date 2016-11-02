package ar.edu.uqbar.prestamos.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import ar.edu.uqbar.eg_prestamos_android_studio.R;
import ar.edu.uqbar.prestamos.model.Prestamo;
import ar.edu.uqbar.prestamos.util.ImageUtil;

/**
 * Created by fernando on 11/1/16.
 */
public class PrestamoAdapter implements ListAdapter {

    List prestamosPendientes;
    Activity mainActivity;

    public PrestamoAdapter(Activity mainActivity, List<Prestamo> prestamosPendientes) {
        this.prestamosPendientes = prestamosPendientes;
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return prestamosPendientes.size();
    }

    @Override
    public Object getItem(int position) {
        return prestamosPendientes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Prestamo prestamo = (Prestamo) getItem(position);
        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.prestamo_row, parent, false);
        TextView lblLibro = (TextView) row.findViewById(R.id.txtLibro);
        TextView lblPrestamo = (TextView) row.findViewById(R.id.txtPrestamo);
        ImageView imgContacto = (ImageView) row.findViewById(R.id.imgContacto);
        lblLibro.setText(prestamo.getLibro().toString());
        lblPrestamo.setText(prestamo.getDatosPrestamo());
        ImageUtil.assignImage(prestamo.getContacto(), imgContacto);
        return row;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * Debe devolver los diferentes tipos de vista asociados a este adapter (en el caso de
     * querer reutilizarlos).
     *
     * http://stackoverflow.com/questions/24854861/java-lang-illegalargumentexception-cant-have-a-viewtypecount-1
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
