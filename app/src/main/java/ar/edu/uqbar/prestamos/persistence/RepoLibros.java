package ar.edu.uqbar.prestamos.persistence;

import java.util.List;

import ar.edu.uqbar.prestamos.model.Libro;

/**
 *
 * Interfaz del objeto que maneja el origen de datos de los libros
 *
 * Created by fernando on 10/27/16.
 */

public interface RepoLibros {

    public void addLibro(Libro libro);
    public Libro addLibroSiNoExiste(Libro libro);
    public List<Libro> getLibros();
    public Libro getLibro(Libro libroOrigen);
    public Libro getLibro(int posicion);
    public List<Libro> getLibrosPrestables();
    public void removeLibro(Libro libro);
    public void updateLibro(Libro libro);
    public void removeLibro(int posicion);
    public void eliminarLibros();

}
