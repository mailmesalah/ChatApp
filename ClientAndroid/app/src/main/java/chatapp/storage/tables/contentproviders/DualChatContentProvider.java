package chatapp.storage.tables.contentproviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import chatapp.storage.DatabaseHandler;

/**
 * Created by Sely on 05-Sep-16.
 */
public class DualChatContentProvider extends ContentProvider {

    private DatabaseHandler database;
    private static final String AUTHORITY = "chat.app.client.dashboard.dualchat.contentprovider";

    public static final Uri DUAL_CHAT_URI = Uri.parse("content://" + AUTHORITY + "/dual_chat");
    public static final Uri DUAL_CHAT_HEADER_URI = Uri.parse("content://" + AUTHORITY + "/dual_chat_header");

    private static final int DUAL_CHAT = 0;
    private static final int DUAL_CHAT_HEADER = 1;


    private static final UriMatcher uriMatcher = getUriMatcher();
    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "dual_chat", DUAL_CHAT);
        uriMatcher.addURI(AUTHORITY, "dual_chat_header", DUAL_CHAT_HEADER);
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
            case DUAL_CHAT: {
                Cursor c = database.getDualChats(selection, selectionArgs, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), DUAL_CHAT_URI);
                return c;
            }
            case DUAL_CHAT_HEADER: {
                Cursor c = database.getDualChatHeaders(selection, selectionArgs, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), DUAL_CHAT_HEADER_URI);
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
            case DUAL_CHAT:
                long i = database.addDualChat(values);
                getContext().getContentResolver().notifyChange(DUAL_CHAT_URI,null);
                getContext().getContentResolver().notifyChange(DUAL_CHAT_HEADER_URI,null);
                return ContentUris.withAppendedId(DUAL_CHAT_URI,i);


            default:
        }

        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        switch (uriMatcher.match(uri)){
            case DUAL_CHAT: {
                int r = database.addDualChats(values);
                getContext().getContentResolver().notifyChange(DUAL_CHAT_URI, null);
                getContext().getContentResolver().notifyChange(DUAL_CHAT_HEADER_URI, null);
                return r;
            }

            default:
        }

        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case DUAL_CHAT: {
                int r = database.deleteDualChat(selection, selectionArgs);
                getContext().getContentResolver().notifyChange(DUAL_CHAT_URI, null);
                return r;
            }

            default:
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case DUAL_CHAT: {
                int r = database.updateDualChat(values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(DUAL_CHAT_URI, null);
                return r;
            }

            default:
        }

        return 0;
    }
}
