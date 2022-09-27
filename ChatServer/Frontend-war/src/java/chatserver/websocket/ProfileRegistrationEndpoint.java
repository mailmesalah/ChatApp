/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.websocket;

import chatserver.database.Profile;
import chatserver.database.Register;
import chatserver.extras.MultiTool;
import chatserver.registration.ProfileRegistration;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Sely
 */
@Stateful
@ServerEndpoint(value = "/profileregistration")
public class ProfileRegistrationEndpoint {

    private Session mClient;

    @EJB
    private ProfileRegistration profileRegEJB;

    @OnMessage
    public String onMessage(String message, Session session) throws IOException, EncodeException {

        return null;
    }

    @OnMessage
    public byte[] OnMessage(byte[] message, Session session) {

        try {
            JsonObject json = Json.createReader(new StringReader(MultiTool.decompress(message))).readObject();
            System.out.println("Client send message : " + json.getString("TYPE"));

            switch (json.getString("TYPE")) {
                case "CHECK_USER_ID": {
                    String userID = json.getString("USER_ID");

                    String data = Json.createObjectBuilder()
                            .add("TYPE", "CHECK_USER_ID")
                            .add("USER_ID", userID)
                            .add("FOUND", profileRegEJB.isUserIDUsed(userID))
                            .build().toString();

                    mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    break;

                }

                case "CHECK_PHONE_NUMBER": {
                    String phoneNumber = json.getString("PHONE_NUMBER");

                    String data = Json.createObjectBuilder()
                            .add("TYPE", "CHECK_PHONE_NUMBER")
                            .add("PHONE_NUMBER", phoneNumber)
                            .add("FOUND", profileRegEJB.isPhoneNumberUsed(phoneNumber))
                            .build().toString();

                    mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    break;

                }

                case "REGISTER_USER": {
                    String userID = json.getString("USER_ID");
                    String phoneNumber = json.getString("PHONE_NUMBER");
                    String deviceID = json.getString("DEVICE_ID");
                    String password = json.getString("PASSWORD");

                    Register result = profileRegEJB.createRegister(userID, phoneNumber, deviceID, password);
                    String data = "";
                    if (result != null) {
                        data = Json.createObjectBuilder()
                                .add("TYPE", "REGISTER_USER")
                                .add("USER_ID", result.getUserID())
                                .add("LOGIN_ID", result.getLoginID())
                                .add("RESULT", true)
                                .build().toString();

                    } else {
                        data = Json.createObjectBuilder()
                                .add("TYPE", "REGISTER_USER")
                                .add("USER_ID", userID)
                                .add("LOGIN_ID", "")
                                .add("RESULT", false)
                                .build().toString();

                    }
                    mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    break;

                }

                case "CONTACTS": {
                    Map<String, String> contactMap = new HashMap<>();
                    String userID = json.getString("USER_ID");
                    JsonArray contacts = json.getJsonArray("CONTACTS");

                    for (Iterator<JsonValue> it = contacts.iterator(); it.hasNext();) {
                        JsonObject contact = (JsonObject) it.next();
                        contactMap.put(contact.getString("PHONE_NUMBER"), contact.getString("USER"));
                    }

                    boolean result = profileRegEJB.createPhoneContacts(userID, contactMap);
                    String data = Json.createObjectBuilder()
                            .add("TYPE", "CONTACTS")
                            .add("USER_ID", userID)
                            .add("RESULT", result)
                            .build().toString();

                    mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    break;

                }

                case "CREATE_PROFILE": {
                    String userID = json.getString("USER_ID");
                    String profileName = json.getString("PROFILE_NAME");
                    String quote = json.getString("QUOTE");
                    String sProfileImage = json.getString("PROFILE_IMAGE");

                    byte[] bProfileImage=null;
                    if(!"".equals(sProfileImage)){
                        bProfileImage = DatatypeConverter.parseBase64Binary(sProfileImage);
                    }
                    //Base64.getDecoder().decode(sProfileImage.replace("/",""));

                    Profile profile = profileRegEJB.createProfile(userID, profileName, bProfileImage, quote);
                    String data = Json.createObjectBuilder()
                            .add("TYPE", "CREATE_PROFILE")
                            .add("RESULT", profile!=null)
                            .build().toString();

                    mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    break;

                }
                
                case "RECOVER_BY_PHONE_NUMBER": {
                    String phoneNumber  = json.getString("PHONE_NUMBER");
                    String deviceID  = json.getString("DEVICE_ID");
                    String password = json.getString("PASSWORD");
                    
                    Register register = profileRegEJB.recoverByPhoneNumber(phoneNumber, deviceID, password);
                    String data = Json.createObjectBuilder()
                            .add("TYPE", "RECOVER_BY_PHONE_NUMBER")
                            .add("RESULT", register!=null)
                            .add("USER_ID", register!=null?register.getUserID():"")
                            .add("LOGIN_ID", register!=null?register.getLoginID():"")                           
                            .build().toString();

                    mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    break;

                }
                
                case "RECOVER_BY_USER_ID": {
                    String userID  = json.getString("USER_ID");
                    String deviceID  = json.getString("DEVICE_ID");
                    String password = json.getString("PASSWORD");
                    
                    Register register = profileRegEJB.recoverByUserID(userID, deviceID, password);
                    String data = Json.createObjectBuilder()
                            .add("TYPE", "RECOVER_BY_USER_ID")
                            .add("RESULT", register!=null)
                            .add("USER_ID", userID)
                            .add("LOGIN_ID", register!=null?register.getLoginID():"")                           
                            .build().toString();

                    mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    break;

                }
                
                

                default:
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(ProfileRegistrationEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig con) {
        System.out.println("Client Connected");
        mClient = session;
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
    }

    @OnError
    public void onError(Session session, Throwable error) {

    }

}
