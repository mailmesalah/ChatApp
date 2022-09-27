package chatapp.communication.websocket;

import android.content.ContentValues;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chatapp.extras.GZip;
import chatapp.extras.SwissArmyKnife;

/**
 * Created by Sely on 18-Sep-16.
 */
public class UndergroundEndpoint {

    private static final String SERVER = "ws://192.168.1.4:8080/Frontend-war/undergroundcommunicator";
    private static final int TIMEOUT = 5000;

    private static WebSocket mWebSocket;
    private static List<IUndergroundWebsocketListener> listenerList = new ArrayList<>();

    public static void connectToServer() {
        Log.d("connect to", "Call to connect");
        try {
            mWebSocket = new WebSocketFactory()
                    .setConnectionTimeout(TIMEOUT)
                    .createSocket(SERVER)
                    .addListener(new WebSocketAdapter() {
                        // A text message arrived from the server.
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                            super.onConnected(websocket, headers);
                            Log.d("Websocket", "Connected");
                        }

                        public void onTextMessage(WebSocket websocket, String message) {

                        }

                        @Override
                        public void onBinaryMessage(WebSocket websocket, byte[] message) throws Exception {
                            super.onBinaryMessage(websocket, message);

                            try {
                                JSONObject json = new JSONObject(GZip.decompress(message));
                                Log.d("Websocket Message", json.getString("TYPE"));
                                switch (json.getString("TYPE")) {
                                    case "AUTHENTICATE": {
                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.authenticate();
                                        }

                                    }
                                    break;

                                    case "AUTHENTICATE_RESPONSE": {
                                        boolean isLogged = json.getBoolean("RESULT");
                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.authenticated(isLogged);
                                        }

                                    }
                                    break;

                                    case "GET_CONTACT_LIST": {
                                        JSONArray contacts = json.getJSONArray("CONTACTS");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.contactList(contacts);
                                        }

                                    }
                                    break;

                                    case "GET_REQUEST_RECEIVED_LIST": {
                                        JSONArray contacts = json.getJSONArray("CONTACTS");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.requestReceivedList(contacts);
                                        }
                                    }
                                    break;

                                    case "GET_REQUEST_SENT_LIST": {
                                        JSONArray contacts = json.getJSONArray("CONTACTS");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.requestSentList(contacts);
                                        }

                                    }
                                    break;

                                    case "GET_BLOCK_LIST": {
                                        JSONArray contacts = json.getJSONArray("CONTACTS");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockedContactList(contacts);
                                        }

                                    }
                                    break;

