package ar.edu.uqbar.prestamos.model;

/**
 * Created by fernando on 10/28/16.
 */
public class ProgramException extends RuntimeException {

    public ProgramException(String msg, Exception cause) {
        super(msg, cause);
    }

}
