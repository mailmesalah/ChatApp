package chatapp.storage.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sely on 14-Oct-16.
 */
public class DualChat {

    //Fields
    public final static int ID=0;
    public final static int SERVER_CHAT_ID=1;
    public final static int SENDER_USER_ID=2;
    public final static int RECEIVER_USER_ID=3;
    public final static int MESSAGE_TEXT=4;
    public final static int FILE_PATH=5;
    public final static int MESSAGE_TYPE=6;
    public final static int MESSAGE_TIME=7;
    public final static int SEEN_BY=8;

    //Seen by
    public final static int SENDER=0;
    public final static int SERVER=1;
    public final static int RECEIVER=2;

    //Message Type
    public final static int IN_TEXT=0;
    public final static int IN_IMAGE_TEXT=1;
    public final static int IN_IMAGES_TEXT=2;
    public final static int IN_CAMERA_IMAGE_TEXT=3;
    public final static int IN_AUDIO_TEXT=4;
    public final static int IN_AUDIOS_TEXT=5;
    public final static int IN_VOICE_TEXT=6;
    public final static int IN_VIDEO_TEXT=7;
    public final static int IN_VIDIEOS_TEXT=8;
    public final static int IN_CAMERA_VIDEO_TEXT=9;
    public final static int IN_VOICE_CALL_SUMMERY_TEXT=10;
    public final static int IN_VIDEO_CALL_SUMMARY_TEXT=11;
    public final static int IN_FILE_TEXT=12;
    public final static int OUT_TEXT=13;
    public final static int OUT_IMAGE_TEXT=14;
    public final static int OUT_IMAGES_TEXT=15;
    public final static int OUT_CAMERA_IMAGE_TEXT=16;
    public final static int OUT_AUDIO_TEXT=17;
    public final static int OUT_AUDIOS_TEXT=18;
    public final static int OUT_VOICE_TEXT=19;
    public final static int OUT_VIDEO_TEXT=20;
    public final static int OUT_VIDIEOS_TEXT=21;
    public final static int OUT_CAMERA_VIDEO_TEXT=22;
    public final static int OUT_VOICE_CALL_SUMMERY_TEXT=23;
    public final static int OUT_VIDEO_CALL_SUMMARY_TEXT=24;
    public final static int OUT_FILE_TEXT=25;

    private long _id;
    private long serverChatID;
    private String senderUserID;
    private String receiverUserID;
    private String messageText;
    private String filePath;
    private int messageType;
    private String messageTime;
    private int seenBy;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public long getServerChatID() {
        return serverChatID;
    }

    public void setServerChatID(long serverChatID) {
        this.serverChatID = serverChatID;
    }

    public String getSenderUserID() {
        return senderUserID;
    }

    public void setSenderUserID(String senderUserID) {
        this.senderUserID = senderUserID;
    }

    public String getReceiverUserID() {
        return receiverUserID;
    }

    public void setReceiverUserID(String receiverUserID) {
        this.receiverUserID = receiverUserID;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public int getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(int seenBy) {
        this.seenBy = seenBy;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL("Create Table DualChat(_ID Integer Primary Key autoincrement, ServerChatID Integer, SenderUserID Text Not Null, ReceiverUserID Text Not Null, MessageText Text, FilePath Text, MessageType Integer, MessageTime DateTime, SeenBy Integer);");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // Drop older table if existed
        database.execSQL("Drop Table If Exists DualChat;");
        onCreate(database);
    }

}
