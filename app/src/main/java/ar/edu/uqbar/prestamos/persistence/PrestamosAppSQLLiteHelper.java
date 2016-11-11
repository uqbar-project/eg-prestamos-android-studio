package ar.edu.uqbar.prestamos.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by fernando on 10/28/16.
 */

public class PrestamosAppSQLLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "librex.db";
    private static final int DATABASE_VERSION = 15;

    private PrestamosAppSQLLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Definicion del Singleton **/
    static PrestamosAppSQLLiteHelper instance = null;

    public static PrestamosAppSQLLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PrestamosAppSQLLiteHelper(context);
        }
        return instance;
    }

    /**
     * Script para iniciar la base
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer crearTablas = new StringBuffer();
        crearTablas.append("CREATE TABLE Libros (ID INTEGER PRIMARY KEY AUTOINCREMENT,");
        crearTablas.append(" TITULO TEXT NOT NULL,");
        crearTablas.append(" AUTOR TEXT NOT NULL,");
        crearTablas.append(" PRESTADO INTEGER NOT NULL);");
        db.execSQL(crearTablas.toString());

        crearTablas = new StringBuffer();
        crearTablas.append("CREATE TABLE Prestamos (ID INTEGER PRIMARY KEY AUTOINCREMENT,");
        crearTablas.append(" LIBRO_ID INTEGER NOT NULL,");
        crearTablas.append(" CONTACTO_PHONE TEXT NOT NULL,");
        crearTablas.append(" FECHA TEXT NOT NULL,");
        crearTablas.append(" FECHA_DEVOLUCION TEXT NULL);");
        db.execSQL(crearTablas.toString());

    }

    /**
     * Estrategia para migrar de una version a otra
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Libros; ");
        db.execSQL("DROP TABLE IF EXISTS Prestamos; ");
        onCreate(db);
    }

}
