package ar.edu.uqbar.prestamos.persistence;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ar.edu.uqbar.prestamos.model.Libro;

/**
 * Created by fernando on 10/28/16.
 */

public class SQLLiteRepoLibros implements RepoLibros {

    private String[] CAMPOS_LIBRO = new String[]{"titulo, autor, prestado, id"};

    PrestamosAppSQLLiteHelper db;

    public SQLLiteRepoLibros(Activity activity) {
        db = PrestamosAppSQLLiteHelper.getInstance(activity);
    }

    @Override
    public void addLibro(Libro libro) {
        SQLiteDatabase con = db.getWritableDatabase();
        try {
            int prestado = 0;
            if (libro.estaPrestado()) {
                prestado = 1;
            }
            ContentValues values = new ContentValues();
            values.put("id", libro.getId());
            values.put("titulo", libro.getTitulo());
            values.put("autor", libro.getAutor());
            values.put("prestado", prestado);

            con.insert("Libros", null, values);
            Log.w("Librex", "Se creó libro " + libro);
        } finally {
            //if (con != null) con.close();
        }
    }

    @Override
    public Libro addLibroSiNoExiste(Libro libro) {
        Libro libroPosta = this.getLibro(libro);
        if (libroPosta != null) return libroPosta;
        this.addLibro(libro);
        return libro;
    }

    @Override
    public List<Libro> getLibros() {
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            List<Libro> result = new ArrayList<Libro>();

            Cursor curLibros = con.query("Libros", CAMPOS_LIBRO, null, null, null, null, null);
            while (curLibros.moveToNext()) {
                result.add(crearLibro(curLibros));
            }
            Log.w("Librex", "getLibros | result: " + result);
            return result;
        } finally {
            //if (con != null) con.close();
        }
    }

    @Override
    public Libro getLibro(Libro libroOrigen) {
        return libroPor("titulo = ? ", new String[]{libroOrigen.getTitulo()});
    }

    @Override
    public Libro getLibro(int posicion) {
        return libroPor("id = ? ", new String[] {"" + posicion});
    }

    @Override
    public List<Libro> getLibrosPrestables() {
        return internalGetLibro((SQLiteDatabase con) -> con.query("Libros", CAMPOS_LIBRO, "prestado = ? ", new String[] {"0"}, null, null, null));
    }

    @Override
    public void removeLibro(Libro libro) {
        borrarLibros("id = ? ", new String[]{"" + libro.getId()});
    }

    @Override
    public void updateLibro(Libro libro) {
        this.removeLibro(libro);
        this.addLibro(libro);
    }

    @Override
    public void removeLibro(int posicion) {
        borrarLibros("id = ? ", new String[]{"" + posicion + 1});
    }

    @Override
    public void eliminarLibros() {
        borrarLibros(null, null);
    }

    private Libro crearLibro(Cursor cursor) {
        Long id = new Long(cursor.getInt(cursor.getColumnIndex("ID")));
        String titulo = cursor.getString(cursor.getColumnIndex("TITULO"));
        String autor = cursor.getString(cursor.getColumnIndex("AUTOR"));
        int prestado = cursor.getInt(cursor.getColumnIndex("PRESTADO"));
        Libro libro = new Libro(id, titulo, autor);
        if (prestado == 1) {
            libro.prestar();
        }
        Log.w("Librex", "Traigo un libro de SQLite a memoria | id: " + libro.getId() + " | libro: " + libro);
        return libro;
    }

    /***********************************************************************
     * METODOS PRIVADOS
     * *********************************************************************
     */
    /**
     * Abstrae una busqueda general de un libro en base a diferentes criterios
     */
    private Libro libroPor(String campo, String[] condicion) {
        List<Libro> libros = internalGetLibro((SQLiteDatabase con) -> con.query("Libros", CAMPOS_LIBRO, campo, condicion, null, null, null));
        Optional<Libro> libro = libros.stream().findFirst();
        if (!libro.isPresent()) {
            return null;
        }
        return libro.get();
    }

    /**
     * Abstrae una busqueda general de varios libros en base a un query que se pasa como Closure
     */
    private List<Libro> internalGetLibro(Function<SQLiteDatabase, Cursor> query) {
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            List<Libro> result = new ArrayList<Libro>();
            Cursor curLibros = query.apply(con);
            while (curLibros.moveToNext()) {
                result.add(crearLibro(curLibros));
            }
            return result;
        } finally {
            //if (con != null) con.close();
        }
    }

    private void borrarLibros(String campo, String[] valores) {
        SQLiteDatabase con = db.getWritableDatabase();
        try {
            con.delete("Libros", campo, valores);
        } finally {
            //if (con != null) con.close();
        }
    }

}
