package ar.edu.uqbar.prestamos.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ar.edu.uqbar.prestamos.model.Contacto;

/**
 * Created by fernando on 10/27/16.
 */
public class ImageUtil {

    static String DEFAULT_CONTACT_URI = "defaultContact.png";

    /**
     * Si la imagen es de un proyecto debe estar en el directorio assets (o bien un directorio ubicable)
     */
    public static byte[] convertToImage(Activity activity, String path) {
        try {
            InputStream inputFoto = activity.getAssets().open(path);
            return convertToByteArray(inputFoto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] convertToByteArray(InputStream inputFoto) {
        Bitmap bmpFoto = BitmapFactory.decodeStream(inputFoto);
        ByteArrayOutputStream foto = new ByteArrayOutputStream();
        bmpFoto.compress(Bitmap.CompressFormat.PNG, 100, foto);
        return foto.toByteArray();
    }

    /**
     * Si la imagen es de un proyecto debe estar en el directorio assets (o bien un directorio ubicable)
     */
    public static byte[] convertToImage(Activity activity, Uri uri) {
        InputStream fotoStream = ContactsContract.Contacts.openContactPhotoInputStream(activity.getContentResolver(), uri);
        if (fotoStream == null) {
            Log.w("Librex", "Esta URI no fue encontrada: " + uri);
            return convertToImage(activity, DEFAULT_CONTACT_URI);
        }
        BufferedInputStream inputFoto = new BufferedInputStream(fotoStream);
        return convertToByteArray(inputFoto);
    }


    /** Gracias a https://inducesmile.com/android/how-to-make-circular-imageview-and-rounded-corner-imageview-in-android/ */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void assignImage(Contacto contacto, ImageView imgContacto) {
        byte[] fotoContacto = contacto.getFoto();
        Bitmap bm = BitmapFactory.decodeByteArray(fotoContacto, 0, fotoContacto.length);
        imgContacto.setImageBitmap(getRoundedCornerBitmap(bm, 50));
    }

}
