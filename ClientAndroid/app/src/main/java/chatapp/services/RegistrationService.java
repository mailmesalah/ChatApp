package chatapp.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import chatapp.communication.websocket.ProfileRegistrationEndpoint;
import chatapp.extras.SwissArmyKnife;

/**
 * Created by Sely on 06-Sep-16.
 */

public class RegistrationService extends Service {
    private final IBinder mBinder = new RegistrationBinder();

    //Websocket
    private static final ProfileRegistrationEndpoint mWebsocket = new ProfileRegistrationEndpoint();


    public class RegistrationBinder extends Binder {
        public RegistrationService getService() {
            return RegistrationService.this;
        }
    }

    public ProfileRegistrationEndpoint getWebsocket(){
        return mWebsocket;
    }


        /*
        Websocket server request methods
         */

    public boolean isUserIDUsed(String userID) {
        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "CHECK_USER_ID").
                    put("USER_ID", userID);
            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public boolean isPhoneNumberUsed(String phoneNumber) {
        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "CHECK_PHONE_NUMBER").
                    put("PHONE_NUMBER", phoneNumber);
            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public boolean registerUser(String userID, String phoneNumber, String deviceID, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "REGISTER_USER")
                    .put("USER_ID", userID)
                    .put("PHONE_NUMBER", phoneNumber)
                    .put("DEVICE_ID", deviceID)
                    .put("PASSWORD", password);
            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public boolean contactUpload(String userID, Map<String,String> contacts){
        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "CONTACTS").put("USER_ID", userID);
            JSONArray contactArray = new JSONArray();
            for (String phone : contacts.keySet()) {
                JSONObject phoneData = new JSONObject();
                phoneData.put("PHONE_NUMBER",phone);
                phoneData.put("USER",contacts.get(phone));

                contactArray.put(phoneData);
            }
            json.put("CONTACTS",contactArray);
            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    public void createProfile(String userID, String profileName, Bitmap profileImage, String quote){
        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "CREATE_PROFILE")
                    .put("USER_ID", userID)
                    .put("PROFILE_NAME", profileName)
                    .put("QUOTE",quote);
            if(profileImage!=null){
                json.put("PROFILE_IMAGE", SwissArmyKnife.encodeTobase64(profileImage));
            }else{
                json.put("PROFILE_IMAGE", "");
            }

            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }

    }

    public void recoverByPhoneNumber(String phoneNumber, String deviceID, String password){
        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "RECOVER_BY_PHONE_NUMBER")
                    .put("PHONE_NUMBER", phoneNumber)
                    .put("DEVICE_ID", deviceID)
                    .put("PASSWORD", password);

            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }

    }

    public void recoverByUserID(String userID, String deviceID, String password){
        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "RECOVER_BY_USER_ID")
                    .put("USER_ID", userID)
                    .put("DEVICE_ID", deviceID)
                    .put("PASSWORD", password);

            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }

    }
        /*
        Websocket server request methods
         */

    //Returns binder to let client send message to this server
    @Override
    public IBinder onBind(Intent intent) {
        //Websocket open and listen
        mWebsocket.connectToServer();

        //return mMessenger.getBinder();
        return mBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mWebsocket.stopConnection();
    }
}

