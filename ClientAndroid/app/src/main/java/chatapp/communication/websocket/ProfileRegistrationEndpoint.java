package chatapp.communication.websocket;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chatapp.extras.GZip;

/**
 * Created by Sely on 26-Aug-16.
 */
public class ProfileRegistrationEndpoint {

    private static final String SERVER = "ws://192.168.1.4:8080/Frontend-war/profileregistration";
    private static final int TIMEOUT = 5000;

    private WebSocket mWebSocket;
    private List<IProfileRegistrationListener> listenerList= new ArrayList<>();

    public void connectToServer() {
        try {
            mWebSocket=new WebSocketFactory()
                    .setConnectionTimeout(TIMEOUT)
                    .createSocket(SERVER)
                    .addListener(new WebSocketAdapter() {
                        // A text message arrived from the server.
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                            super.onConnected(websocket, headers);
                            Log.d("Websocket","Connected");
                        }

                        public void onTextMessage(WebSocket websocket, String message) {

                        }

                        @Override
                        public void onBinaryMessage(WebSocket websocket, byte[] message) throws Exception {
                            super.onBinaryMessage(websocket, message);

                            try {
                                JSONObject json = new JSONObject(GZip.decompress(message));
                                switch(json.getString("TYPE")){
                                    case "CHECK_USER_ID" :
                                    {
                                        boolean found = json.optBoolean("FOUND");
                                        String userID= json.getString("USER_ID");
                                        for (IProfileRegistrationListener listener : listenerList) {
                                            listener.userIDCheckedEvent(found,userID);
                                        }

                                    }
                                    break;

                                    case "CHECK_PHONE_NUMBER" :
                                    {
                                        boolean found = json.optBoolean("FOUND");
                                        String phoneNumber= json.getString("PHONE_NUMBER");
                                        for (IProfileRegistrationListener listener : listenerList) {
                                            listener.phoneNumberCheckedEvent(found,phoneNumber);
                                        }

                                    }
                                    break;

                                    case "REGISTER_USER" :
                                    {
                                        boolean result = json.optBoolean("RESULT");
                                        String userID= json.getString("USER_ID");
                                        String loginID= json.getString("LOGIN_ID");

                                        for (IProfileRegistrationListener listener : listenerList) {
                                            listener.registerUserEvent(result,userID,loginID);
                                        }

                                    }
                                    break;

                                    case "CONTACTS" :
                                    {
                                        boolean result = json.optBoolean("RESULT");
                                        String userID= json.getString("USER_ID");

                                        for (IProfileRegistrationListener listener : listenerList) {
                                            listener.contactUploadEvent(result,userID);
                                        }

                                    }
                                    break;

                                    case "CREATE_PROFILE" :
                                    {
                                        boolean result = json.optBoolean("RESULT");

                                        for (IProfileRegistrationListener listener : listenerList) {
                                            listener.profileCreatedEvent(result);
                                        }

                                    }
                                    break;

                                    case "RECOVER_BY_PHONE_NUMBER" :
                                    {
                                        boolean result = json.optBoolean("RESULT");

                                        for (IProfileRegistrationListener listener : listenerList) {
                                            listener.profileRecoveredEvent(result, json.getString("USER_ID"), json.getString("LOGIN_ID"));
                                        }

                                    }
                                    break;

                                    case "RECOVER_BY_USER_ID" :
                                    {
                                        boolean result = json.optBoolean("RESULT");

                                        for (IProfileRegistrationListener listener : listenerList) {
                                            listener.profileRecoveredEvent(result, json.getString("USER_ID"), json.getString("LOGIN_ID"));
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
                        }

                        @Override
                        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                            super.onError(websocket, cause);
                            Log.d("Websocket","Error");
                        }
                    })
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListener(IProfileRegistrationListener listener){
        this.listenerList.add(listener);
    }

    public void removeListener(IProfileRegistrationListener listener){
        this.listenerList.remove(listener);
    }

    public void stopConnection(){
        this.listenerList.clear();
        mWebSocket.disconnect();
    }

    public void sendToServer(String data){
        try {
            mWebSocket.sendBinary(GZip.compress(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface IProfileRegistrationListener{
        public void userIDCheckedEvent(boolean found, String userID);
        public void phoneNumberCheckedEvent(boolean found, String phoneNumber);
        public void registerUserEvent(boolean result, String userID, String loginID);
        public void contactUploadEvent(boolean result, String userID);
        public void profileCreatedEvent(boolean result);
        public void profileRecoveredEvent(boolean result, String userID, String loginID);

    }
}
