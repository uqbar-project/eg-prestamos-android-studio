package ar.edu.uqbar.prestamos.config;

import android.app.Activity;

import ar.edu.uqbar.prestamos.persistence.CollectionBasedPrestamos;
import ar.edu.uqbar.prestamos.persistence.CollectionBasedRepoLibros;
import ar.edu.uqbar.prestamos.persistence.PhoneBasedContactos;
import ar.edu.uqbar.prestamos.persistence.RepoContactos;
import ar.edu.uqbar.prestamos.persistence.RepoLibros;
import ar.edu.uqbar.prestamos.persistence.RepoPrestamos;
import ar.edu.uqbar.prestamos.persistence.SQLLiteRepoLibros;
import ar.edu.uqbar.prestamos.persistence.SQLLiteRepoPrestamos;

/**
 * Created by fernando on 10/27/16.
 */

public class PrestamosConfig {

    public static RepoLibros getRepoLibros(Activity activity) {
        // PERSISTENTE
        //return new SQLLiteRepoLibros(activity);
        // NO PERSISTENTE
        return CollectionBasedRepoLibros.getInstance();
    }

    public static RepoPrestamos getRepoPrestamos(Activity activity) {
        // PERSISTENTE
        //return new SQLLiteRepoPrestamos(activity);
        // NO PERSISTENTE
        return CollectionBasedPrestamos.getInstance();
    }

    public static RepoContactos getRepoContactos(Activity activity) {
        return new PhoneBasedContactos(activity);
    }

}
