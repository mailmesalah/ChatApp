package chatapp.services;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.net.ConnectivityManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

import chatapp.client.R;
import chatapp.communication.websocket.ProfileRegistrationEndpoint;
import chatapp.communication.websocket.UndergroundEndpoint;
import chatapp.contacts.ContactManagerActivity;
import chatapp.dashboard.fragments.dual.DualChatActivity;
import chatapp.extras.DeviceID;
import chatapp.extras.SwissArmyKnife;
import chatapp.storage.tables.Contact;
import chatapp.storage.tables.DualChat;
import chatapp.storage.tables.Register;
import chatapp.storage.tables.RequestReceived;
import chatapp.storage.tables.contentproviders.ContactManagerContentProvider;
import chatapp.storage.tables.contentproviders.DualChatContentProvider;
import chatapp.storage.tables.contentproviders.RegistrationContentProvider;

/**
 * Created by Sely on 17-Sep-16.
 */

public class UndergroundService extends Service {


    // Message codes to check against Message.what
    //
    // Message.what is a User-defined message code so
    // that the recipient can identify what the message is about.
    public static final int GET_CONTACT_LIST = 0;
    public static final int GET_REQUEST_RECEIVED_LIST = 1;
    public static final int GET_REQUEST_SENT_LIST = 2;
    public static final int GET_BLOCK_LIST = 3;
    public static final int SEARCH_PROFILES = 4;
    public static final int ADD_REQUEST = 5;
    public static final int ACCEPT_REQUEST = 6;
    public static final int BLOCK_OTHER = 7;
    public static final int BLOCK_CONTACT = 8;
    public static final int BLOCK_REQUEST_RECEIVED = 9;
    public static final int BLOCK_REQUEST_SENT = 10;
    public static final int REMOVE_CONTACT = 11;
    public static final int REMOVE_REQUEST_RECEIVED = 12;
    public static final int REMOVE_REQUEST_SENT = 13;
    public static final int REMOVE_BLOCK = 14;
    public static final int SEND_DUAL_CHAT = 15;

    //LOCAL COMMUNICATION WITH SERVICE AND ACTIVITIES
    public static final int CONTACT_MANAGER_TAB = 16;
    public static final int DUAL_CHAT_ACTIVITY = 17;


