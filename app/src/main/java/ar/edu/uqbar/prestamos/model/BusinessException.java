package ar.edu.uqbar.prestamos.model;

import java.io.UnsupportedEncodingException;

/**
 * Created by fernando on 10/27/16.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String msg) {
        super(msg);
    }

}
