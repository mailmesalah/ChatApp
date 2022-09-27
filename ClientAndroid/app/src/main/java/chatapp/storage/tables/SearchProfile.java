package chatapp.storage.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sely on 19-Sep-16.
 */
public class SearchProfile {
    public final static int ID=0;
    public final static int USER_ID=1;
    public final static int PROFILE_NAME=2;
    public final static int PROFILE_IMAGE=3;
    public final static int QUOTE=4;
    public final static int CONTACT_TYPE=5;


    private long _id;
    private String userID;
    private String profileName;
    private byte[] profileImage;
    private String quote;
    private String contactType;


    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL("Create Table SearchProfile(_ID Integer Primary Key autoincrement, UserID Text Not Null, ProfileName Text Not Null,ProfileImage Blob, Quote Text, ContactType Text Not Null);");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // Drop older table if existed
        database.execSQL("Drop Table If Exists SearchProfile;");
        onCreate(database);
    }
}
