package ar.edu.uqbar.prestamos.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Representa el préstamo de un libro a un contacto
 *
 * Created by fernando on 10/27/16.
 */

public class Prestamo {
    /*****************************************************
     * Atributos
     ****************************************************/
    Long id;
    Date fechaPrestamo;
    Date fechaDevolucion;
    Contacto contacto;
    Libro libro;

    /*****************************************************
     * Getters & Setters
     ****************************************************/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    /*****************************************************
     * Constructores
     ****************************************************/
    public Prestamo() {
    }

    public Prestamo(Long id, Contacto contacto, Libro libro) {
        this.id = id;
        this.fechaPrestamo = new Date();
        this.contacto = contacto;
        this.libro = libro;
    }

    /*****************************************************
     * Negocio
     ****************************************************/
    public boolean estaPendiente() {
        return fechaDevolucion == null;
    }

    @Override
    public String toString() {
        return libro.toString() + " - " + this.getDatosPrestamo();
    }

    public String getDatosPrestamo() {
        return new SimpleDateFormat("dd/MM/yyyy").format(fechaPrestamo) + " a " + contacto.toString();
    }

    public String getTelefono() {
        return contacto.getNumero();
    }

    public String getContactoMail() {
        return contacto.getEmail();
    }

    public void prestar() {
        if (libro == null) {
            throw new BusinessException("Debe ingresar libro");
        }
        if (contacto == null) {
            throw new BusinessException("Debe ingresar contacto");
        }
        fechaPrestamo = new Date();
        libro.prestar();
    }

    public void devolver() {
        libro.devolver();
        setFechaDevolucion(new Date());
    }
}
