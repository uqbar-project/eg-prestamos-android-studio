package ar.edu.uqbar.prestamos.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fernando on 10/27/16.
 */

public class DateUtil {

    public static String FORMATO = "dd/MM/yyyy";

    public static String asString(Date aDate) {
        return new SimpleDateFormat(FORMATO).format(aDate);
    }

    public static Date asDate(String aString) {
        try {
            return new SimpleDateFormat(FORMATO).parse(aString);
        } catch (ParseException e) {
            return null;
        }
    }

}
