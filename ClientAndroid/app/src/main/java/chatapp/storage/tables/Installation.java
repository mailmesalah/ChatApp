package chatapp.storage.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sely on 03-Sep-16.
 */
public class Installation {

    public static int COMPLETED=0;

    private long _id;
    private String completed;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL("Create Table Installation(_ID Integer Primary Key autoincrement, Completed Text Not Null);");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // Drop older table if existed
        database.execSQL("Drop Table If Exists Installation;");
        onCreate(database);
    }
}
