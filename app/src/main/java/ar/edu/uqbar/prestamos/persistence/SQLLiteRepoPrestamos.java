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
    private Long maxIdPrestamo;

    public SQLLiteRepoPrestamos(Activity activity) {
        db = new PrestamosAppSQLLiteHelper(activity);
        this.activity = activity;
    }

    @Override
    public List<Prestamo> getPrestamosPendientes() {
        List<Prestamo> result = new ArrayList<Prestamo>();
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            Cursor curPrestamos = con.query(TABLA_PRESTAMOS, CAMPOS_PRESTAMO, null, null, null, null, null);
            while (curPrestamos.moveToNext()) {
                result.add(crearPrestamo(curPrestamos));
            }
            curPrestamos.close();
            Log.w("Librex", "Préstamos pendientes " + result);
            return result;
        } finally {
            if (con != null) con.close();
        }
    }

    @Override
    public Prestamo getPrestamo(Long id) {
        return null;
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
            if (con != null) con.close();
        }
    }

    @Override
    public void removePrestamo(Prestamo prestamo) {
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            con.delete(TABLA_PRESTAMOS, "id = ? ", new String[]{"" + prestamo.getId()});
        } finally {
            if (con != null) con.close();
        }
    }

    @Override
    public void updatePrestamo(Prestamo prestamo) {
        this.removePrestamo(prestamo);
        this.addPrestamo(prestamo);
    }

    private Prestamo crearPrestamo(Cursor cursor) {
        Contacto contactoBuscar = new Contacto();
        Long idPrestamo = cursor.getLong(0);
        int idLibro = cursor.getInt(1);
        String numeroTelContacto = cursor.getString(2);
        String fecha = cursor.getString(3);
        String fechaDevolucion = cursor.getString(4);
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
            Log.w("Existe", "Verifico id de Prestamo");
            Cursor curPrestamos = con.rawQuery("select MAX(id) FROM " + TABLA_PRESTAMOS, null);
            Long idMax = 0L;
            if (curPrestamos.moveToFirst()) {
                Log.w("Existe", "Si existe y es " + curPrestamos.getLong(0));
                idMax = curPrestamos.getLong(0) + 1;
            }
            curPrestamos.close();
            return idMax;
        } finally {
            if (con != null) con.close();
        }
    }
}
