package ar.edu.uqbar.prestamos.persistence;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ar.edu.uqbar.prestamos.model.Libro;
import ar.edu.uqbar.prestamos.model.Prestamo;

/**
 * Created by fernando on 10/28/16.
 */

public class CollectionBasedPrestamos implements RepoPrestamos {

    /**
     * ******************************************************************************
     *   IMPLEMENTACION DEL SINGLETON
     * ******************************************************************************
     */
    static RepoPrestamos instance;

    public static RepoPrestamos getInstance() {
        if (instance == null) {
            instance = new CollectionBasedPrestamos();
        }
        return instance;
    }

    /**
     * ******************************************************************************
     *   DEFINICION
     * ******************************************************************************
     */

    List<Prestamo> prestamos;

    private CollectionBasedPrestamos() {
        prestamos = new ArrayList<Prestamo>();
    }

    @Override
    public List<Prestamo> getPrestamosPendientes() {
        // Hay que configurar el proyecto para que utilice Jackson
        return this.prestamos
                .stream()
                .filter((Prestamo prestamo) -> prestamo.estaPendiente())
                .collect(Collectors.toList());

    }

    @Override
    public Prestamo getPrestamo(Long id) {
        Optional<Prestamo> result = this.prestamos
                .stream()
                .filter((Prestamo prestamo) -> prestamo.getId().equals(id))
                .findFirst();

        if (!result.isPresent()) return null;
        return result.get();
    }

    @Override
    public void addPrestamo(Prestamo prestamo) {
        prestamo.setId(new Long(prestamos.size() + 1));
        prestamos.add(prestamo);
    }

    @Override
    public void removePrestamo(Prestamo prestamo) {
        prestamos.remove(prestamo);
    }

    @Override
    public void updatePrestamo(Prestamo _prestamo) {
        this.prestamos.removeIf(prestamo ->
                prestamo.getId().equals(_prestamo.getId()));
        this.addPrestamo(_prestamo);
    }

}
