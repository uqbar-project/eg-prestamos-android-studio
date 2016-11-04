package ar.edu.uqbar.prestamos.persistence;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uqbar.prestamos.config.PrestamosConfig;
import ar.edu.uqbar.prestamos.model.Contacto;
import ar.edu.uqbar.prestamos.model.Libro;
import ar.edu.uqbar.prestamos.model.Prestamo;
import ar.edu.uqbar.prestamos.util.DateUtil;

/**
 * Created by fernando on 10/28/16.
 */

public class SQLLiteRepoPrestamos implements RepoPrestamos {

    public static String TABLA_PRESTAMOS = "Prestamos";
    public static String[] CAMPOS_PRESTAMO = new String[]{"id, fecha, fecha_devolucion, contacto_phone, libro_id"};

    PrestamosAppSQLLiteHelper db;
    Activity activity;

    public SQLLiteRepoPrestamos(Activity activity) {
        db = PrestamosAppSQLLiteHelper.getInstance(activity);
        this.activity = activity;
    }

    @Override
    public List<Prestamo> getPrestamosPendientes() {
        return getPrestamos("fecha_devolucion is null", null);
    }

    @Override
    public Prestamo getPrestamo(Long id) {
        List<Prestamo> prestamos = getPrestamos("id", new String[] {id.toString()});
        if (prestamos.isEmpty()) {
            return null;
        }
        return prestamos.get(0);
    }

    @Override
    public void addPrestamo(Prestamo prestamo) {
        SQLiteDatabase con = db.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            Long idPrestamo = prestamo.getId();
            if (idPrestamo == null) {
                idPrestamo = getMaxIdPrestamo();
                prestamo.setId(idPrestamo);
            }
            values.put("id", idPrestamo);
            Log.w("Crear prestamo", idPrestamo.toString());
            values.put("libro_id", prestamo.getLibro().getId());
            values.put("contacto_phone", prestamo.getTelefono());
            // uso de extension methods de DateUtil
            values.put("fecha", DateUtil.asString(prestamo.getFechaPrestamo()));
            if (prestamo.estaPendiente()) {
                values.put("fecha_devolucion", (String) null);
            } else {
                values.put("fecha_devolucion", prestamo.getFechaDevolucion().toString());
            }
            con.insert(TABLA_PRESTAMOS, null, values);
            Log.w("Librex", "Se creó préstamo " + prestamo + " en SQLite");
        } finally {
            // Intuitivamente deberíamos cerrar la conexión, no obstante, esto cierra también
            // la base de datos, así que no debemos hacer eso
            // :)
            //if (con != null) con.close();
        }
    }

    @Override
    public void removePrestamo(Prestamo prestamo) {
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            con.delete(TABLA_PRESTAMOS, "ID = ? ", new String[]{"" + prestamo.getId()});
        } finally {
            //if (con != null) con.close();
        }
    }

    @Override
    public void updatePrestamo(Prestamo prestamo) {
        this.removePrestamo(prestamo);
        this.addPrestamo(prestamo);
    }

    private Prestamo crearPrestamo(Cursor cursor) {
        Contacto contactoBuscar = new Contacto();
        Long idPrestamo = new Long(cursor.getInt(cursor.getColumnIndex("ID")));
        int idLibro = cursor.getInt(cursor.getColumnIndex("LIBRO_ID"));
        String numeroTelContacto = cursor.getString(cursor.getColumnIndex("CONTACTO_PHONE"));
        String fecha = cursor.getString(cursor.getColumnIndex("FECHA"));
        String fechaDevolucion = cursor.getString(cursor.getColumnIndex("FECHA_DEVOLUCION"));
        contactoBuscar.setNumero(numeroTelContacto);
        Contacto contacto = PrestamosConfig.getRepoContactos(activity).getContacto(contactoBuscar);
        Libro libro = PrestamosConfig.getRepoLibros(activity).getLibro(idLibro);
        Prestamo prestamo = new Prestamo(idPrestamo, contacto, libro);
        prestamo.setFechaPrestamo(DateUtil.asDate(fecha));
        if (fechaDevolucion != null) {
            prestamo.setFechaDevolucion(DateUtil.asDate(fechaDevolucion));
        }
        return prestamo;
    }

    public Long getMaxIdPrestamo() {
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            Cursor curPrestamos = con.rawQuery("select MAX(ID) AS MAX_ID FROM " + TABLA_PRESTAMOS, null);
            Long idMax = 0L;
            if (curPrestamos.moveToFirst()) {
                idMax = curPrestamos.getLong(curPrestamos.getColumnIndex("MAX_ID"));
            }
            curPrestamos.close();
            return idMax + 1;
        } finally {
            //if (con != null) con.close();
        }
    }

    /***********************************************************************
     * METODOS INTERNOS
     ***********************************************************************
     */
    private List<Prestamo> getPrestamos(String campos, String[] valores) {
        List<Prestamo> result = new ArrayList<Prestamo>();
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            Cursor curPrestamos = con.query(TABLA_PRESTAMOS, CAMPOS_PRESTAMO, campos, valores, null, null, null);
            while (curPrestamos.moveToNext()) {
                result.add(crearPrestamo(curPrestamos));
            }
            curPrestamos.close();
            Log.w("Librex", "Préstamos pendientes " + result);
            return result;
        } finally {
            //if (con != null) con.close();
        }
    }

}
