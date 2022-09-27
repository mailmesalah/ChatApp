package chatapp.storage.tables;

import android.database.sqlite.SQLiteDatabase;
import android.media.Image;

/**
 * Created by Sely on 03-Sep-16.
 */
public class Profile {

    public final static int ID=0;
    public final static int PROFILE_NAME=1;
    public final static int PROFILE_IMAGE=2;
    public final static int QUOTE=3;

    private long _id;
    private String profileName;
    private byte[] profileImage;
    private String quote;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
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
        database.execSQL("Create Table Profile(_ID Integer Primary Key autoincrement, ProfileName Text Not Null,ProfileImage Blob, Quote Text);");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // Drop older table if existed
        database.execSQL("Drop Table If Exists Profile;");
        onCreate(database);
    }

}