                                    case "SEARCH_PROFILE_BY_SEARCH_TEXT": {
                                        JSONArray contacts = json.getJSONArray("PROFILES");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.searchProfiles(contacts);
                                        }

                                    }
                                    break;

                                    case "SEARCH_PROFILE_BY_USER_ID": {
                                        JSONObject contact = json.getJSONObject("PROFILE");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.searchProfile(contact);
                                        }

                                    }
                                    break;

                                    //Commands
                                    case "ADD_REQUEST": {
                                        ContentValues c = new ContentValues();
                                        c.put("UserID", json.getString("CONTACT_ID"));
                                        c.put("ProfileName", json.getString("PROFILE_NAME"));
                                        c.put("Quote", json.getString("QUOTE"));
                                        byte[] bProfileImage = null;
                                        try {
                                            if(!json.getString("PROFILE_IMAGE").equals("")) {
                                                bProfileImage = SwissArmyKnife.getBitmapAsByteArray(SwissArmyKnife.decodeBase64(json.getString("PROFILE_IMAGE")));
                                            }
                                        } catch (Exception e) {
                                            Log.d("Error", e.getMessage());
                                        }
                                        if (bProfileImage != null && bProfileImage.length > 0) {
                                            c.put("ProfileImage", bProfileImage);
                                        }

                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.addRequestEvent(c, result);
                                        }
                                    }
                                    break;

                                    case "ADD_REQUEST_RESPONSE": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.addRequestResponseEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "ADD_REQUEST_RECEIVED": {
                                        ContentValues c = new ContentValues();
                                        c.put("UserID", json.getString("CONTACT_ID"));
                                        c.put("ProfileName", json.getString("PROFILE_NAME"));
                                        c.put("Quote", json.getString("QUOTE"));

                                        byte[] bProfileImage = null;
                                        try {
                                            if(!json.getString("PROFILE_IMAGE").equals("")) {
                                                bProfileImage = SwissArmyKnife.getBitmapAsByteArray(SwissArmyKnife.decodeBase64(json.getString("PROFILE_IMAGE")));
                                            }
                                        } catch (Exception e) {
                                            Log.d("Error", e.getMessage());
                                        }
                                        if (bProfileImage != null && bProfileImage.length > 0) {
                                            c.put("ProfileImage", bProfileImage);
                                        }

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.addRequestReceivedEvent(c);
                                        }
                                    }
                                    break;

                                    case "ACCEPT_REQUEST": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.acceptRequestEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "ACCEPT_REQUEST_RESPONSE": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.acceptRequestResponseEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "ACCEPT_REQUEST_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.acceptRequestReceivedEvent(contactID);
                                        }
                                    }
                                    break;

                                    case "BLOCK_OTHER": {
                                        ContentValues c = new ContentValues();
                                        c.put("UserID", json.getString("CONTACT_ID"));
                                        c.put("ProfileName", json.getString("PROFILE_NAME"));

                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockOtherEvent(c, result);
                                        }
                                    }
                                    break;

                                    case "BLOCK_OTHER_RESPONSE": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockOtherResponseEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "BLOCK_CONTACT": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockContactEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "BLOCK_CONTACT_RESPONSE": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockContactResponseEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "BLOCK_CONTACT_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockContactReceivedEvent(contactID);
                                        }
                                    }
                                    break;

                                    case "BLOCK_REQUEST_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockRequestReceivedEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "BLOCK_REQUEST_RECEIVED_RESPONSE": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockRequestReceivedResponseEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "BLOCK_REQUEST_RECEIVED_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockRequestReceivedReceivedEvent(contactID);
                                        }
                                    }
                                    break;

                                    case "BLOCK_REQUEST_SENT": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockRequestSentEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "BLOCK_REQUEST_SENT_RESPONSE": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockRequestSentResponseEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "BLOCK_REQUEST_SENT_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.blockRequestSentReceivedEvent(contactID);
                                        }
                                    }
                                    break;

                                    case "REMOVE_CONTACT": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.removeContactEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "REMOVE_CONTACT_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.removeContactReceivedEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "REMOVE_REQUEST_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.removeRequestReceivedEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "REMOVE_REQUEST_RECEIVED_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.removeRequestReceivedReceivedEvent(contactID);
                                        }
                                    }
                                    break;

                                    case "REMOVE_REQUEST_SENT": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.removeRequestSentEvent(contactID, result);
                                        }
                                    }
                                    break;

                                    case "REMOVE_REQUEST_SENT_RECEIVED": {
                                        String contactID = json.getString("CONTACT_ID");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.removeRequestSentReceivedEvent(contactID);
                                        }
                                    }
                                    break;

                                    case "REMOVE_BLOCK": {
                                        String contactID = json.getString("CONTACT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.removeBlockEvent(contactID, result);
                                        }
                                    }
                                    break;


                                    //Dual Chat Commands
                                    case "GET_ALL_CHATS_MORE": {
                                        JSONArray chats = json.getJSONArray("CHATS");
                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.chatListEvent(chats);
                                        }
                                    }
                                    break;

                                    case "GET_ALL_UNREAD_CHATS": {
                                        JSONArray chats = json.getJSONArray("CHATS");
                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.unreadChatListEvent(chats);
                                        }
                                    }
                                    break;

                                    case "CHAT_MESSAGE_SEEN_BY_SERVER": {
                                        long serverChatID = json.getLong("SERVER_CHAT_ID");
                                        long clientChatID = json.getLong("CLIENT_CHAT_ID");
                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.chatSeenByServerEvent(serverChatID, clientChatID);
                                        }
                                    }
                                    break;

                                    case "CHAT_MESSAGE_TO_RECEIVER": {
                                        long serverChatID = json.getLong("SERVER_CHAT_ID");
                                        String senderUserID = json.getString("SENDER_USER_ID");
                                        String messageText = json.getString("MESSAGE_TEXT");
                                        String messageType = json.getString("MESSAGE_TYPE");
                                        long sendTime = json.getLong("SEND_TIME");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.chatReceivedEvent(serverChatID, senderUserID, messageText, messageType, sendTime);
                                        }
                                    }
                                    break;

                                    case "CHAT_MESSAGE_SEEN_BY_RECEIVER": {
                                        long serverChatID = json.getLong("SERVER_CHAT_ID");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.chatSeenByReceiverEvent(serverChatID);
                                        }
                                    }
                                    break;

                                    case "CHAT_MESSAGE_DELETED_BY_SENDER": {
                                        long serverChatID = json.getLong("SERVER_CHAT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.chatDeletedEvent(serverChatID, result);
                                        }
                                    }
                                    break;

                                    case "CHAT_MESSAGE_DELETED_BY_RECEIVER": {
                                        long serverChatID = json.getLong("SERVER_CHAT_ID");
                                        boolean result = json.getBoolean("RESULT");

                                        for (IUndergroundWebsocketListener listener : listenerList) {
                                            listener.chatDeletedEvent(serverChatID, result);
                                        }
                                    }
                                    break;

                                    default:
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);

                            //Let listeners know connection closed
                            for (IUndergroundWebsocketListener listener : listenerList) {
                                listener.websocketClosed(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                            }
                        }

                        @Override
                        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                            super.onError(websocket, cause);
                            Log.d("Websocket", "Error" + cause);
                        }
                    })
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListener(IUndergroundWebsocketListener listener) {
        this.listenerList.add(listener);
    }

    public void removeListener(IUndergroundWebsocketListener listener) {
        this.listenerList.remove(listener);
    }

    public void stopConnection() {
        this.listenerList.clear();
        mWebSocket.disconnect();
    }

    public void sendToServer(String data) {
        try {
            mWebSocket.sendBinary(GZip.compress(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface IUndergroundWebsocketListener {

        public void authenticate();

        public void authenticated(boolean result);

        public void contactList(JSONArray contacts);

        public void requestReceivedList(JSONArray contacts);

        public void requestSentList(JSONArray contacts);

        public void blockedContactList(JSONArray contacts);

        public void searchProfiles(JSONArray contacts);

        public void searchProfile(JSONObject contact);

        //Command Event
        public void addRequestEvent(ContentValues cv, boolean result);

        public void addRequestResponseEvent(String contactID, boolean result);

        public void addRequestReceivedEvent(ContentValues cv);

        public void acceptRequestEvent(String contactID, boolean result);

        public void acceptRequestResponseEvent(String contactID, boolean result);

        public void acceptRequestReceivedEvent(String contactID);

        public void blockOtherEvent(ContentValues cv, boolean result);

        public void blockOtherResponseEvent(String contactID, boolean result);

        public void blockContactEvent(String contactID, boolean result);

        public void blockContactResponseEvent(String contactID, boolean result);

        public void blockContactReceivedEvent(String contactID);

        public void blockRequestReceivedEvent(String contactID, boolean result);

        public void blockRequestReceivedResponseEvent(String contactID, boolean result);

        public void blockRequestReceivedReceivedEvent(String contactID);

        public void blockRequestSentEvent(String contactID, boolean result);

        public void blockRequestSentResponseEvent(String contactID, boolean result);

        public void blockRequestSentReceivedEvent(String contactID);

        public void removeContactEvent(String contactID, boolean result);

        public void removeContactReceivedEvent(String contactID, boolean result);

        public void removeRequestReceivedEvent(String contactID, boolean result);

        public void removeRequestReceivedReceivedEvent(String contactID);

        public void removeRequestSentEvent(String contactID, boolean result);

        public void removeRequestSentReceivedEvent(String contactID);

        public void removeBlockEvent(String contactID, boolean result);

        //Dual Chat commands
        public void chatListEvent(JSONArray contacts);

        public void unreadChatListEvent(JSONArray contacts);

        public void chatSeenByServerEvent(long serverChatID, long clientChatID);

        public void chatSeenByReceiverEvent(long serverChatID);

        public void chatReceivedEvent(long serverChatID, String senderUserID, String messageText, String messageType, long sendTime);

        public void chatDeletedEvent(long serverChatID, boolean result);


        //Websocket Alert events
        public void websocketClosed(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer);
    }
}
