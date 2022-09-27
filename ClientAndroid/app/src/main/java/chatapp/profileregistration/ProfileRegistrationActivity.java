package chatapp.profileregistration;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Map;

import chatapp.client.R;
import chatapp.extras.DeviceID;
import chatapp.extras.Permissions;
import chatapp.extras.SwissArmyKnife;
import chatapp.profileregistration.fragments.RegistrationStep1CreateOrRecoverFragment;
import chatapp.profileregistration.fragments.RegistrationStep1CreateProfileFragment;
import chatapp.profileregistration.fragments.RegistrationStep1RecoverByPhoneNumberFragment;
import chatapp.profileregistration.fragments.RegistrationStep1RecoverByUserIDFragment;
import chatapp.profileregistration.fragments.RegistrationStep1RecoverSelectionFragment;
import chatapp.profileregistration.fragments.RegistrationStep2ProfileCreation;
import chatapp.services.RegistrationService;
import chatapp.storage.tables.Register;
import chatapp.storage.tables.RegistrationStatus;
import chatapp.storage.tables.contentproviders.RegistrationContentProvider;

public class ProfileRegistrationActivity extends AppCompatActivity {

    /**
     * Messenger for communicating with the service.
     */
    //Messenger mClientService = null;
    RegistrationService mService;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mBound;

