package ar.edu.uqbar.prestamos.config;

import android.app.Activity;
import android.util.Log;

import ar.edu.uqbar.eg_prestamos_android_studio.MainActivity;
import ar.edu.uqbar.prestamos.model.Contacto;
import ar.edu.uqbar.prestamos.model.Libro;
import ar.edu.uqbar.prestamos.model.Prestamo;
import ar.edu.uqbar.prestamos.persistence.PhoneBasedContactos;
import ar.edu.uqbar.prestamos.persistence.RepoContactos;
import ar.edu.uqbar.prestamos.persistence.RepoLibros;
import ar.edu.uqbar.prestamos.persistence.RepoPrestamos;
import ar.edu.uqbar.prestamos.util.ImageUtil;

/**
 * Created by fernando on 10/28/16.
 */

public class PrestamosAppBootstrap {

    public static void initialize(MainActivity activity) {
        /**
         * inicializamos la información de la aplicación
         */
        RepoContactos repoContactos = new PhoneBasedContactos(activity);
        repoContactos.addContactoSiNoExiste(
                new Contacto("1", "46425829", "Chiara Dodino", "kiki.dodain@gmail.com", ImageUtil.convertToImage(activity, "kiarush.png")));
        repoContactos.addContactoSiNoExiste(
                new Contacto("2", "45387743", "Ornella Bordino", "ornelia@yahoo.com.ar", ImageUtil.convertToImage(activity, "ornella.jpg")));
        repoContactos.addContactoSiNoExiste(
                new Contacto("3", "47067261", "Federico Cano", "el_fede@gmail.com", ImageUtil.convertToImage(activity, "fedeCano.jpg")));
        repoContactos.addContactoSiNoExiste(
                new Contacto("4", "46050144", "Gisela Decuzzi", "shize_dekuuse@hotmail.com",
                        ImageUtil.convertToImage(activity, "gise.jpg")));
        repoContactos.addContactoSiNoExiste(
                new Contacto("5", "42040007", "Estefanía Miguel", "tefffffi@hotmail.com",
                        ImageUtil.convertToImage(activity, "tefi.jpg")));

        Libro elAleph = new Libro(1, "El Aleph", "J.L. Borges");
        elAleph.prestar();
        Libro laNovelaDePeron = new Libro(2, "La novela de Perón", "T.E. Martínez");
        laNovelaDePeron.prestar();
        Libro cartasMarcadas = new Libro(3, "Cartas marcadas", "A. Dolina");
        cartasMarcadas.prestar();

        RepoLibros repoLibros = PrestamosConfig.getRepoLibros(activity);

        // Cuando necesitemos generar una lista nueva de libros
        // homeDeLibros.eliminarLibros()
        elAleph = repoLibros.addLibroSiNoExiste(elAleph);
        laNovelaDePeron = repoLibros.addLibroSiNoExiste(laNovelaDePeron);
        cartasMarcadas = repoLibros.addLibroSiNoExiste(cartasMarcadas);
        repoLibros.addLibroSiNoExiste(new Libro(4, "Rayuela", "J. Cortázar"));
        repoLibros.addLibroSiNoExiste(new Libro(5, "No habrá más penas ni olvido", "O. Soriano"));
        repoLibros.addLibroSiNoExiste(new Libro(6, "La invención de Morel", "A. Bioy Casares"));
        repoLibros.addLibroSiNoExiste(new Libro(7, "Cuentos de los años felices", "O. Soriano"));
        repoLibros.addLibroSiNoExiste(new Libro(8, "Una sombra ya pronto serás", "O. Soriano"));
        repoLibros.addLibroSiNoExiste(new Libro(9, "Octaedro", "J. Cortázar"));
        repoLibros.addLibroSiNoExiste(new Libro(10, "Ficciones", "J.L. Borges"));

        Contacto gise = new Contacto(null, "46050144", null, null, null);
        Contacto fede = new Contacto(null, "47067261", null, null, null);
        Contacto orne = new Contacto(null, null, "Ornella Bordino", null, null);

        RepoPrestamos repoPrestamos = PrestamosConfig.getRepoPrestamos(activity);
        if (repoPrestamos.getPrestamosPendientes().isEmpty()) {
            Log.w("Librex", "Creando préstamos");
            repoPrestamos.addPrestamo(new Prestamo(1L, repoContactos.getContacto(fede), elAleph));
            repoPrestamos.addPrestamo(new Prestamo(2L, repoContactos.getContacto(gise), laNovelaDePeron));
            repoPrestamos.addPrestamo(new Prestamo(3L, repoContactos.getContacto(orne), cartasMarcadas));
        }
    }
}
