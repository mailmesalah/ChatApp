package chatapp.storage.tables.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import chatapp.storage.DatabaseHandler;

/**
 * Created by Sely on 05-Sep-16.
 */
public class RegistrationContentProvider extends ContentProvider {

    private DatabaseHandler database;
    private static final String AUTHORITY = "chat.app.client.registration.contentprovider";

    public static final Uri REGISTER_URI = Uri.parse("content://" + AUTHORITY + "/register");
    public static final Uri PROFILE_URI = Uri.parse("content://" + AUTHORITY + "/profile");
    public static final Uri STATUS_URI = Uri.parse("content://" + AUTHORITY + "/status");
    public static final Uri INSTALLATION_URI = Uri.parse("content://" + AUTHORITY + "/installation");

    private static final int REGISTER = 0;
    private static final int PROFILE = 1;
    private static final int STATUS = 2;
    private static final int INSTALLATION = 3;

    private static final UriMatcher uriMatcher = getUriMatcher();
    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "register", REGISTER);
        uriMatcher.addURI(AUTHORITY, "profile", PROFILE);
        uriMatcher.addURI(AUTHORITY, "status", STATUS);
        uriMatcher.addURI(AUTHORITY, "installation", INSTALLATION);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        database=DatabaseHandler.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (uriMatcher.match(uri)){
            case REGISTER:
                return database.getRegister();

            case PROFILE:
                return database.getProfile();

            case STATUS:
                return database.getRegistrationStatus();

            case INSTALLATION:
                return database.getInstallationStatus();

            default:
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)){
            case REGISTER:
                database.setRegister(values);
                return null;

            case PROFILE:
                database.setProfile(values);
                return null;

            case STATUS:
                database.setRegistrationStatus(values);
                return null;

            case INSTALLATION:
                database.setInstallationStatus(values);
                return null;

            default:
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
