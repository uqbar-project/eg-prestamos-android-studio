package ar.edu.uqbar.prestamos.persistence;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ar.edu.uqbar.prestamos.model.Libro;

/**
 * Created by fernando on 10/27/16.
 */

public class CollectionBasedRepoLibros implements RepoLibros {

    /**
     * ******************************************************************************
     *   IMPLEMENTACION DEL SINGLETON
     * ******************************************************************************
     */
    static RepoLibros instance;

    public static RepoLibros getInstance() {
        if (instance == null) {
            instance = new CollectionBasedRepoLibros();
        }
        return instance;
    }

    /**
     * ******************************************************************************
     *   Atributos
     * ******************************************************************************
     */
    List<Libro> libros;

    /**
     * ******************************************************************************
     *   Constructores
     * ******************************************************************************
     */
    public CollectionBasedRepoLibros() {
        libros = new LinkedList<>();
    }

    /**
     * ******************************************************************************
     *   NEGOCIO
     * ******************************************************************************
     */

    @Override
    public void addLibro(Libro libro) {
        libros.add(libro);
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
        return libros;
    }

    @Override
    public Libro getLibro(Libro libroOrigen) {
        Optional<Libro> result = this.libros
                .stream()
                .filter((Libro libro) -> libro.getTitulo().equalsIgnoreCase(libroOrigen.getTitulo()))
                .findFirst();
                
        if (!result.isPresent()) return null;
        return result.get();
    }

    @Override
    public Libro getLibro(int posicion) {
        return libros.get(posicion);
    }

    @Override
    public List<Libro> getLibrosPrestables() {
        return this.libros
                .stream()
                .filter((Libro libro) -> libro.estaDisponible())
                .collect(Collectors.toList());
    }

    @Override
    public void removeLibro(Libro libro) {
        libros.remove(libro);
    }

    @Override
    public void updateLibro(Libro _libro) {
        this.libros.removeIf(libro -> libro.getId().equals(_libro.getId()));
        this.addLibro(_libro);
    }

    @Override
    public void removeLibro(int posicion) {
        libros.remove(posicion);
    }

    @Override
    public void eliminarLibros() {
        libros.clear();
    }

}