    //Websocket
    private static final UndergroundEndpoint mWebsocket = new UndergroundEndpoint();
    public static boolean isLoggedIn = false;
    private static String mUserID = "";
    //The BroadcastReceiver that listens for network broadcasts
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SwissArmyKnife.isNetworkAvailable(context)) {
                mWebsocket.connectToServer();
            }
        }
    };
    // Messenger object used by clients to send messages to IncomingHandler
    Messenger mMessenger = new Messenger(new IncomingMessageHandler());
    private String TAG = "MessengerService";

    // Incoming messages Handler
    class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Log.d("Service Message", "" + msg.what);
            switch (msg.what) {
                case GET_CONTACT_LIST: {
                    getAllContacts();
                }

                break;

                case GET_REQUEST_RECEIVED_LIST: {
                    getAllRequestReceived();
                }

                break;

                case GET_REQUEST_SENT_LIST: {
                    getAllRequestSent();
                }

                break;

                case GET_BLOCK_LIST: {
                    getAllBlockedContacts();
                }

                break;

                case SEARCH_PROFILES: {
                    Bundle bundle = msg.getData();
                    String searchText = bundle.getString("SEARCH_TEXT");
                    int start = bundle.getInt("START");
                    int length = bundle.getInt("LENGTH");

                    searchProfile(searchText, start, length);
                }

                break;

                //Commands
                case ADD_REQUEST: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    addRequest(contactID);
                }
                break;

                case ACCEPT_REQUEST: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    acceptRequest(contactID);
                }
                break;

                case BLOCK_OTHER: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    blockOther(contactID);
                }
                break;

                case BLOCK_CONTACT: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    blockContact(contactID);
                }
                break;

                case BLOCK_REQUEST_RECEIVED: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    blockRequestReceived(contactID);
                }
                break;

                case BLOCK_REQUEST_SENT: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    blockRequestSent(contactID);
                }
                break;

                case REMOVE_CONTACT: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    removeContact(contactID);
                }
                break;

                case REMOVE_REQUEST_RECEIVED: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    removeRequestReceived(contactID);
                }
                break;

                case REMOVE_REQUEST_SENT: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    removeRequestSent(contactID);
                }
                break;

                case REMOVE_BLOCK: {
                    Bundle bundle = msg.getData();
                    String contactID = bundle.getString("CONTACT_ID");

                    removeBlock(contactID);
                }
                break;

                //Dual Chat Messages
                case SEND_DUAL_CHAT: {
                    Bundle bundle = msg.getData();
                    String receiverUserID = bundle.getString("RECEIVER_USER_ID");
                    String messageText = bundle.getString("MESSAGE_TEXT");
                    long clientChatID = bundle.getLong("CLIENT_CHAT_ID");
                    int iMessageType = bundle.getInt("MESSAGE_TYPE");
                    String filePath = bundle.getString("FILE_PATH");

                    String messageType = "";
                    switch (iMessageType) {
                        case DualChat.OUT_TEXT:
                            messageType = "TEXT";
                            break;
                        case DualChat.OUT_IMAGE_TEXT:
                            messageType = "IMAGE_TEXT";
                            break;
                        case DualChat.OUT_IMAGES_TEXT:
                            messageType = "IMAGES_TEXT";
                            break;
                        case DualChat.OUT_CAMERA_IMAGE_TEXT:
                            messageType = "CAMERA_IMAGE_TEXT";
                            break;
                        case DualChat.OUT_AUDIO_TEXT:
                            messageType = "AUDIO_TEXT";
                            break;
                        case DualChat.OUT_AUDIOS_TEXT:
                            messageType = "AUDIOS_TEXT";
                            break;
                        case DualChat.OUT_VOICE_TEXT:
                            messageType = "VOICE_TEXT";
                            break;
                        case DualChat.OUT_VIDEO_TEXT:
                            messageType = "VIDEO_TEXT";
                            break;
                        case DualChat.OUT_VIDIEOS_TEXT:
                            messageType = "VIDIEOS_TEXT";
                            break;
                        case DualChat.OUT_CAMERA_VIDEO_TEXT:
                            messageType = "CAMERA_VIDEO_TEXT";
                            break;
                        case DualChat.OUT_VOICE_CALL_SUMMERY_TEXT:
                            messageType = "VOICE_CALL_SUMMERY_TEXT";
                            break;
                        case DualChat.OUT_VIDEO_CALL_SUMMARY_TEXT:
                            messageType = "VIDEO_CALL_SUMMARY_TEXT";
                            break;
                        case DualChat.OUT_FILE_TEXT:
                            messageType = "FILE_TEXT";
                            break;

                        default:
                            messageType = "TEXT";
                    }
                    sendDualChatMessage(receiverUserID, clientChatID, messageText, messageType, filePath);
                }
                break;

                case DUAL_CHAT_ACTIVITY: {
                    Bundle bundle = msg.getData();
                    chatapp.notifications.NotificationManager.DUAL_CHAT_ACTIVITY_CONTACT_ID=bundle.getString("CONTACT_ID");
                }
                break;

                case CONTACT_MANAGER_TAB: {
                    Bundle bundle = msg.getData();
                    chatapp.notifications.NotificationManager.CONTACT_MANAGER_TAB = bundle.getInt("TAB");

                }
                break;

                default:
                    super.handleMessage(msg);
            }

        }
    }

    public UndergroundService() {
    }


    //Get all updates
    private static void getAllUpdates(ContentResolver cr) {
        getAllContacts();
        getAllRequestReceived();
        getAllRequestSent();
        getAllBlockedContacts();

        //Dual Chats
        isMessagesSeenByReceiver(cr);
        getAllUnreadDualChats();
        sendAllUnreadDualChats(cr);
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");

        //Websocket open and listen
        mWebsocket.connectToServer();
        mWebsocket.addListener(new UndergroundEndpoint.IUndergroundWebsocketListener() {
            @Override
            public void authenticate() {
                String deviceID = DeviceID.getDeviceID(getApplicationContext());
                Cursor c = getContentResolver().query(RegistrationContentProvider.REGISTER_URI, null, null, null, null);
                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                    String userID = c.getString(Register.USER_ID);
                    String loginID = c.getString(Register.LOGIN_ID);

                    UndergroundService.authenticate(userID, loginID, deviceID);

                    //Store UserID
                    mUserID = userID;

                    c.close();
                }


            }

            @Override
            public void authenticated(boolean result) {
                isLoggedIn = result;
                Log.d("Logged Result", "" + result + isLoggedIn);
                //Get all updates on connection
                if (isLoggedIn) {
                    getAllUpdates(getContentResolver());
                }
            }

            @Override
            public void contactList(JSONArray contacts) {
                try {
                    //Cache for notification
                    Cursor dContacts = getContentResolver().query(ContactManagerContentProvider.CONTACT_URI, null, null, null, null);
                    //Delete Previous Data
                    getContentResolver().delete(ContactManagerContentProvider.CONTACT_URI, null, null);
                    ContentValues[] cv = new ContentValues[contacts.length()];
                    for (int i = 0; i < contacts.length(); i++) {
                        ContentValues c = new ContentValues();
                        JSONObject obj = contacts.getJSONObject(i);
                        c.put("UserID", obj.getString("USER_ID"));
                        c.put("ProfileName", obj.getString("PROFILE_NAME"));
                        c.put("Quote", obj.getString("QUOTE"));

                        byte[] bProfileImage = null;
                        try {
                            if(!obj.getString("PROFILE_IMAGE").equals("")) {
                                bProfileImage = SwissArmyKnife.getBitmapAsByteArray(SwissArmyKnife.decodeBase64(obj.getString("PROFILE_IMAGE")));
                            }
                        } catch (Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                        if (bProfileImage != null && bProfileImage.length > 0) {
                            c.put("ProfileImage", bProfileImage);
                        }

                        cv[i] = c;
                    }
                    //Add to database
                    getContentResolver().bulkInsert(ContactManagerContentProvider.CONTACT_URI, cv);

                    //For Notification
                    if (dContacts == null || dContacts.getCount() > 0) {
                        dContacts.close();
                        return;
                    }
                    for (int i = 0; i < cv.length; i++) {
                        dContacts.moveToFirst();
                        boolean found = false;
                        while (!dContacts.isAfterLast()) {
                            if (cv[i].getAsString("UserID").equals(dContacts.getString(Contact.USER_ID))) {
                                found = true;
                            }
                            dContacts.moveToNext();
                        }

                        if (!found && chatapp.notifications.NotificationManager.CONTACT_MANAGER_TAB!= chatapp.notifications.NotificationManager.CONTACT_TAB) {
                            chatapp.notifications.NotificationManager.showAcceptRequestNotification(cv[i].getAsString("ProfileName"),UndergroundService.this);
                        }
                    }
                    dContacts.close();

                } catch (JSONException e) {
                    Log.d("Websocket Message", e.getMessage());
                }
            }

            @Override
            public void requestReceivedList(JSONArray contacts) {
                try {
                    //Cache for notification
                    Cursor dContacts = getContentResolver().query(ContactManagerContentProvider.REQUEST_RECEIVED_URI, null, null, null, null);
                    //Delete Previous Data
                    getContentResolver().delete(ContactManagerContentProvider.REQUEST_RECEIVED_URI, null, null);
                    ContentValues[] cv = new ContentValues[contacts.length()];
                    for (int i = 0; i < contacts.length(); i++) {
                        ContentValues c = new ContentValues();
                        JSONObject obj = contacts.getJSONObject(i);
                        c.put("UserID", obj.getString("USER_ID"));
                        c.put("ProfileName", obj.getString("PROFILE_NAME"));
                        c.put("Quote", obj.getString("QUOTE"));

                        byte[] bProfileImage = null;
                        try {
                            if(!obj.getString("PROFILE_IMAGE").equals("")) {
                                bProfileImage = SwissArmyKnife.getBitmapAsByteArray(SwissArmyKnife.decodeBase64(obj.getString("PROFILE_IMAGE")));
                            }
                        } catch (Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                        if (bProfileImage != null && bProfileImage.length > 0) {
                            c.put("ProfileImage", bProfileImage);
                        }

                        cv[i] = c;
                    }
                    //Add to database
                    getContentResolver().bulkInsert(ContactManagerContentProvider.REQUEST_RECEIVED_URI, cv);

                    //For Notification
                    if (dContacts == null || dContacts.getCount() > 0) {
                        dContacts.close();
                        return;
                    }
                    for (int i = 0; i < cv.length; i++) {
                        dContacts.moveToFirst();
                        boolean found = false;
                        while (!dContacts.isAfterLast()) {
                            if (cv[i].getAsString("UserID").equals(dContacts.getString(Contact.USER_ID))) {
                                found = true;
                            }
                            dContacts.moveToNext();
                        }

                        if (!found && chatapp.notifications.NotificationManager.CONTACT_MANAGER_TAB!= chatapp.notifications.NotificationManager.REQUEST_RECEIVED_TAB) {
                            chatapp.notifications.NotificationManager.showAddRequestNotification(cv[i].getAsString("ProfileName"),UndergroundService.this);
                        }
                    }
                    dContacts.close();
                } catch (JSONException e) {
                    Log.d("Websocket Message", e.getMessage());
                }
            }

            @Override
            public void requestSentList(JSONArray contacts) {
                try {
                    //Delete Previous Data
                    getContentResolver().delete(ContactManagerContentProvider.REQUEST_SENT_URI, null, null);
                    ContentValues[] cv = new ContentValues[contacts.length()];
                    for (int i = 0; i < contacts.length(); i++) {
                        ContentValues c = new ContentValues();
                        JSONObject obj = contacts.getJSONObject(i);
                        c.put("UserID", obj.getString("USER_ID"));
                        c.put("ProfileName", obj.getString("PROFILE_NAME"));
                        c.put("Quote", obj.getString("QUOTE"));

                        byte[] bProfileImage = null;
                        try {
                            if(!obj.getString("PROFILE_IMAGE").equals("")) {
                                bProfileImage = SwissArmyKnife.getBitmapAsByteArray(SwissArmyKnife.decodeBase64(obj.getString("PROFILE_IMAGE")));
                            }
                        } catch (Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                        if (bProfileImage != null && bProfileImage.length > 0) {
                            c.put("ProfileImage", bProfileImage);
                        }

                        cv[i] = c;
                    }
                    //Add to database
                    getContentResolver().bulkInsert(ContactManagerContentProvider.REQUEST_SENT_URI, cv);
                } catch (JSONException e) {
                    Log.d("Websocket Message", e.getMessage());
                }
            }

            @Override
            public void blockedContactList(JSONArray contacts) {
                try {
                    //Delete Previous Data
                    getContentResolver().delete(ContactManagerContentProvider.BLOCKED_CONTACT_URI, null, null);
                    ContentValues[] cv = new ContentValues[contacts.length()];
                    for (int i = 0; i < contacts.length(); i++) {
                        ContentValues c = new ContentValues();
                        JSONObject obj = contacts.getJSONObject(i);
                        c.put("UserID", obj.getString("USER_ID"));
                        c.put("ProfileName", obj.getString("PROFILE_NAME"));

                        cv[i] = c;
                    }
                    //Add to database
                    getContentResolver().bulkInsert(ContactManagerContentProvider.BLOCKED_CONTACT_URI, cv);
                } catch (JSONException e) {
                    Log.d("Websocket Message", e.getMessage());
                }
            }

            @Override
            public void searchProfiles(JSONArray contacts) {
                try {
                    ContentValues[] cv = new ContentValues[contacts.length()];

                    for (int i = 0; i < contacts.length(); i++) {
                        ContentValues c = new ContentValues();
                        JSONObject obj = contacts.getJSONObject(i);
                        Log.d("ProfileName", obj.getString("PROFILE_NAME"));
                        c.put("UserID", obj.getString("USER_ID"));
                        c.put("ProfileName", obj.getString("PROFILE_NAME"));
                        c.put("Quote", obj.getString("QUOTE"));
                        c.put("ContactType", "Others");
                        Log.d("worksss", "111");
                        byte[] bProfileImage = null;
                        try {
                            if(!obj.getString("PROFILE_IMAGE").equals("")) {
                                bProfileImage = SwissArmyKnife.getBitmapAsByteArray(SwissArmyKnife.decodeBase64(obj.getString("PROFILE_IMAGE")));
                            }
                        } catch (Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                        if (bProfileImage != null && bProfileImage.length > 0) {
                            c.put("ProfileImage", bProfileImage);
                        }

                        Log.d("worksss", "111");

                        cv[i] = c;
                    }
                    //Add to database
                    getContentResolver().bulkInsert(ContactManagerContentProvider.SEARCH_PROFILE_URI, cv);
                    Log.d("worksss", "111");
                } catch (JSONException e) {
                    Log.d("Websocket Message", e.getMessage());
                }
            }

            @Override
            public void searchProfile(JSONObject contact) {
                //Delete Previous Data
                getContentResolver().delete(ContactManagerContentProvider.SEARCH_PROFILE_URI, null, null);
                ContentValues cv = new ContentValues();
                try {
                    cv.put("UserID", contact.getString("USER_ID"));
                    cv.put("ProfileName", contact.getString("PROFILE_NAME"));
                    cv.put("Quote", contact.getString("QUOTE"));
                    byte[] bProfileImage = null;
                    try {
                        if(!contact.getString("PROFILE_IMAGE").equals("")) {
                            bProfileImage = SwissArmyKnife.getBitmapAsByteArray(SwissArmyKnife.decodeBase64(contact.getString("PROFILE_IMAGE")));
                        }
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                    if (bProfileImage != null && bProfileImage.length > 0) {
                        cv.put("ProfileImage", bProfileImage);
                    }
                    cv.put("ContactType", "Others");
                } catch (JSONException e) {
                    Log.d("Websocket Message", e.getMessage());
                }
                //Add to database
                getContentResolver().insert(ContactManagerContentProvider.SEARCH_PROFILE_URI, cv);
            }

            @Override
            public void addRequestEvent(ContentValues cv, boolean result) {
                Log.d("addRequestEvent", "" + result);
                if (!result) return;

                //Add contact to Request Sent
                getContentResolver().insert(ContactManagerContentProvider.REQUEST_SENT_URI, cv);

                //Send response to server
                addRequestResponse(cv.getAsString("UserID"));

                //Remove from search
                getContentResolver().delete(ContactManagerContentProvider.SEARCH_PROFILE_URI, "UserID=?", new String[]{cv.getAsString("UserID")});
            }

            @Override
            public void addRequestResponseEvent(String contactID, boolean result) {
                Log.d("addRequestResponseEvent", "" + result);
                if (!result) return;
            }

            @Override
            public void addRequestReceivedEvent(ContentValues cv) {
                Log.d("addRequestReceivedEvent", "Reached");

                //Add contact to Request Received
                getContentResolver().insert(ContactManagerContentProvider.REQUEST_RECEIVED_URI, cv);

                //Send response to server
                addRequestReceived(cv.getAsString("UserID"));

                //Show notification
                if(chatapp.notifications.NotificationManager.CONTACT_MANAGER_TAB!= chatapp.notifications.NotificationManager.REQUEST_RECEIVED_TAB) {
                    chatapp.notifications.NotificationManager.showAddRequestNotification(cv.getAsString("ProfileName"), UndergroundService.this);
                }
            }

            @Override
            public void acceptRequestEvent(String contactID, boolean result) {
                Log.d("acceptRequestEvent", "" + result);
                if (!result) return;

                //Read Contact
                Cursor contact = getContentResolver().query(ContactManagerContentProvider.REQUEST_RECEIVED_URI, null, "UserID=?", new String[]{contactID}, null);
                ContentValues c = new ContentValues();
                if (contact != null && contact.getCount() > 0) {
                    contact.moveToFirst();

                    c.put("UserID", contact.getString(RequestReceived.USER_ID));
                    c.put("ProfileName", contact.getString(RequestReceived.PROFILE_NAME));
                    c.put("Quote", contact.getString(RequestReceived.QUOTE));
                    c.put("ProfileImage", contact.getBlob(RequestReceived.PROFILE_IMAGE));

                    contact.close();
                }

                getContentResolver().delete(ContactManagerContentProvider.REQUEST_RECEIVED_URI, "UserID=?", new String[]{contactID});
                getContentResolver().insert(ContactManagerContentProvider.CONTACT_URI, c);
                //Send response to server
                acceptRequestResponse(contactID);
            }

            @Override
            public void acceptRequestResponseEvent(String contactID, boolean result) {
                Log.d("acceptRequestResponseEv", "" + result);
                if (!result) return;
            }

            @Override
            public void acceptRequestReceivedEvent(String contactID) {
                Log.d("acceptRequestReceivedEv", "Reached");

                //Read Contact
                Cursor contact = getContentResolver().query(ContactManagerContentProvider.REQUEST_SENT_URI, null, "UserID=?", new String[]{contactID}, null);
                ContentValues c = new ContentValues();
                String profileName = "";
                if (contact != null && contact.getCount() > 0) {
                    contact.moveToFirst();

                    c.put("UserID", contact.getString(RequestReceived.USER_ID));
                    c.put("ProfileName", contact.getString(RequestReceived.PROFILE_NAME));
                    c.put("Quote", contact.getString(RequestReceived.QUOTE));
                    c.put("ProfileImage", contact.getBlob(RequestReceived.PROFILE_IMAGE));
                    profileName = c.getAsString("ProfileName");

                    contact.close();
                }

                getContentResolver().delete(ContactManagerContentProvider.REQUEST_SENT_URI, "UserID=?", new String[]{contactID});
                getContentResolver().insert(ContactManagerContentProvider.CONTACT_URI, c);
                //Send response to server
                acceptRequestReceived(contactID);

                //Show notification
                if(chatapp.notifications.NotificationManager.CONTACT_MANAGER_TAB!= chatapp.notifications.NotificationManager.CONTACT_TAB) {
                    chatapp.notifications.NotificationManager.showAcceptRequestNotification(profileName, UndergroundService.this);
                }
            }

            @Override
            public void blockOtherEvent(ContentValues cv, boolean result) {
                Log.d("blockOtherEvent", "" + result);
                if (!result) return;

                //Add contact to Blocked Contact
                getContentResolver().insert(ContactManagerContentProvider.BLOCKED_CONTACT_URI, cv);

                //Send response to server
                blockOtherResponse(cv.getAsString("UserID"));

                //Remove from search
                getContentResolver().delete(ContactManagerContentProvider.SEARCH_PROFILE_URI, "UserID=?", new String[]{cv.getAsString("UserID")});

            }

            @Override
            public void blockOtherResponseEvent(String contactID, boolean result) {
                Log.d("blockOtherResponseEvent", "" + result);
                if (!result) return;
            }

            @Override
            public void blockContactEvent(String contactID, boolean result) {
                Log.d("blockContactEvent", "" + result);
                if (!result) return;

                //Read Contact
                Cursor contact = getContentResolver().query(ContactManagerContentProvider.CONTACT_URI, null, "UserID=?", new String[]{contactID}, null);
                ContentValues c = new ContentValues();
                if (contact != null && contact.getCount() > 0) {
                    contact.moveToFirst();

                    c.put("UserID", contact.getString(RequestReceived.USER_ID));
                    c.put("ProfileName", contact.getString(RequestReceived.PROFILE_NAME));

                    contact.close();
                }

                getContentResolver().delete(ContactManagerContentProvider.CONTACT_URI, "UserID=?", new String[]{contactID});
                getContentResolver().insert(ContactManagerContentProvider.BLOCKED_CONTACT_URI, c);
                //Send response to server
                blockContactResponse(contactID);
            }

            @Override
            public void blockContactResponseEvent(String contactID, boolean result) {
                Log.d("blockContactResponseEve", "" + result);
                if (!result) return;
            }

            @Override
            public void blockContactReceivedEvent(String contactID) {
                Log.d("blockContactReceivedEve", "Reached");

                getContentResolver().delete(ContactManagerContentProvider.CONTACT_URI, "UserID=?", new String[]{contactID});
                //Send response to server
                blockContactReceived(contactID);
            }

            @Override
            public void blockRequestReceivedEvent(String contactID, boolean result) {
                Log.d("blockRequestReceivedEve", "" + result);
                if (!result) return;

                //Read Contact
                Cursor contact = getContentResolver().query(ContactManagerContentProvider.REQUEST_RECEIVED_URI, null, "UserID=?", new String[]{contactID}, null);
                ContentValues c = new ContentValues();
                if (contact != null && contact.getCount() > 0) {
                    contact.moveToFirst();

                    c.put("UserID", contact.getString(RequestReceived.USER_ID));
                    c.put("ProfileName", contact.getString(RequestReceived.PROFILE_NAME));

                    contact.close();
                }

                getContentResolver().delete(ContactManagerContentProvider.REQUEST_RECEIVED_URI, "UserID=?", new String[]{contactID});
                getContentResolver().insert(ContactManagerContentProvider.BLOCKED_CONTACT_URI, c);
                //Send response to server
                blockRequestReceivedResponse(contactID);
            }

            @Override
            public void blockRequestReceivedResponseEvent(String contactID, boolean result) {
                Log.d("blockRequestReceivedRes", "" + result);
                if (!result) return;
            }

            @Override
            public void blockRequestReceivedReceivedEvent(String contactID) {
                Log.d("blockRequestReceivedRec", "Reached");

                getContentResolver().delete(ContactManagerContentProvider.REQUEST_SENT_URI, "UserID=?", new String[]{contactID});
                //Response to server
                blockRequestReceivedReceived(contactID);
            }

            @Override
            public void blockRequestSentEvent(String contactID, boolean result) {
                Log.d("blockRequestSentEvent", "" + result);
                if (!result) return;

                //Read Contact
                Cursor contact = getContentResolver().query(ContactManagerContentProvider.REQUEST_SENT_URI, null, "UserID=?", new String[]{contactID}, null);
                ContentValues c = new ContentValues();
                if (contact != null && contact.getCount() > 0) {
                    contact.moveToFirst();

                    c.put("UserID", contact.getString(RequestReceived.USER_ID));
                    c.put("ProfileName", contact.getString(RequestReceived.PROFILE_NAME));

                    contact.close();
                }

                getContentResolver().delete(ContactManagerContentProvider.REQUEST_SENT_URI, "UserID=?", new String[]{contactID});
                getContentResolver().insert(ContactManagerContentProvider.BLOCKED_CONTACT_URI, c);
                //Send response to server
                blockRequestSentResponse(contactID);
            }

            @Override
            public void blockRequestSentResponseEvent(String contactID, boolean result) {
                Log.d("blockRequestSentRespons", "" + result);
                if (!result) return;
            }

            @Override
            public void blockRequestSentReceivedEvent(String contactID) {
                Log.d("blockRequestSentReceive", "Reached");

                getContentResolver().delete(ContactManagerContentProvider.REQUEST_RECEIVED_URI, "UserID=?", new String[]{contactID});
                //Response to server
                blockRequestSentReceived(contactID);
            }

            @Override
            public void removeContactEvent(String contactID, boolean result) {
                Log.d("removeContactEvent", "" + result);
                if (!result) return;

                //Remove contact
                getContentResolver().delete(ContactManagerContentProvider.CONTACT_URI, "UserID=?", new String[]{contactID});

            }

            @Override
            public void removeContactReceivedEvent(String contactID, boolean result) {
                Log.d("removeContactReceivedEv", "" + result);
                if (!result) return;

                //Remove contact
                getContentResolver().delete(ContactManagerContentProvider.CONTACT_URI, "UserID=?", new String[]{contactID});
            }

            @Override
            public void removeRequestReceivedEvent(String contactID, boolean result) {
                Log.d("removeRequestReceivedEv", "" + result);
                if (!result) return;

                //Remove contact
                getContentResolver().delete(ContactManagerContentProvider.REQUEST_RECEIVED_URI, "UserID=?", new String[]{contactID});
            }

            @Override
            public void removeRequestReceivedReceivedEvent(String contactID) {
                Log.d("removeRequestReceivedRe", "Reached");

                //Remove contact
                getContentResolver().delete(ContactManagerContentProvider.REQUEST_SENT_URI, "UserID=?", new String[]{contactID});
            }

            @Override
            public void removeRequestSentEvent(String contactID, boolean result) {
                Log.d("removeRequestSentEvent", "" + result);
                if (!result) return;

                //Remove contact
                getContentResolver().delete(ContactManagerContentProvider.REQUEST_SENT_URI, "UserID=?", new String[]{contactID});
            }

            @Override
            public void removeRequestSentReceivedEvent(String contactID) {
                Log.d("removeRequestSentReceiv", "Reached");

                //Remove contact
                getContentResolver().delete(ContactManagerContentProvider.REQUEST_RECEIVED_URI, "UserID=?", new String[]{contactID});
            }

            @Override
            public void removeBlockEvent(String contactID, boolean result) {
                Log.d("removeBlockEvent", "" + result);
                if (!result) return;

                //Remove contact
                getContentResolver().delete(ContactManagerContentProvider.BLOCKED_CONTACT_URI, "UserID=?", new String[]{contactID});
            }

            //Dual chat events
            @Override
            public void chatListEvent(JSONArray contacts) {

            }

            @Override
            public void unreadChatListEvent(JSONArray contacts) {
                try {
                    List<String> contactIDs = new ArrayList<String>();
                    ContentValues[] cv = new ContentValues[contacts.length()];
                    contactIDs.clear();
                    for (int i = 0; i < contacts.length(); i++) {
                        ContentValues c = new ContentValues();
                        JSONObject obj = contacts.getJSONObject(i);
                        c.put("ServerChatID", obj.getLong("SERVER_CHAT_ID"));
                        c.put("SenderUserID", obj.getString("SENDER_USER_ID"));
                        c.put("ReceiverUserID", obj.getString("RECEIVER_USER_ID"));
                        c.put("MessageText", obj.getString("MESSAGE_TEXT"));
                        c.put("FilePath", "");

                        if(!contactIDs.contains(obj.getString("SENDER_USER_ID"))){
                            contactIDs.add(obj.getString("SENDER_USER_ID"));
                        }

                        String messageType = obj.getString("MESSAGE_TYPE");
                        int iMessageType;
                        switch (messageType) {
                            case "TEXT":
                                iMessageType = DualChat.IN_TEXT;
                                break;
                            case "IMAGE_TEXT":
                                iMessageType = DualChat.IN_IMAGE_TEXT;
                                break;
                            case "IMAGES_TEXT":
                                iMessageType = DualChat.IN_IMAGES_TEXT;
                                break;
                            case "CAMERA_IMAGE_TEXT":
                                iMessageType = DualChat.IN_CAMERA_IMAGE_TEXT;
                                break;
                            case "AUDIO_TEXT":
                                iMessageType = DualChat.IN_AUDIO_TEXT;
                                break;
                            case "AUDIOS_TEXT":
                                iMessageType = DualChat.IN_AUDIOS_TEXT;
                                break;
                            case "VOICE_TEXT":
                                iMessageType = DualChat.IN_VOICE_TEXT;
                                break;
                            case "VIDEO_TEXT":
                                iMessageType = DualChat.IN_VIDEO_TEXT;
                                break;
                            case "VIDIEOS_TEXT":
                                iMessageType = DualChat.IN_VIDIEOS_TEXT;
                                break;
                            case "CAMERA_VIDEO_TEXT":
                                iMessageType = DualChat.IN_CAMERA_VIDEO_TEXT;
                                break;
                            case "VOICE_CALL_SUMMERY_TEXT":
                                iMessageType = DualChat.IN_VOICE_CALL_SUMMERY_TEXT;
                                break;
                            case "VIDEO_CALL_SUMMARY_TEXT":
                                iMessageType = DualChat.IN_VIDEO_CALL_SUMMARY_TEXT;
                                break;
                            case "FILE_TEXT":
                                iMessageType = DualChat.IN_FILE_TEXT;
                                break;

                            default:
                                iMessageType = DualChat.IN_TEXT;
                        }
                        c.put("MessageType", iMessageType);

                        try {
                            c.put("MessageTime", SwissArmyKnife.getLocalDateTime(obj.getInt("SEND_TIME"), "yyyy-MM-dd HH:mm:ss"));
                        } catch (Exception e) {
                            Log.d("Date Error", e.getMessage());
                        }
                        c.put("SeenBy", DualChat.SERVER);


                        cv[i] = c;
                    }
                    //Add to database
                    getContentResolver().bulkInsert(DualChatContentProvider.DUAL_CHAT_URI, cv);

                    //For Notification with reply seen by
                    for (String contactID : contactIDs) {
                        Cursor dContacts = getContentResolver().query(ContactManagerContentProvider.CONTACT_URI, null, "UserID=?", new String[]{contactID}, null);
                        if (dContacts != null && dContacts.getCount() > 0) {
                            dContacts.moveToFirst();
                            String profileName = dContacts.getString(Contact.PROFILE_NAME);
                            int _id=dContacts.getInt(Contact.ID);

                            if(!chatapp.notifications.NotificationManager.DUAL_CHAT_ACTIVITY_CONTACT_ID.equals(contactID)) {
                                chatapp.notifications.NotificationManager.showNewDualChatNotification(_id,contactID, profileName, UndergroundService.this);
                            }
                        }
                        dContacts.close();
                    }

                    //let server know that the message is received
                    for (int i = 0; i < cv.length; i++) {
                        if(cv[i].getAsString("ReceiverUserID").equals(mUserID)) {
                            dualChatMessageSeenByReceiver(cv[i].getAsString("SenderUserID"), cv[i].getAsLong("ServerChatID"));
                        }
                    }

                } catch (JSONException e) {
                    Log.d("Websocket Message", e.getMessage());
                }
            }

            @Override
            public void chatSeenByServerEvent(long serverChatID, long clientChatID) {
                //Update with new ServerChatID and Seen by Server
                Log.d("clientservid",clientChatID+" "+serverChatID);
                ContentValues cv = new ContentValues();
                cv.put("ServerChatID", serverChatID);
                cv.put("SeenBy", DualChat.SERVER);
                getContentResolver().update(DualChatContentProvider.DUAL_CHAT_URI, cv, "_ID=?", new String[]{clientChatID + ""});
            }

            @Override
            public void chatSeenByReceiverEvent(long serverChatID) {
                //Update Seen by Server
                ContentValues cv = new ContentValues();
                cv.put("SeenBy", DualChat.RECEIVER);
                getContentResolver().update(DualChatContentProvider.DUAL_CHAT_URI, cv, "ServerChatID=?", new String[]{serverChatID + ""});
            }


            @Override
            public void chatReceivedEvent(long serverChatID, String senderUserID, String messageText, String messageType, long sendTime) {
                //Add new chat received
                int iMessageType;
                switch (messageType) {
                    case "TEXT":
                        iMessageType = DualChat.IN_TEXT;
                        break;
                    case "IMAGE_TEXT":
                        iMessageType = DualChat.IN_IMAGE_TEXT;
                        break;
                    case "IMAGES_TEXT":
                        iMessageType = DualChat.IN_IMAGES_TEXT;
                        break;
                    case "CAMERA_IMAGE_TEXT":
                        iMessageType = DualChat.IN_CAMERA_IMAGE_TEXT;
                        break;
                    case "AUDIO_TEXT":
                        iMessageType = DualChat.IN_AUDIO_TEXT;
                        break;
                    case "AUDIOS_TEXT":
                        iMessageType = DualChat.IN_AUDIOS_TEXT;
                        break;
                    case "VOICE_TEXT":
                        iMessageType = DualChat.IN_VOICE_TEXT;
                        break;
                    case "VIDEO_TEXT":
                        iMessageType = DualChat.IN_VIDEO_TEXT;
                        break;
                    case "VIDIEOS_TEXT":
                        iMessageType = DualChat.IN_VIDIEOS_TEXT;
                        break;
                    case "CAMERA_VIDEO_TEXT":
                        iMessageType = DualChat.IN_CAMERA_VIDEO_TEXT;
                        break;
                    case "VOICE_CALL_SUMMERY_TEXT":
                        iMessageType = DualChat.IN_VOICE_CALL_SUMMERY_TEXT;
                        break;
                    case "VIDEO_CALL_SUMMARY_TEXT":
                        iMessageType = DualChat.IN_VIDEO_CALL_SUMMARY_TEXT;
                        break;
                    case "FILE_TEXT":
                        iMessageType = DualChat.IN_FILE_TEXT;
                        break;

                    default:
                        iMessageType = DualChat.IN_TEXT;
                }

                ContentValues cv = new ContentValues();
                cv.put("ServerChatID", serverChatID);
                cv.put("SenderUserID", senderUserID);
                cv.put("ReceiverUserID", mUserID);
                cv.put("MessageText", messageText);
                cv.put("FilePath", "");
                cv.put("MessageType", iMessageType);
                try {
                    cv.put("MessageTime", SwissArmyKnife.getLocalDateTime(sendTime, "yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e) {
                    Log.d("Date Error", e.getMessage());
                }
                cv.put("SeenBy", DualChat.SERVER);
                getContentResolver().insert(DualChatContentProvider.DUAL_CHAT_URI, cv);

                //For Notification with reply seen by
                Cursor dContacts = getContentResolver().query(ContactManagerContentProvider.CONTACT_URI, null, "UserID=?", new String[]{cv.getAsString("SenderUserID")}, null);
                if (dContacts != null && dContacts.getCount() > 0) {
                    dContacts.moveToFirst();
                    String profileName = dContacts.getString(Contact.PROFILE_NAME);
                    int _id=dContacts.getInt(Contact.ID);
                    //Check if the activity is already open
                    if(!chatapp.notifications.NotificationManager.DUAL_CHAT_ACTIVITY_CONTACT_ID.equals(senderUserID)) {
                        //Show notification
                        chatapp.notifications.NotificationManager.showNewDualChatNotification(_id,senderUserID, profileName, UndergroundService.this);
                    }
                }
                dContacts.close();


                //let server know that the message is received
                dualChatMessageSeenByReceiver(senderUserID, serverChatID);
            }

            @Override
            public void chatDeletedEvent(long serverChatID, boolean result) {
                //Delete the chat from database
                getContentResolver().delete(DualChatContentProvider.DUAL_CHAT_URI, "ServerChatID=?", new String[]{serverChatID + ""});
            }


            //Reconnected if closed and internet connection
            @Override
            public void websocketClosed(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                if (SwissArmyKnife.isNetworkAvailable(getApplicationContext())) {
                    if (UndergroundService.isLoggedIn) {
                        mWebsocket.connectToServer();
                    }
                }
            }

        });

        //Register network change broadcast receiver
        IntentFilter filter1 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        IntentFilter filter2 = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        IntentFilter filter3 = new IntentFilter("android.net.wifi.STATE_CHANGE");
        registerReceiver(networkReceiver, filter1);
        registerReceiver(networkReceiver, filter2);
        registerReceiver(networkReceiver, filter3);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    //Dual Chat

    /*
    Return our Messenger interface for sending messages to
    the service by the clients.
    */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind done");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind done");
        return false;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy done");
        super.onDestroy();

        //Unregister network change boradvcast receiver
        unregisterReceiver(networkReceiver);

        //Stop Websocket connection
        mWebsocket.stopConnection();
    }


    private static void authenticate(String userID, String loginID, String deviceID) {
        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "AUTHENTICATE")
                    .put("USER_ID", userID)
                    .put("LOGIN_ID", loginID)
                    .put("DEVICE_ID", deviceID);


            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }

    }

    private static void getAllContacts() {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "GET_CONTACT_LIST")
                    .put("USER_ID", mUserID);


            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }
    }

    private static void getAllRequestReceived() {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "GET_REQUEST_RECEIVED_LIST")
                    .put("USER_ID", mUserID);


            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }
    }

    private static void getAllRequestSent() {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "GET_REQUEST_SENT_LIST")
                    .put("USER_ID", mUserID);


            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }
    }

    private static void getAllBlockedContacts() {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "GET_BLOCK_LIST")
                    .put("USER_ID", mUserID);


            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }
    }

    private static void searchProfile(String text, int start, int length) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        if (text.startsWith("@")) {
            try {
                json.put("TYPE", "SEARCH_PROFILE_BY_USER_ID")
                        .put("USER_ID", mUserID)
                        .put("SEARCH_USER_ID", text.substring(1));
            } catch (JSONException e) {

            }
        } else {
            try {
                json.put("TYPE", "SEARCH_PROFILE_BY_SEARCH_TEXT")
                        .put("USER_ID", mUserID)
                        .put("SEARCH_TEXT", text)
                        .put("START", start)
                        .put("LENGTH", length);

            } catch (JSONException e) {

            }

        }

        mWebsocket.sendToServer(json.toString());

    }


    //Websocket methods start

    //Commands
    public static void addRequest(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "ADD_REQUEST")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void acceptRequest(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "ACCEPT_REQUEST")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockContact(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_CONTACT")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockRequestReceived(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_REQUEST_RECEIVED")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockRequestSent(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_REQUEST_SENT")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockOther(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_OTHER")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void removeContact(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "REMOVE_CONTACT")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void removeRequestReceived(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "REMOVE_REQUEST_RECEIVED")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void removeRequestSent(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "REMOVE_REQUEST_SENT")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void removeBlock(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "REMOVE_BLOCK")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void addRequestResponse(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "ADD_REQUEST_RESPONSE")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void acceptRequestResponse(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "ACCEPT_REQUEST_RESPONSE")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockContactResponse(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_CONTACT_RESPONSE")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockRequestReceivedResponse(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_REQUEST_RECEIVED_RESPONSE")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockRequestSentResponse(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_REQUEST_SENT_RESPONSE")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockOtherResponse(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_OTHER_RESPONSE")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void addRequestReceived(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "ADD_REQUEST_RECEIVED")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void acceptRequestReceived(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "ACCEPT_REQUEST_RECEIVED")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockContactReceived(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_CONTACT_RECEIVED")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockRequestReceivedReceived(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_REQUEST_RECEIVED_RECEIVED")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void blockRequestSentReceived(String contactID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "BLOCK_REQUEST_SENT_RECEIVED")
                    .put("USER_ID", mUserID)
                    .put("CONTACT_ID", contactID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void sendDualChatMessage(String receiverUserID, long clientChatID, String messageText, String messageType, String filePath) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "CHAT_MESSAGE_BY_SENDER")
                    .put("USER_ID", mUserID)
                    .put("RECEIVER_USER_ID", receiverUserID)
                    .put("CLIENT_CHAT_ID", clientChatID)
                    .put("MESSAGE_TEXT", messageText)
                    .put("MESSAGE_TYPE", messageType);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    public static void dualChatMessageSeenByReceiver(String senderUserID, long serverChatID) {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "CHAT_MESSAGE_SEEN_BY_RECEIVER")
                    .put("USER_ID", mUserID)
                    .put("SERVER_CHAT_ID", serverChatID)
                    .put("SENDER_USER_ID", senderUserID);

        } catch (JSONException e) {

        }

        mWebsocket.sendToServer(json.toString());
    }

    private static void getAllUnreadDualChats() {
        if (!isLoggedIn) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "GET_ALL_UNREAD_CHATS")
                    .put("USER_ID", mUserID);


            mWebsocket.sendToServer(json.toString());
        } catch (JSONException e) {

        }
    }

    private static void sendAllUnreadDualChats(ContentResolver cr) {
        if (!isLoggedIn) {
            return;
        }

        Cursor unreadChats = cr.query(DualChatContentProvider.DUAL_CHAT_URI, null, "SenderUserID=? And SeenBy=?", new String[]{mUserID, DualChat.SENDER + ""}, null);
        if (unreadChats != null && unreadChats.getCount() > 0) {
            unreadChats.moveToFirst();

            while (!unreadChats.isAfterLast()) {
                JSONObject json = new JSONObject();
                try {
                    json.put("TYPE", "CHAT_MESSAGE_BY_SENDER")
                            .put("USER_ID", mUserID)
                            .put("RECEIVER_USER_ID", unreadChats.getString(DualChat.RECEIVER_USER_ID))
                            .put("CLIENT_CHAT_ID", unreadChats.getLong(DualChat.ID))
                            .put("MESSAGE_TEXT", unreadChats.getString(DualChat.MESSAGE_TEXT))
                            .put("MESSAGE_TYPE", unreadChats.getString(DualChat.MESSAGE_TYPE));

                } catch (JSONException e) {

                }

                mWebsocket.sendToServer(json.toString());

                unreadChats.moveToNext();
            }
        }
        unreadChats.close();

    }

    private static void isMessagesSeenByReceiver(ContentResolver cr) {
        if (!isLoggedIn) {
            return;
        }

        Cursor unreadChats = cr.query(DualChatContentProvider.DUAL_CHAT_URI, null, "SenderUserID=? And SeenBy=?", new String[]{mUserID, DualChat.SERVER + ""}, null);
        if (unreadChats != null && unreadChats.getCount() > 0) {
            unreadChats.moveToFirst();

            while (!unreadChats.isAfterLast()) {
                JSONObject json = new JSONObject();
                try {
                    json.put("TYPE", "IS_CHAT_MESSAGE_SEEN_BY_RECEIVER")
                            .put("USER_ID", mUserID)
                            .put("SERVER_CHAT_ID", unreadChats.getLong(DualChat.SERVER_CHAT_ID));

                } catch (JSONException e) {

                }

                mWebsocket.sendToServer(json.toString());

                unreadChats.moveToNext();
            }
        }
        unreadChats.close();

    }
    //Websocket methods end
}