    public static final int CREATE_OR_RECOVER = 1;
    public static final int RECOVER_BY_SELECTION = 2;
    public static final int RECOVER_BY_USER_ID = 4;
    public static final int RECOVER_BY_PHONE_NUMBER = 5;
    public static final int CREATE_PROFILE = 6;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_registration);

        //Get Permissions before continue
        Permissions.getAllRequiredPermissions(this);

        if (savedInstanceState == null) {

        }

    }

    public void nextFragment(int fragmentNo, String result) {
        switch (fragmentNo) {

            case CREATE_OR_RECOVER: {

                if (result.equals("RECOVER")) {
                    RegistrationStep1RecoverSelectionFragment fragment = (RegistrationStep1RecoverSelectionFragment) getSupportFragmentManager().findFragmentByTag("RECOVER_BY_SELECTION");
                    if (fragment == null) {
                        fragment = RegistrationStep1RecoverSelectionFragment.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "RECOVER_BY_SELECTION");
                        //ft.addToBackStack(null);
                        ft.commit();
                    } else if (!fragment.isInLayout()) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "RECOVER_BY_SELECTION");
                        //ft.addToBackStack(null);
                        ft.commit();
                    }
                } else {
                    RegistrationStep1CreateProfileFragment fragment = (RegistrationStep1CreateProfileFragment) getSupportFragmentManager().findFragmentByTag("CREATE_PROFILE");
                    if (fragment == null) {
                        fragment = RegistrationStep1CreateProfileFragment.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                        //ft.addToBackStack(null);
                        ft.commit();
                    } else if (!fragment.isInLayout()) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                        //ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }
            break;

            case RECOVER_BY_SELECTION: {
                if (result.equals("USER_ID")) {
                    RegistrationStep1RecoverByUserIDFragment fragment = (RegistrationStep1RecoverByUserIDFragment) getSupportFragmentManager().findFragmentByTag("RECOVER_BY_USER_ID");
                    if (fragment == null) {
                        fragment = RegistrationStep1RecoverByUserIDFragment.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "RECOVER_BY_USER_ID");
                        //ft.addToBackStack(null);
                        ft.commit();
                    } else if (!fragment.isInLayout()) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "RECOVER_BY_USER_ID");
                        //ft.addToBackStack(null);
                        ft.commit();
                    }
                } else if (result.equals("PHONE_NUMBER")) {
                    RegistrationStep1RecoverByPhoneNumberFragment fragment = (RegistrationStep1RecoverByPhoneNumberFragment) getSupportFragmentManager().findFragmentByTag("RECOVER_BY_PHONE_NUMBER");
                    if (fragment == null) {
                        fragment = RegistrationStep1RecoverByPhoneNumberFragment.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "RECOVER_BY_PHONE_NUMBER");
                        //ft.addToBackStack(null);
                        ft.commit();
                    } else if (!fragment.isInLayout()) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "RECOVER_BY_PHONE_NUMBER");
                        //ft.addToBackStack(null);
                        ft.commit();
                    }
                } else if (result.equals("CREATE_NEW")) {
                    RegistrationStep1CreateProfileFragment fragment = (RegistrationStep1CreateProfileFragment) getSupportFragmentManager().findFragmentByTag("CREATE_PROFILE");
                    if (fragment == null) {
                        fragment = RegistrationStep1CreateProfileFragment.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                        //ft.addToBackStack(null);
                        ft.commit();
                    } else if (!fragment.isInLayout()) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                        //ft.addToBackStack(null);
                        ft.commit();
                    }
                }

            }
            break;

            case RECOVER_BY_PHONE_NUMBER: {
                RegistrationStep1CreateProfileFragment fragment = (RegistrationStep1CreateProfileFragment) getSupportFragmentManager().findFragmentByTag("CREATE_PROFILE");
                if (fragment == null) {
                    fragment = RegistrationStep1CreateProfileFragment.newInstance();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                    //ft.addToBackStack(null);
                    ft.commit();
                } else if (!fragment.isInLayout()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                    //ft.addToBackStack(null);
                    ft.commit();
                }
            }
            break;

            case RECOVER_BY_USER_ID: {
                RegistrationStep1CreateProfileFragment fragment = (RegistrationStep1CreateProfileFragment) getSupportFragmentManager().findFragmentByTag("CREATE_PROFILE");
                if (fragment == null) {
                    fragment = RegistrationStep1CreateProfileFragment.newInstance();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                    //ft.addToBackStack(null);
                    ft.commit();
                } else if (!fragment.isInLayout()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                    //ft.addToBackStack(null);
                    ft.commit();
                }
            }
            break;

            case CREATE_PROFILE: {
                if (result.equals("RECOVER")) {
                    RegistrationStep1RecoverSelectionFragment fragment = (RegistrationStep1RecoverSelectionFragment) getSupportFragmentManager().findFragmentByTag("RECOVER_BY_SELECTION");
                    if (fragment == null) {
                        fragment = RegistrationStep1RecoverSelectionFragment.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "RECOVER_BY_SELECTION");
                        //ft.addToBackStack(null);
                        ft.commit();
                    } else if (!fragment.isInLayout()) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "RECOVER_BY_SELECTION");
                        //ft.addToBackStack(null);
                        ft.commit();
                    }
                }else{
                    RegistrationStep2ProfileCreation fragment = (RegistrationStep2ProfileCreation) getSupportFragmentManager().findFragmentByTag("PROFILE");
                    if (fragment == null) {
                        fragment = RegistrationStep2ProfileCreation.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "PROFILE");
                        //ft.addToBackStack(null);
                        ft.commit();
                    } else if (!fragment.isInLayout()) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.profile_registration_fragment_container, fragment, "PROFILE");
                        //ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }
            break;

            default:
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Bind to the service
        bindService(new Intent(this, RegistrationService.class), mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();


        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    //Service Connection Manager
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mService = (RegistrationService) ((RegistrationService.RegistrationBinder) binder).getService();
            //Message handle to send message to service is received
            //mClientService = new Messenger(service);
            mBound = true;

            loadFirstFragment();
        }

        public void onServiceDisconnected(ComponentName className) {
            //mClientService = null;
            mService = null;
            mBound = false;
        }
    };

    public RegistrationService getService() {
        return mService;
    }

    public void checkPhoneNumberUsed(String phoneNumber) {
        mService.isPhoneNumberUsed(phoneNumber);
    }

    public void checkUserIDUsed(String userID) {
        mService.isUserIDUsed(userID);
    }

    public void registerUser(String userID, String phoneNumber, String password) {
        String deviceID = DeviceID.getDeviceID(getApplicationContext());
        mService.registerUser(userID, phoneNumber, deviceID, password);
    }

    public void uploadContacts() {
        Cursor c = getContentResolver().query(RegistrationContentProvider.REGISTER_URI, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            Map<String, String> contacts = SwissArmyKnife.getContacts(getApplicationContext());
            mService.contactUpload(c.getString(Register.USER_ID), contacts);
        }
        c.close();

    }

    public void recoverByPhoneNumber(String phoneNumber, String password) {
        String deviceID = DeviceID.getDeviceID(getApplicationContext());
        mService.recoverByPhoneNumber(phoneNumber, deviceID, password);
    }

    public void recoverByUserID(String userID, String password) {
        String deviceID = DeviceID.getDeviceID(getApplicationContext());
        mService.recoverByUserID(userID, deviceID, password);
    }


    public void createProfile(String profileName, Bitmap profileImage, String quote){
        Cursor c = getContentResolver().query(RegistrationContentProvider.REGISTER_URI, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            mService.createProfile(c.getString(Register.USER_ID), profileName, profileImage, quote);
        }

        c.close();

    }

    private void loadFirstFragment(){
        //Check if Registeration over
        Cursor c = getContentResolver().query(RegistrationContentProvider.STATUS_URI, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            String status = c.getString(RegistrationStatus.STATUS);
            if (status.equals("Registered")) {
                //open fragment to contact upload
                RegistrationStep1CreateProfileFragment fragment = RegistrationStep1CreateProfileFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_PROFILE");
                ft.commit();
            } else if (status.equals("ContactUploaded")) {
                //open profile fragment
                RegistrationStep2ProfileCreation fragment = RegistrationStep2ProfileCreation.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.profile_registration_fragment_container, fragment, "PROFILE");
                ft.commit();
            }
        }else{
            RegistrationStep1CreateOrRecoverFragment fragment = RegistrationStep1CreateOrRecoverFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.profile_registration_fragment_container, fragment, "CREATE_OR_RECOVER");
            ft.commit();
        }

        c.close();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Permissions.PERMISSION_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
            }

            case Permissions.PERMISSION_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
            }

            case Permissions.PERMISSION_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
            }

            case Permissions.PERMISSION_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Forwarding to all Fragments
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            try {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d("Success","Success");
            }catch(Exception e){
                Log.d("Error",e.getLocalizedMessage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
