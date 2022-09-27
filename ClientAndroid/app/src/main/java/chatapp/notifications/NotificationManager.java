package chatapp.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import chatapp.client.R;
import chatapp.contacts.ContactManagerActivity;
import chatapp.dashboard.fragments.dual.DualChatActivity;
import chatapp.extras.SwissArmyKnife;
import chatapp.services.UndergroundService;
import chatapp.storage.tables.DualChat;
import chatapp.storage.tables.contentproviders.DualChatContentProvider;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Sely on 24-Oct-16.
 */

public final class NotificationManager {
    //Contact Manager Tab constants for notifications
    public static final int NONE =0;
    public static final int CONTACT_TAB =1;
    public static final int REQUEST_RECEIVED_TAB =2;
    //Activity status members for notification management
    public static String DUAL_CHAT_ACTIVITY_CONTACT_ID="";
    public static int CONTACT_MANAGER_TAB=NONE;

    public static final String DUAL_CHAT_GROUP="DUAL_CHAT_GROUP";
    public static final String ADD_REQUEST_GROUP="ADD_REQUEST_GROUP";
    public static final String ACCEPT_REQUEST_GROUP="ACCEPT_REQUEST_GROUP";

    public static void showAddRequestNotification(String userName, UndergroundService us) {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = Uri.parse("android.resource://" + us.getPackageName() +"/raw/message_received");

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(us, ContactManagerActivity.class);
        Bundle b = new Bundle();
        b.putString("OPEN", "REQUEST_RECEIVED_LIST");
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(us, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification notification = new NotificationCompat.Builder(us.getBaseContext())

                .setContentTitle("Request Received!")
                .setContentText("Request Sent by " + userName)
                .setSmallIcon(R.drawable.ic_add)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                //.addAction(R.drawable.ninja, "View", pIntent)
                //.addAction(0, "Remind", pIntent)
                .build();

        // Cancel the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) us.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int)Calendar.getInstance().getTimeInMillis(), notification);


    }

    public static void showAcceptRequestNotification(String userName, UndergroundService us) {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = Uri.parse("android.resource://" + us.getPackageName() +"/raw/message_received");

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(us, ContactManagerActivity.class);
        Bundle b = new Bundle();
        b.putString("OPEN", "CONTACT_LIST");
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(us, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification notification = new NotificationCompat.Builder(us.getBaseContext())

                .setContentTitle("Request Accepted!")
                .setContentText("Accepted by " + userName)
                .setSmallIcon(R.drawable.ic_add)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                //.addAction(R.drawable.ninja, "View", pIntent)
                //.addAction(0, "Remind", pIntent)
                .build();

        // Cancel the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) us.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int)Calendar.getInstance().getTimeInMillis(), notification);
    }

    public static void showNewDualChatNotification(int chatID, String contactID, String profileName, UndergroundService us) {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = Uri.parse("android.resource://" + us.getPackageName() +"/raw/message_received");
        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(us, DualChatActivity.class);
        Bundle b = new Bundle();
        b.putString("CONTACT_ID", contactID);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(us, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        int count=0;
        Cursor dc = us.getContentResolver().query(DualChatContentProvider.DUAL_CHAT_URI, null, "SenderUserID=? And SeenBy=?", new String[]{contactID, DualChat.SERVER + ""}, null);

        if(dc!=null && dc.getCount()>0){
            count=dc.getCount();
            dc.moveToFirst();
            int inc=0;
            while (!dc.isAfterLast()&&inc>6){
                String message = dc.getString(DualChat.MESSAGE_TEXT);
                inboxStyle.addLine(message);
                dc.moveToNext();
                ++inc;
            }

        }
        dc.close();


        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification notification = new NotificationCompat.Builder(us.getBaseContext())

                .setContentTitle(count+" New Message from "+profileName)
                .setContentText(profileName)
                .setSmallIcon(R.drawable.ic_dual_header)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .setStyle(inboxStyle)
                .setGroup(DUAL_CHAT_GROUP)
                //.setGroupSummary(true)
                //.addAction(R.drawable.ninja, "View", pIntent)
                //.addAction(0, "Remind", pIntent)
                .build();
        Log.d("works","here22");
        // Cancel the notification after its selected

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) us.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(chatID, notification);
        Log.d("works","here1");
    }
}
