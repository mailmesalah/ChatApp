package chatapp.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import chatapp.storage.tables.BlockedContact;
import chatapp.storage.tables.Contact;
import chatapp.storage.tables.ContactProfile;
import chatapp.storage.tables.DualChat;
import chatapp.storage.tables.Installation;
import chatapp.storage.tables.Profile;
import chatapp.storage.tables.Register;
import chatapp.storage.tables.RegistrationStatus;
import chatapp.storage.tables.RequestReceived;
import chatapp.storage.tables.RequestSent;
import chatapp.storage.tables.SearchProfile;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables

	private static DatabaseHandler database;

	private DatabaseHandler(Context context) {
		super(context, "Storage", null, 9);
	}

	public static synchronized DatabaseHandler getInstance(Context context) {

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (database == null) {
			database = new DatabaseHandler(context.getApplicationContext());
		}
		return database;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
        //Table creation
        Register.onCreate(db);
        Profile.onCreate(db);
        RegistrationStatus.onCreate(db);
        Installation.onCreate(db);

        Contact.onCreate(db);
        RequestSent.onCreate(db);
        RequestReceived.onCreate(db);
        BlockedContact.onCreate(db);

        ContactProfile.onCreate(db);
        SearchProfile.onCreate(db);

        DualChat.onCreate(db);

        Log.d("Database : ", "Database and Table Created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Upgrade Tables
  /*      Register.onUpgrade(db,oldVersion,newVersion);
        Profile.onUpgrade(db,oldVersion,newVersion);
        RegistrationStatus.onUpgrade(db,oldVersion,newVersion);
        Installation.onUpgrade(db,oldVersion,newVersion);

        Contact.onUpgrade(db,oldVersion,newVersion);
        RequestReceived.onUpgrade(db,oldVersion,newVersion);
        RequestSent.onUpgrade(db,oldVersion,newVersion);
        BlockedContact.onUpgrade(db,oldVersion,newVersion);

        ContactProfile.onUpgrade(db,oldVersion,newVersion);
        SearchProfile.onUpgrade(db,oldVersion,newVersion);
*/
        DualChat.onUpgrade(db,oldVersion,newVersion);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */


	public synchronized Cursor getRegister(){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("Register", new String[] { "UserID", "LoginID" }, null, null, null, null, null);
    }

    public synchronized boolean setRegister(ContentValues values){
        database.getWritableDatabase().delete("Register",null,null);
        long id = database.getWritableDatabase().insert("Register", "", values);
        if(id <=0 ) {
            return false;
        }
        return true;
    }

    public synchronized Cursor getRegistrationStatus(){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("RegistrationStatus", new String[] { "Status"}, null, null, null, null, null);
    }

    public synchronized boolean setRegistrationStatus(ContentValues values){
        database.getWritableDatabase().delete("RegistrationStatus",null,null);
        long id = database.getWritableDatabase().insert("RegistrationStatus", "", values);
        if(id <=0 ) {
            return false;
        }
        return true;
    }

    public synchronized Cursor getInstallationStatus(){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("Installation", new String[] { "Completed"}, null, null, null, null, null);
    }

    public synchronized boolean setInstallationStatus(ContentValues values){
        database.getWritableDatabase().delete("Installation",null,null);
        long id = database.getWritableDatabase().insert("Installation", "", values);
        if(id <=0 ) {
            return false;
        }
        return true;
    }

    public synchronized Cursor getProfile(){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("Profile", new String[] { "_ID","ProfileName", "ProfileImage", "Quote"}, null, null, null, null, null);
    }

    public synchronized boolean setProfile(ContentValues values){
        database.getWritableDatabase().delete("Profile",null,null);
        long id = database.getWritableDatabase().insert("Profile", "", values);
        if(id <=0 ) {
            return false;
        }
        return true;
    }

    //Contact Manager
    public synchronized Cursor getContacts(String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("Contact", new String[] { "_ID","UserID", "ProfileName", "ProfileImage", "Quote" }, selection, selectionArgs, null, null, sortOrder);
    }

    public synchronized int addContact(ContentValues values){
        long id = database.getWritableDatabase().insert("Contact", "", values);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int addContacts(ContentValues[] values){
        int returnVal = values.length;
        try {
            database.getWritableDatabase().beginTransaction();
            for (ContentValues cv:values) {
                database.getWritableDatabase().insert("Contact", "", cv);
            }
            database.getWritableDatabase().setTransactionSuccessful();
        }catch(SQLException e){
            returnVal=0;
        }
        finally {
            database.getWritableDatabase().endTransaction();
        }

        return returnVal;
    }

    public synchronized int updateContact(ContentValues values, String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().update("Contact",values,selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int deleteContact(String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().delete("Contact",selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int) id;
    }

    public synchronized Cursor getRequestReceived(String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("RequestReceived", new String[] { "_ID","UserID", "ProfileName", "ProfileImage", "Quote" }, selection, selectionArgs, null, null, sortOrder);
    }

    public synchronized int addRequestReceived(ContentValues values){
        long id = database.getWritableDatabase().insert("RequestReceived", "", values);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int addRequestsReceived(ContentValues[] values){
        int returnVal = values.length;
        try {
            database.getWritableDatabase().beginTransaction();
            for (ContentValues cv:values) {
                database.getWritableDatabase().insert("RequestReceived", "", cv);
            }
            database.getWritableDatabase().setTransactionSuccessful();
        }catch(SQLException e){
            returnVal=0;
        }
        finally {
            database.getWritableDatabase().endTransaction();
        }

        return returnVal;
    }

    public synchronized int updateRequestReceived(ContentValues values, String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().update("RequestReceived",values,selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int deleteRequestReceived(String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().delete("RequestReceived",selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int) id;
    }

    public synchronized Cursor getRequestSent(String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("RequestSent", new String[] { "_ID","UserID", "ProfileName", "ProfileImage", "Quote" }, selection, selectionArgs, null, null, sortOrder);
    }

    public synchronized int addRequestSent(ContentValues values){
        long id = database.getWritableDatabase().insert("RequestSent", "", values);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int addRequestsSent(ContentValues[] values){
        int returnVal = values.length;
        try {
            database.getWritableDatabase().beginTransaction();
            for (ContentValues cv:values) {
                database.getWritableDatabase().insert("RequestSent", "", cv);
            }
            database.getWritableDatabase().setTransactionSuccessful();
        }catch(SQLException e){
            returnVal=0;
        }
        finally {
            database.getWritableDatabase().endTransaction();
        }

        return returnVal;
    }

    public synchronized int updateRequestSent(ContentValues values, String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().update("RequestSent",values,selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int deleteRequestSent(String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().delete("RequestSent",selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int) id;
    }


    public synchronized Cursor getBlockedContact(String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("BlockedContact", new String[] { "_ID","UserID", "ProfileName" }, selection, selectionArgs, null, null, sortOrder);
    }

    public synchronized int addBlockedContact(ContentValues values){
        long id = database.getWritableDatabase().insert("BlockedContact", "", values);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int addBlockedContacts(ContentValues[] values){
        int returnVal = values.length;
        try {
            database.getWritableDatabase().beginTransaction();
            for (ContentValues cv:values) {
                database.getWritableDatabase().insert("BlockedContact", "", cv);
            }
            database.getWritableDatabase().setTransactionSuccessful();
        }catch(SQLException e){
            returnVal=0;
        }
        finally {
            database.getWritableDatabase().endTransaction();
        }

        return returnVal;
    }

    public synchronized int updateBlockedContact(ContentValues values, String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().update("BlockedContact",values,selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int deleteBlockedContact(String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().delete("BlockedContact",selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int) id;
    }

    public synchronized Cursor getContactProfile(){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("ContactProfile", new String[] { "_ID","UserID","ProfileName", "ProfileImage", "Quote"}, null, null, null, null, null);
    }

    public synchronized boolean setContactProfile(ContentValues values){
        database.getWritableDatabase().delete("ContactProfile",null,null);
        long id = database.getWritableDatabase().insert("ContactProfile", "", values);
        if(id <=0 ) {
            return false;
        }
        return true;
    }


    public synchronized Cursor getSearchProfiles(String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("SearchProfile", new String[] { "_ID","UserID", "ProfileName", "ProfileImage", "Quote" }, selection, selectionArgs, null, null, sortOrder);
    }

    public synchronized int addSearchProfile(ContentValues values){
        long id = database.getWritableDatabase().insert("SearchProfile", "", values);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int addSearchProfiles(ContentValues[] values){
        int returnVal = values.length;
        try {
            database.getWritableDatabase().beginTransaction();
            for (ContentValues cv:values) {
                database.getWritableDatabase().insert("SearchProfile", "", cv);
            }
            database.getWritableDatabase().setTransactionSuccessful();
        }catch(SQLException e){
            Log.d("Error",""+e.getLocalizedMessage());
            returnVal=0;
        }
        finally {
            database.getWritableDatabase().endTransaction();
        }

        return returnVal;
    }

    public synchronized int updateSearchProfile(ContentValues values, String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().update("SearchProfile",values,selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int)id;
    }

    public synchronized int deleteSearchProfile(String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().delete("SearchProfile",selection,selectionArgs);
        if(id <=0 ) {
            return (int)id;
        }
        return (int) id;
    }


    //Dual Chat
    public synchronized Cursor getDualChats(String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query("DualChat", new String[] { "_ID","ServerChatID", "SenderUserID", "ReceiverUserID", "MessageText", "FilePath", "MessageType", "MessageTime", "SeenBy" }, selection, selectionArgs, null, null, "MessageTime");
    }

    public synchronized long addDualChat(ContentValues values){
        return database.getWritableDatabase().insert("DualChat", "", values);
    }

    public synchronized int addDualChats(ContentValues[] values){
        int returnVal = 0;
        try {
            database.getWritableDatabase().beginTransaction();
            for (ContentValues cv:values) {
                database.getWritableDatabase().insert("DualChat", "", cv);
            }
            database.getWritableDatabase().setTransactionSuccessful();
        }catch(SQLException e){
            returnVal=0;
        }
        finally {
            database.getWritableDatabase().endTransaction();
        }

        return returnVal;
    }

    public synchronized int updateDualChat(ContentValues values, String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().update("DualChat",values,selection,selectionArgs);
        return (int)id;
    }

    public synchronized int deleteDualChat(String selection, String[] selectionArgs){
        long id = database.getWritableDatabase().delete("DualChat",selection,selectionArgs);
        return (int) id;
    }

    //Dual Chat Header
    public synchronized Cursor getDualChatHeaders(String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase rdb = database.getReadableDatabase();
        SQLiteQueryBuilder queryB = new SQLiteQueryBuilder();
        queryB.setTables("DualChat DC Inner Join Contact C On (DC.SenderUserID=C.UserID Or DC.ReceiverUserID=C.UserID)");
        return queryB.query(rdb, new String[]{"UserID","ProfileName","ProfileImage","Max(MessageTime) As MessageTime","(Select Count(*) From DualChat DCS Where(DCS.SenderUserID=UserID And DCS.SeenBy=1)) As MessageCount"}, selection, selectionArgs, "UserID,ProfileName,ProfileImage", null, "MessageTime DESC");
    }

}