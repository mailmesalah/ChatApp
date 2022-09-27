package chatapp.storage.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sely on 03-Sep-16.
 */
public class RegistrationStatus {

    public static int STATUS=0;

    private long _id;
    private String status;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL("Create Table RegistrationStatus(_ID Integer Primary Key autoincrement, Status Text Not Null);");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // Drop older table if existed
        database.execSQL("Drop Table If Exists RegistrationStatus;");
        onCreate(database);
    }
}
