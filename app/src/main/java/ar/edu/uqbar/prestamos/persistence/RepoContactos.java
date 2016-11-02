package ar.edu.uqbar.prestamos.persistence;

import java.util.List;

import ar.edu.uqbar.prestamos.model.Contacto;

/**
 * Created by fernando on 10/28/16.
 */

public interface RepoContactos {
    
    public void addContactoSiNoExiste(Contacto contacto);
    public void addContacto(Contacto contacto);
    public List<Contacto> getContactos();
    public Contacto getContacto(Contacto contactoOrigen);
    public void eliminarContactos();

}
