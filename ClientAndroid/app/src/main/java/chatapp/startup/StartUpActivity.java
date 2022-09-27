package chatapp.startup;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import chatapp.client.R;
import chatapp.dashboard.DashboardActivity;
import chatapp.extras.Permissions;
import chatapp.profileregistration.ProfileRegistrationActivity;
import chatapp.storage.tables.Installation;
import chatapp.storage.tables.RegistrationStatus;
import chatapp.storage.tables.contentproviders.ContactManagerContentProvider;
import chatapp.storage.tables.contentproviders.RegistrationContentProvider;

public class StartUpActivity extends AppCompatActivity {

    private static final int REGISTRATION = 0;
    private static final int DASHBOARD = 1;

    private void generateMokeTableData(){
        ContentValues cv = new ContentValues();
        cv.put("UserID","1");
        cv.put("ProfileName","Name");
        cv.put("Quote","Just Do it...!");

        getContentResolver().insert(ContactManagerContentProvider.CONTACT_URI,cv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        //generateMokeTableData();

        if (!isRegistered()) {
            Intent i = new Intent(this, ProfileRegistrationActivity.class);
            startActivityForResult(i, REGISTRATION);
        } else {
            Intent i = new Intent(this, DashboardActivity.class);
            startActivityForResult(i, DASHBOARD);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTRATION) {
            if (resultCode == RESULT_OK) {

                //Check again if Registration has completed
                if (isRegistered()){
                    Intent i = new Intent(this, DashboardActivity.class);
                    startActivityForResult(i, DASHBOARD);
                }
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        } else {
            finish();
        }
    }


    private Boolean isRegistered() {
        //Check if Profile Registration Over
        Cursor c = getContentResolver().query(RegistrationContentProvider.INSTALLATION_URI, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            if (c.getString(Installation.COMPLETED).equalsIgnoreCase("TRUE")) {
                // always close the cursor
                c.close();
                return true;
            }
        }
        return false;
    }


}
