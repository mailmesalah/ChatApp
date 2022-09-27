package chatapp.storage.tables.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import chatapp.storage.DatabaseHandler;

/**
 * Created by Sely on 05-Sep-16.
 */
public class ContactManagerContentProvider extends ContentProvider {

    private DatabaseHandler database;
    private static final String AUTHORITY = "chat.app.client.dashboard.contactmanager.contentprovider";

    public static final Uri CONTACT_URI = Uri.parse("content://" + AUTHORITY + "/contact");
    public static final Uri REQUEST_RECEIVED_URI = Uri.parse("content://" + AUTHORITY + "/request_received");
    public static final Uri REQUEST_SENT_URI = Uri.parse("content://" + AUTHORITY + "/request_sent");
    public static final Uri BLOCKED_CONTACT_URI = Uri.parse("content://" + AUTHORITY + "/blocked_contact");
    public static final Uri CONTACT_PROFILE_URI = Uri.parse("content://" + AUTHORITY + "/contact_profile");
    public static final Uri SEARCH_PROFILE_URI = Uri.parse("content://" + AUTHORITY + "/search_profile");

    private static final int CONTACT = 0;
    private static final int REQUEST_RECEIVED = 1;
    private static final int REQUEST_SENT = 2;
    private static final int BLOCKED_CONTACT = 3;
    private static final int CONTACT_PROFILE = 4;
    private static final int SEARCH_PROFILE = 5;

    private static final UriMatcher uriMatcher = getUriMatcher();
    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "contact", CONTACT);
        uriMatcher.addURI(AUTHORITY, "request_received", REQUEST_RECEIVED);
        uriMatcher.addURI(AUTHORITY, "request_sent", REQUEST_SENT);
        uriMatcher.addURI(AUTHORITY, "blocked_contact", BLOCKED_CONTACT);
        uriMatcher.addURI(AUTHORITY, "contact_profile", CONTACT_PROFILE);
        uriMatcher.addURI(AUTHORITY, "search_profile", SEARCH_PROFILE);
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
            case CONTACT: {
                Cursor c = database.getContacts(selection, selectionArgs, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), CONTACT_URI);
                return c;
            }
            case REQUEST_RECEIVED: {
                Cursor c = database.getRequestReceived(selection, selectionArgs, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), REQUEST_RECEIVED_URI);
                return c;
            }
            case REQUEST_SENT: {
                Cursor c = database.getRequestSent(selection, selectionArgs, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), REQUEST_SENT_URI);
                return c;
            }
            case BLOCKED_CONTACT: {
                Cursor c = database.getBlockedContact(selection, selectionArgs, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), BLOCKED_CONTACT_URI);
                return c;
            }
            case CONTACT_PROFILE: {
                Cursor c = database.getContactProfile();
                c.setNotificationUri(getContext().getContentResolver(), CONTACT_PROFILE_URI);
                return c;
            }
            case SEARCH_PROFILE: {
                Cursor c = database.getSearchProfiles(selection, selectionArgs, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), SEARCH_PROFILE_URI);
                return c;
            }
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
            case CONTACT:
                database.addContact(values);
                getContext().getContentResolver().notifyChange(CONTACT_URI,null);
                return null;

            case REQUEST_RECEIVED:
                database.addRequestReceived(values);
                getContext().getContentResolver().notifyChange(REQUEST_RECEIVED_URI,null);
                return null;

            case REQUEST_SENT:
                database.addRequestSent(values);
                getContext().getContentResolver().notifyChange(REQUEST_SENT_URI,null);
                return null;

            case BLOCKED_CONTACT:
                database.addBlockedContact(values);
                getContext().getContentResolver().notifyChange(BLOCKED_CONTACT_URI,null);
                return null;

            case CONTACT_PROFILE:
                database.setContactProfile(values);
                getContext().getContentResolver().notifyChange(CONTACT_PROFILE_URI,null);
                return null;

            case SEARCH_PROFILE:
                database.addSearchProfile(values);
                getContext().getContentResolver().notifyChange(SEARCH_PROFILE_URI,null);
                return null;

            default:
        }

        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        switch (uriMatcher.match(uri)){
            case CONTACT: {
                int r = database.addContacts(values);
                getContext().getContentResolver().notifyChange(CONTACT_URI, null);
                return r;
            }
            case REQUEST_RECEIVED: {
                int r = database.addRequestsReceived(values);
                getContext().getContentResolver().notifyChange(REQUEST_RECEIVED_URI, null);
                return r;
            }
            case REQUEST_SENT: {
                int r = database.addRequestsSent(values);
                getContext().getContentResolver().notifyChange(REQUEST_SENT_URI, null);
                return r;
            }
            case BLOCKED_CONTACT: {
                int r = database.addBlockedContacts(values);
                getContext().getContentResolver().notifyChange(BLOCKED_CONTACT_URI, null);
                return r;
            }
            case SEARCH_PROFILE: {
                int r = database.addSearchProfiles(values);
                getContext().getContentResolver().notifyChange(SEARCH_PROFILE_URI, null);
                return r;
            }

            default:
        }

        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case CONTACT: {
                int r = database.deleteContact(selection, selectionArgs);
                getContext().getContentResolver().notifyChange(CONTACT_URI, null);
                return r;
            }
            case REQUEST_RECEIVED: {
                int r = database.deleteRequestReceived(selection, selectionArgs);
                getContext().getContentResolver().notifyChange(REQUEST_RECEIVED_URI, null);
                return r;
            }
            case REQUEST_SENT: {
                int r = database.deleteRequestSent(selection, selectionArgs);
                getContext().getContentResolver().notifyChange(REQUEST_SENT_URI, null);
                return r;
            }
            case BLOCKED_CONTACT: {
                int r = database.deleteBlockedContact(selection, selectionArgs);
                getContext().getContentResolver().notifyChange(BLOCKED_CONTACT_URI, null);
                return r;
            }
            case SEARCH_PROFILE: {
                int r = database.deleteSearchProfile(selection, selectionArgs);
                getContext().getContentResolver().notifyChange(SEARCH_PROFILE_URI, null);
                return r;
            }
            default:
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case CONTACT: {
                int r = database.updateContact(values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(CONTACT_URI, null);
                return r;
            }
            case REQUEST_RECEIVED: {
                int r = database.updateRequestReceived(values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(REQUEST_RECEIVED_URI, null);
                return r;
            }
            case REQUEST_SENT: {
                int r = database.updateRequestSent(values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(REQUEST_SENT_URI, null);
                return r;
            }
            case BLOCKED_CONTACT: {
                int r = database.updateBlockedContact(values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(BLOCKED_CONTACT_URI, null);
                return r;
            }
            case SEARCH_PROFILE: {
                int r = database.updateSearchProfile(values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(SEARCH_PROFILE_URI, null);
                return r;
            }
            default:
        }

        return 0;
    }
}
