package ar.edu.uqbar.prestamos.model;

/**
 * Representa un contacto dentro de la aplicación de préstamos de libros
 * Adapta el Contact propio de la API de ContactProvider de Android
 * Created by fernando on 10/27/16.
 */

public class Contacto {
    /*****************************************************
     * Atributos
     ****************************************************/
    String id;
    String nombre;
    String numero;
    String email;
    byte[] foto;

    /*****************************************************
     * Getters & Setters
     ****************************************************/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    /*****************************************************
     * Constructores
     ****************************************************/
    public Contacto() {}

    public Contacto(String _id) { id = _id; }

    public Contacto(String _id, String _numero, String _nombre, String _email, byte[] _foto) {
        id = _id;
        numero = _numero;
        nombre = _nombre;
        email = _email;
        foto = _foto;
    }

    /*****************************************************
     * Negocio
     ****************************************************/
    @Override
    public String toString() {
        return nombre;
    }

}
