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
        db = new PrestamosAppSQLLiteHelper(activity);
    }

    @Override
    public void addLibro(Libro libro) {
        SQLiteDatabase con = db.getWritableDatabase();

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
        con.close();
        Log.w("Librex", "Se creó libro " + libro);
    }

    @Override
    public Libro addLibroSiNoExiste(Libro libro) {
        if (this.getLibro(libro) == null) {
            this.addLibro(libro);
        }
        return libro;
    }

    @Override
    public List<Libro> getLibros() {
        List<Libro> result = new ArrayList<Libro>();
        SQLiteDatabase con = db.getReadableDatabase();

        Cursor curLibros = con.query("Libros", CAMPOS_LIBRO, null, null, null, null, null);
        while (curLibros.moveToNext()) {
            result.add(crearLibro(curLibros));
        }
        con.close();
        Log.w("Librex", "getLibros | result: " + result);
        return result;
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
        Long id = cursor.getLong(0);
        String titulo = cursor.getString(1);
        String autor = cursor.getString(2);
        int prestado = cursor.getInt(3);
        Libro libro = new Libro(id, titulo, autor);
        if (prestado == 1) {
            libro.prestar();
        }
        Log.w("Librex", "genero un libro en memoria | id: " + libro.getId() + " | libro: " + libro);
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
        List<Libro> result = new ArrayList<Libro>();
        SQLiteDatabase con = db.getReadableDatabase();
        try {
            Cursor curLibros = query.apply(con);
            while (curLibros.moveToNext()) {
                result.add(crearLibro(curLibros));
            }
            return result;
        } finally {
            con.close();
        }
    }

    private void borrarLibros(String campo, String[] valores) {
        SQLiteDatabase con = db.getWritableDatabase();
        try {
            con.delete("Libros", campo, valores);
        } finally {
            con.close();
        }
    }

}
