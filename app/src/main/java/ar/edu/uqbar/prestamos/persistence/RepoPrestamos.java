package ar.edu.uqbar.prestamos.persistence;

import java.util.List;

import ar.edu.uqbar.prestamos.model.Prestamo;

/**
 * Created by fernando on 10/28/16.
 */

public interface RepoPrestamos {

    public List<Prestamo> getPrestamosPendientes();
    public Prestamo getPrestamo(Long id);
    public void addPrestamo(Prestamo prestamo);
    public void removePrestamo(Prestamo prestamo);
    public void updatePrestamo(Prestamo prestamo);

}
