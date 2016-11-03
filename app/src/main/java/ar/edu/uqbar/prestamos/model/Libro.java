package ar.edu.uqbar.prestamos.model;

import java.io.Serializable;

/**
 * Representa un libro, un documento que puede ser prestado a un contacto.
 * Created by fernando on 10/27/16.
 */

public class Libro implements Serializable {
    /*****************************************************
     * Atributos
     ****************************************************/
    Long id;
    String titulo;
    String autor;
    boolean prestado;

    /*****************************************************
     * Getters & Setters
     ****************************************************/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public boolean isPrestado() {
        return prestado;
    }

    public void setPrestado(boolean prestado) {
        this.prestado = prestado;
    }

    /*****************************************************
     * Constructores
     ****************************************************/
    public Libro() {
        initialize();
    }

    public Libro(Long id, String titulo, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
    }

    public Libro(int id, String titulo, String autor) {
        this(new Long(id), titulo, autor);
    }

    /*****************************************************
     * Negocio
     ****************************************************/
    public void initialize() {
        prestado = false;
    }

    @Override
    public String toString() {
        return titulo + " (" + autor + ")";
    }

    public void prestar() {
        prestado = true;
    }

    public void devolver() {
        prestado = false;
    }

    public boolean estaPrestado() {
        return prestado;
    }

    public boolean estaDisponible() {
        return !prestado;
    }
}
