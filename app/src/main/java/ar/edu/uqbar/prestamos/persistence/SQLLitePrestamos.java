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

public class SQLLitePrestamos implements RepoPrestamos {

    public static String TABLA_PRESTAMOS = "Prestamos";
    public static String[] CAMPOS_PRESTAMO = new String[]{"id, fecha, fecha_devolucion, contacto_phone, libro_id"};

    PrestamosAppSQLLiteHelper db;
    Activity activity;

    public SQLLitePrestamos(Activity activity) {
        db = new PrestamosAppSQLLiteHelper(activity);
        this.activity = activity;
    }

    @Override
    public List<Prestamo> getPrestamosPendientes() {
        List<Prestamo> result = new ArrayList<Prestamo>();
        SQLiteDatabase con = db.getReadableDatabase();

        Cursor curPrestamos = con.query(TABLA_PRESTAMOS, CAMPOS_PRESTAMO, null, null, null, null, null);
        while (curPrestamos.moveToNext()) {
            result.add(crearPrestamo(curPrestamos));
        }
        con.close();
        Log.w("Librex", "Préstamos pendientes " + result);
        return result;
    }

    @Override
    public Prestamo getPrestamo(Long id) {
        return null;
    }

    @Override
    public void addPrestamo(Prestamo prestamo) {
        SQLiteDatabase con = db.getWritableDatabase();

        ContentValues values = new ContentValues();
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
        con.close();
        Log.w("Librex", "Se creó préstamo " + prestamo + " en SQLite");
    }

    @Override
    public void removePrestamo(Prestamo prestamo) {
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            con.delete(TABLA_PRESTAMOS, "id = ? ", new String[] {"" + prestamo.getId()});
        } finally {
            con.close();
        }
    }

    private Prestamo crearPrestamo(Cursor cursor) {
        Contacto contactoBuscar = new Contacto();
        contactoBuscar.setNumero(cursor.getString(3));
        Contacto contacto = PrestamosConfig.getRepoContactos(activity).getContacto(contactoBuscar);
        int idLibro = cursor.getInt(4);
        Log.w("Librex", "idLibro " + idLibro);
        Libro libro = PrestamosConfig.getRepoLibros(activity).getLibro(idLibro);
        Log.w("Librex", "libro " + libro);
        Prestamo prestamo = new Prestamo(cursor.getLong(0), contacto, libro);
        // extension method toDate de DateUtil
        prestamo.setFechaPrestamo(DateUtil.asDate(cursor.getString(1)));
        if (cursor.getString(2) != null) {
            prestamo.setFechaDevolucion(DateUtil.asDate(cursor.getString(2)));
        }
        Log.w("Librex", "genero un prestamo en memoria | " + prestamo);
        return prestamo;
    }
}
