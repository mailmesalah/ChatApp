package chatapp.storage.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sely on 23-Sep-16.
 */
public class Contact {

    public final static int ID=0;
    public final static int USER_ID=1;
    public final static int PROFILE_NAME=2;
    public final static int PROFILE_IMAGE=3;
    public final static int QUOTE=4;

    private long _id;
    private String userID;
    private String profileName;
    private byte[] profileImage;
    private String quote;

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


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL("Create Table Contact(_ID Integer Primary Key autoincrement, UserID Text Not Null, ProfileName Text Not Null,ProfileImage Blob, Quote Text);");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // Drop older table if existed
        database.execSQL("Drop Table If Exists Contact;");
        onCreate(database);
    }

}
