package chatapp.storage.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sely on 03-Sep-16.
 */
public class Register {
    public static int USER_ID=0;
    public static int LOGIN_ID=1;

    private long _id;
    private String userID;
    private String loginID;

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

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL("Create Table Register(_ID Integer Primary Key autoincrement, UserID Text Not Null,LoginID Text Not Null);");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // Drop older table if existed
        database.execSQL("Drop Table If Exists Register;");
        onCreate(database);
    }
}
