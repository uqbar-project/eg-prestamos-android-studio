package ar.edu.uqbar.prestamos.persistence;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uqbar.prestamos.model.Contacto;
import ar.edu.uqbar.prestamos.model.ProgramException;
import ar.edu.uqbar.prestamos.util.ImageUtil;

/**
 * Created by fernando on 10/28/16.
 */

public class PhoneBasedContactos implements RepoContactos {

    /**
     * actividad (página) madre que permite hacer consultas sobre los contactos
     */
    Activity parentActivity;

    public PhoneBasedContactos(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void addContactoSiNoExiste(Contacto contacto) {
        if (this.getContacto(contacto) == null) {
            this.addContacto(contacto);
        }
    }

    @Override
    public void addContacto(Contacto contacto) {
        String tipoCuenta = null;
        String nombreCuenta = null;

        /** CON BUILDERS */
        ArrayList<ContentProviderOperation> comandosAgregar = new ArrayList<ContentProviderOperation>();
        // Malisimo que obligue a definirlo como ArrayList
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, tipoCuenta)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, nombreCuenta)
                .build()
        );
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contacto.getNombre())
                .build()
        );
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contacto.getNumero())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build()
        );
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contacto.getEmail())
                .build()
        );
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, contacto.getFoto())
                .build()
        );
        try {
            parentActivity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, comandosAgregar);
        } catch (Exception e) {
            throw new ProgramException("No se pudo generar la información del contacto " + contacto.getNombre(), e);
        }
    }

    @Override
    public List<Contacto> getContactos() {
        Cursor cursorContactos = parentActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursorContactos == null || cursorContactos.getCount() < 1) {
            return null;
        }

        List<Contacto> contactos = new ArrayList<Contacto>();
        cursorContactos.moveToFirst();
        while (!cursorContactos.isAfterLast()) {
            contactos.add(this.crearContacto(cursorContactos));
            cursorContactos.moveToNext();
        }

        cursorContactos.close();
        cursorContactos = null;

        return contactos;
    }

    @Override
    public Contacto getContacto(Contacto contactoOrigen) {
        // si queremos buscar por nombre
        //var cursorContactos = parentActivity.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Data.DISPLAY_NAME + " = ?", #[contactoOrigen.nombre], null)
        Uri lookupUri = null;
        if (contactoOrigen.getNumero() != null) {
            lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactoOrigen.getNumero()));
        } else {
            lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, contactoOrigen.getNombre());
        }

        Cursor cursorContactos = parentActivity.getContentResolver().query(lookupUri, null, null, null, null);
        if (cursorContactos == null || cursorContactos.getCount() < 1) {
            return null;
        }

        cursorContactos.moveToFirst();
        Contacto contacto = this.crearContacto(cursorContactos);
        cursorContactos.close();
        cursorContactos = null;
        return contacto;
    }

    @Override
    public void eliminarContactos() {
        ContentResolver contentResolver = parentActivity.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String clave = getDataAsString(cursor, ContactsContract.Contacts.LOOKUP_KEY);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, clave);
            contentResolver.delete(uri, null, null);
        }
    }


    /**
     * ***********************************************************************
     *     					METODOS INTERNOS
     * ***********************************************************************
     */
    /**
     * Extension method
     *
     * Facilita traer el dato de un cursor como un String
     */
    private String getDataAsString(Cursor cursor, String index) {
        return cursor.getString(cursor.getColumnIndex(index));
    }

    /**
     * Método de uso interno.
     * Permite generar un objeto de dominio Contacto a partir de un cursor de ContactsContract.Contacts,
     * la API estándar de Android para manejar contactos del dispositivo.
     */
    private Contacto crearContacto(Cursor cursorContactos) {
        String contactId = getDataAsString(cursorContactos, ContactsContract.Contacts._ID);
        String contactName = getDataAsString(cursorContactos, ContactsContract.Contacts.DISPLAY_NAME);
        String contactNumber = null;
        byte[] foto = null;
        String email = ""; // TODO: Agregarlo

        final ContentResolver contentResolver = parentActivity.getContentResolver();
        Cursor cursorTelefono = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
        if (getDataAsString(cursorContactos, ContactsContract.Contacts.HAS_PHONE_NUMBER).equals("1")) {
            if (cursorTelefono.moveToNext()) {
                contactNumber = getDataAsString(cursorTelefono, ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
        }
        Cursor cursorMail = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{contactId}, null);
        if (cursorMail.moveToNext()) {
            email = getDataAsString(cursorMail, ContactsContract.CommonDataKinds.Email.ADDRESS);
        }
        Uri uriContacto = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        foto = ImageUtil.convertToImage(parentActivity, uriContacto);
        return new Contacto(contactId, contactNumber, contactName, email, foto);
    }

}
