/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.websocket;

import chatserver.database.Contact;
import chatserver.database.ContactBlocked;
import chatserver.database.ContactRequest;
import chatserver.database.DualChat;
import chatserver.database.Profile;
import chatserver.database.ProfileMap;
import chatserver.extras.MultiTool;
import chatserver.underground.ContactManager;
import chatserver.underground.DualChatManager;
import chatserver.underground.MapActivityManager;
import chatserver.underground.ProfileManager;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
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
@ServerEndpoint(value = "/undergroundcommunicator")
public class UndergroundEndpoint {

    private Session mClient;
    private boolean isLoggedIn = false;
    private String mUserID;

    @EJB
    private ContactManager contactManagerEJB;
    @EJB
    private ProfileManager profileManagerEJB;
    @EJB
    private DualChatManager dualChatManagerEJB;
    @EJB
    private MapActivityManager mapActivityManagerEJB;

    private final ArrayList<Session> mRegisteredSessions = new ArrayList<>();

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

                case "AUTHENTICATE": {
                    String userID = json.getString("USER_ID");
                    String loginID = json.getString("LOGIN_ID");
                    String deviceID = json.getString("DEVICE_ID");

                    if (userID == null || loginID == null || deviceID == null) {
                        break;
                    }

                    isLoggedIn = profileManagerEJB.loginToRegister(userID, loginID, deviceID);
                    if (isLoggedIn) {
                        mUserID = userID;
                        session.getUserProperties().put("USER_ID", userID);
                        session.getUserProperties().put("CONTACT_SESSIONS", mRegisteredSessions);
                        //Register Session
                        registerSession(session);
                    }
                    String data = Json.createObjectBuilder()
                            .add("TYPE", "AUTHENTICATE_RESPONSE")
                            .add("RESULT", isLoggedIn)
                            .build().toString();

                    mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    break;

                }

                case "GET_CONTACT_LIST": {
                    if (isLoggedIn) {

                        List<Contact> cl = contactManagerEJB.readContactList(mUserID);
                        JsonArrayBuilder contactArrayB = Json.createArrayBuilder();

                        for (Contact c : cl) {
                            Profile p;
                            if (c.getUser().getRegister().getUserID().equals(mUserID)) {
                                p = c.getContact();
                            } else {
                                p = c.getUser();
                            }

                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p.getProfileName())
                                    .add("QUOTE", p.getQuote())
                                    .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                    .build();

                            contactArrayB.add(jo);
                        }

                        JsonArray contactArray = contactArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_CONTACT_LIST")
                                .add("CONTACTS", contactArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "GET_REQUEST_RECEIVED_LIST": {
                    if (isLoggedIn) {

                        List<ContactRequest> cl = contactManagerEJB.readContactReceivedList(mUserID);
                        JsonArrayBuilder contactArrayB = Json.createArrayBuilder();
                        for (ContactRequest c : cl) {
                            Profile p = c.getSender();
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p.getProfileName())
                                    .add("QUOTE", p.getQuote())
                                    .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                    .build();

                            contactArrayB.add(jo);
                        }

                        JsonArray contactArray = contactArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_REQUEST_RECEIVED_LIST")
                                .add("CONTACTS", contactArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "GET_REQUEST_SENT_LIST": {
                    if (isLoggedIn) {

                        List<ContactRequest> cl = contactManagerEJB.readContactSentList(mUserID);
                        JsonArrayBuilder contactArrayB = Json.createArrayBuilder();
                        for (ContactRequest c : cl) {
                            Profile p = c.getReceiver();
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p.getProfileName())
                                    .add("QUOTE", p.getQuote())
                                    .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                    .build();

                            contactArrayB.add(jo);
                        }
                        JsonArray contactArray = contactArrayB.build();
                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_REQUEST_SENT_LIST")
                                .add("CONTACTS", contactArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "GET_BLOCK_LIST": {
                    if (isLoggedIn) {

                        List<ContactBlocked> cl = contactManagerEJB.readBlockList(mUserID);
                        JsonArrayBuilder contactArrayB = Json.createArrayBuilder();
                        for (ContactBlocked c : cl) {
                            Profile p = c.getContact();
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p.getProfileName())
                                    .build();

                            contactArrayB.add(jo);
                        }
                        JsonArray contactArray = contactArrayB.build();
                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_BLOCK_LIST")
                                .add("CONTACTS", contactArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "GET_CONTACT_LIST_MORE": {
                    if (isLoggedIn) {

                        int start = json.getInt("START");
                        int length = json.getInt("LENGTH");

                        List<Contact> cl = contactManagerEJB.readContactList(mUserID, start, length);
                        JsonArrayBuilder contactArrayB = Json.createArrayBuilder();
                        for (Contact c : cl) {
                            Profile p;
                            if (c.getUser().getRegister().getUserID().equals(mUserID)) {
                                p = c.getContact();
                            } else {
                                p = c.getUser();
                            }
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p.getProfileName())
                                    .add("QUOTE", p.getQuote())
                                    .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                    .build();

                            contactArrayB.add(jo);
                        }
                        JsonArray contactArray = contactArrayB.build();
                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_CONTACT_LIST_MORE")
                                .add("CONTACTS", contactArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "GET_REQUEST_RECEIVED_LIST_MORE": {
                    if (isLoggedIn) {

                        int start = json.getInt("START");
                        int length = json.getInt("LENGTH");

                        List<ContactRequest> cl = contactManagerEJB.readContactReceivedList(mUserID, start, length);
                        JsonArrayBuilder contactArrayB = Json.createArrayBuilder();
                        for (ContactRequest c : cl) {
                            Profile p = c.getSender();
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p.getProfileName())
                                    .add("QUOTE", p.getQuote())
                                    .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                    .build();

                            contactArrayB.add(jo);
                        }

                        JsonArray contactArray = contactArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_REQUEST_RECEIVED_LIST_MORE")
                                .add("CONTACTS", contactArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "GET_REQUEST_SENT_LIST_MORE": {
                    if (isLoggedIn) {

                        int start = json.getInt("START");
                        int length = json.getInt("LENGTH");

                        List<ContactRequest> cl = contactManagerEJB.readContactSentList(mUserID, start, length);
                        JsonArrayBuilder contactArrayB = Json.createArrayBuilder();
                        for (ContactRequest c : cl) {
                            Profile p = c.getReceiver();
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p.getProfileName())
                                    .add("QUOTE", p.getQuote())
                                    .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                    .build();

                            contactArrayB.add(jo);
                        }

                        JsonArray contactArray = contactArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_REQUEST_SENT_LIST_MORE")
                                .add("CONTACTS", contactArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "GET_BLOCK_LIST_MORE": {
                    if (isLoggedIn) {

                        int start = json.getInt("START");
                        int length = json.getInt("LENGTH");

                        List<ContactBlocked> cl = contactManagerEJB.readBlockList(mUserID, start, length);
                        JsonArrayBuilder contactArrayB = Json.createArrayBuilder();
                        for (ContactBlocked c : cl) {
                            Profile p = c.getContact();
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p.getProfileName())
                                    .build();

                            contactArrayB.add(jo);
                        }

                        JsonArray contactArray = contactArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_BLOCK_LIST_MORE")
                                .add("CONTACTS", contactArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "ADD_REQUEST": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.addRequest(mUserID, contactID);
                        Profile p = profileManagerEJB.readProfileByUserID(contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "ADD_REQUEST")
                                .add("CONTACT_ID", contactID)
                                .add("PROFILE_NAME", p == null ? "" : p.getProfileName())
                                .add("QUOTE", p == null ? "" : p.getQuote())
                                .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "ADD_REQUEST_RESPONSE": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeContactRequestStatus(mUserID, contactID, ContactRequest.RECEIVED_BY_SERVER);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "ADD_REQUEST_RESPONSE")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                        //Send Request to Contact if online
                        if (!result) {
                            break;
                        }

                        //Add to the session
                        addSession(contactID, session);

                        Profile p = profileManagerEJB.readProfileByUserID(mUserID);
                        String contactData = Json.createObjectBuilder()
                                .add("TYPE", "ADD_REQUEST_RECEIVED")
                                .add("CONTACT_ID", mUserID)
                                .add("PROFILE_NAME", p == null ? "" : p.getProfileName())
                                .add("QUOTE", p == null ? "" : p.getQuote())
                                .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                .build().toString();

                        sendMessageToContact(contactID, contactData);
                    }
                    break;

                }

                case "ACCEPT_REQUEST": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.acceptRequest(mUserID, contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "ACCEPT_REQUEST")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "ACCEPT_REQUEST_RESPONSE": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeContactStatus(mUserID, contactID, Contact.RECEIVED_BY_SERVER);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "ACCEPT_REQUEST_RESPONSE")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                        //INFORM CONTACT REQUEST ACCEPTED
                        if (!result) {
                            break;
                        }

                        String contactData = Json.createObjectBuilder()
                                .add("TYPE", "ACCEPT_REQUEST_RECEIVED")
                                .add("CONTACT_ID", mUserID)
                                .add("RESULT", result)
                                .build().toString();

                        sendMessageToContact(contactID, contactData);

                    }

                    break;

                }

                case "BLOCK_OTHER": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.blockOther(mUserID, contactID);
                        Profile p = profileManagerEJB.readProfileByUserID(contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_OTHER")
                                .add("CONTACT_ID", contactID)
                                .add("PROFILE_NAME", p == null ? "" : p.getProfileName())
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "BLOCK_OTHER_RESPONSE": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeBlockStatus(mUserID, contactID, ContactBlocked.RECEIVED_BY_SERVER);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_OTHER_RESPONSE")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "BLOCK_CONTACT": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.blockContact(mUserID, contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_CONTACT")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "BLOCK_CONTACT_RESPONSE": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeBlockStatus(mUserID, contactID, ContactBlocked.RECEIVED_BY_SERVER);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_CONTACT_RESPONSE")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                        //INFORM CONTACT ADD BLOCK TO REMOVE FROM CONTACT
                        if (!result) {
                            break;
                        }

                        String contactData = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_CONTACT_RECEIVED")
                                .add("CONTACT_ID", mUserID)
                                .add("RESULT", result)
                                .build().toString();

                        sendMessageToContact(contactID, contactData);

                        //Remove Session
                        removeSession(contactID, session);
                    }
                    break;

                }

                case "BLOCK_REQUEST_RECEIVED": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.blockContactReceived(mUserID, contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_REQUEST_RECEIVED")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "BLOCK_REQUEST_RECEIVED_RESPONSE": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeBlockStatus(mUserID, contactID, ContactBlocked.RECEIVED_BY_SERVER);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_REQUEST_RECEIVED_RESPONSE")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                        //INFORM CONTACT ADD BLOCK TO REMOVE FROM CONTACT
                        if (!result) {
                            break;
                        }

                        String contactData = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_REQUEST_RECEIVED_RECEIVED")
                                .add("CONTACT_ID", mUserID)
                                .add("RESULT", result)
                                .build().toString();

                        sendMessageToContact(contactID, contactData);

                        //Remove Session
                        removeSession(contactID, session);
                    }

                    break;

                }

                case "BLOCK_REQUEST_SENT": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.blockContactSent(mUserID, contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_REQUEST_SENT")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "BLOCK_REQUEST_SENT_RESPONSE": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeBlockStatus(mUserID, contactID, ContactBlocked.RECEIVED_BY_SERVER);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_REQUEST_SENT_RESPONSE")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                        //INFORM CONTACT ADD BLOCK TO REMOVE FROM CONTACT
                        if (!result) {
                            break;
                        }

                        String contactData = Json.createObjectBuilder()
                                .add("TYPE", "BLOCK_REQUEST_SENT_RECEIVED")
                                .add("CONTACT_ID", mUserID)
                                .add("RESULT", result)
                                .build().toString();

                        sendMessageToContact(contactID, contactData);

                        //Remove Session
                        removeSession(contactID, session);
                    }
                    break;

                }

                case "REMOVE_CONTACT": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.removeContact(mUserID, contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "REMOVE_CONTACT")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                        //INFORM CONTACT REMOVED
                        if (!result) {
                            break;
                        }

                        String contactData = Json.createObjectBuilder()
                                .add("TYPE", "REMOVE_CONTACT_RECEIVED")
                                .add("CONTACT_ID", mUserID)
                                .add("RESULT", result)
                                .build().toString();

                        sendMessageToContact(contactID, contactData);

                        //Remove Session
                        removeSession(contactID, session);
                    }
                    break;

                }

                case "REMOVE_REQUEST_SENT": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.removeRequestSent(mUserID, contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "REMOVE_REQUEST_SENT")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                        //INFORM CONTACT REQUEST CANCELED
                        if (!result) {
                            break;
                        }

                        String contactData = Json.createObjectBuilder()
                                .add("TYPE", "REMOVE_REQUEST_SENT_RECEIVED")
                                .add("CONTACT_ID", mUserID)
                                .add("RESULT", result)
                                .build().toString();

                        sendMessageToContact(contactID, contactData);

                        //Remove Session
                        removeSession(contactID, session);
                    }
                    break;

                }

                case "REMOVE_REQUEST_RECEIVED": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.removeRequestReceived(mUserID, contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "REMOVE_REQUEST_RECEIVED")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                        //INFORM CONTACT REQUEST CANCELED
                        if (!result) {
                            break;
                        }

                        String contactData = Json.createObjectBuilder()
                                .add("TYPE", "REMOVE_REQUEST_RECEIVED_RECEIVED")
                                .add("CONTACT_ID", mUserID)
                                .add("RESULT", result)
                                .build().toString();

                        sendMessageToContact(contactID, contactData);

                        //Remove Session
                        removeSession(contactID, session);
                    }
                    break;

                }

                case "REMOVE_BLOCK": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.removeBlock(mUserID, contactID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "REMOVE_BLOCK")
                                .add("CONTACT_ID", contactID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "SEARCH_PROFILE_BY_SEARCH_TEXT": {
                    if (isLoggedIn) {

                        String searchText = json.getString("SEARCH_TEXT");
                        int start = json.getInt("START");
                        int length = json.getInt("LENGTH");

                        List<Profile> profiles = contactManagerEJB.searchProfile(mUserID, searchText, start, length);

                        JsonArrayBuilder profileArrayB = Json.createArrayBuilder();
                        for (Profile p : profiles) {
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", p == null ? "" : p.getRegister().getUserID())
                                    .add("PROFILE_NAME", p == null ? "" : p.getProfileName())
                                    .add("QUOTE", p == null ? "" : p.getQuote())
                                    .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                    .build();

                            profileArrayB.add(jo);
                        }
                        JsonArray profileArray = profileArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "SEARCH_PROFILE_BY_SEARCH_TEXT")
                                .add("PROFILES", profileArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;
                }

                case "SEARCH_PROFILE_BY_USER_ID": {
                    if (isLoggedIn) {

                        String searchUserID = json.getString("SEARCH_USER_ID");

                        if (searchUserID == null) {
                            break;
                        }

                        Profile p = contactManagerEJB.searchProfileByUserID(mUserID, searchUserID);

                        JsonObject jo = Json.createObjectBuilder()
                                .add("USER_ID", p == null ? "" : p.getRegister().getUserID())
                                .add("PROFILE_NAME", p == null ? "" : p.getProfileName())
                                .add("QUOTE", p == null ? "" : p.getQuote())
                                .add("PROFILE_IMAGE", p == null || p.getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(p.getProfileImage()))
                                .build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "SEARCH_PROFILE_BY_USER_ID")
                                .add("PROFILE", jo)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }
                    break;

                }

                case "ADD_REQUEST_RECEIVED": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeContactRequestStatus(mUserID, contactID, ContactRequest.RECEIVED_BY_CLIENT);
                    }

                    break;

                }

                case "ACCEPT_REQUEST_RECEIVED": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeContactStatus(mUserID, contactID, ContactRequest.RECEIVED_BY_CLIENT);
                    }

                    break;

                }

                case "BLOCK_CONTACT_RECEIVED": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeBlockStatus(mUserID, contactID, ContactRequest.RECEIVED_BY_CLIENT);
                    }

                    break;

                }

                case "BLOCK_REQUEST_RECEIVED_RECEIVED": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeBlockStatus(mUserID, contactID, ContactRequest.RECEIVED_BY_CLIENT);
                    }

                    break;

                }

                case "BLOCK_REQUEST_SENT_RECEIVED": {
                    if (isLoggedIn) {

                        String contactID = json.getString("CONTACT_ID");

                        if (contactID == null) {
                            break;
                        }

                        boolean result = contactManagerEJB.changeBlockStatus(mUserID, contactID, ContactRequest.RECEIVED_BY_CLIENT);
                    }

                    break;

                }

                //Dual Chat Conditions (Commands)
                case "GET_ALL_CHATS_MORE": {
                    if (isLoggedIn) {
                        int start = json.getInt("START");
                        int length = json.getInt("LENGTH");
                        List<DualChat> chats = dualChatManagerEJB.readChats(mUserID, start, length);

                        if (chats == null) {
                            break;
                        }

                        //Send Chats
                        JsonArrayBuilder chatArrayB = Json.createArrayBuilder();
                        for (DualChat dc : chats) {
                            String messageType = "";
                            switch (dc.getMessageType()) {
                                case DualChat.TEXT:
                                    messageType = "TEXT";
                                    break;
                                case DualChat.IMAGE_TEXT:
                                    messageType = "IMAGE_TEXT";
                                    break;
                                case DualChat.IMAGES_TEXT:
                                    messageType = "IMAGES_TEXT";
                                    break;
                                case DualChat.CAMERA_IMAGE_TEXT:
                                    messageType = "CAMERA_IMAGE_TEXT";
                                    break;
                                case DualChat.AUDIO_TEXT:
                                    messageType = "AUDIO_TEXT";
                                    break;
                                case DualChat.AUDIOS_TEXT:
                                    messageType = "AUDIOS_TEXT";
                                    break;
                                case DualChat.VOICE_TEXT:
                                    messageType = "VOICE_TEXT";
                                    break;
                                case DualChat.VIDEO_TEXT:
                                    messageType = "VIDEO_TEXT";
                                    break;
                                case DualChat.VIDIEOS_TEXT:
                                    messageType = "VIDIEOS_TEXT";
                                    break;
                                case DualChat.CAMERA_VIDEO_TEXT:
                                    messageType = "CAMERA_VIDEO_TEXT";
                                    break;
                                case DualChat.VOICE_CALL_SUMMERY_TEXT:
                                    messageType = "VOICE_CALL_SUMMERY_TEXT";
                                    break;
                                case DualChat.VIDEO_CALL_SUMMARY_TEXT:
                                    messageType = "VIDEO_CALL_SUMMARY_TEXT";
                                    break;
                                case DualChat.FILE_TEXT:
                                    messageType = "FILE_TEXT";
                                    break;

                                default:
                                    messageType = "TEXT";
                            }

                            String seenBy = "";
                            switch (dc.getSeenBy()) {
                                case DualChat.SENDER:
                                    seenBy = "SENDER";
                                    break;
                                case DualChat.SERVER:
                                    seenBy = "SERVER";
                                    break;
                                case DualChat.RECEIVER:
                                    seenBy = "RECEIVER";
                                    break;

                                default:
                                    seenBy = "SENDER";
                            }

                            JsonObject jo = Json.createObjectBuilder()
                                    .add("SERVER_CHAT_ID", dc.getId())
                                    .add("SENDER_USER_ID", dc.getSender().getRegister().getUserID())
                                    .add("RECEIVER_USER_ID", dc.getReceiver().getRegister().getUserID())
                                    .add("MESSAGE_TEXT", dc.getMessageText())
                                    .add("MESSAGE_TYPE", messageType)
                                    .add("SEEN_BY", seenBy)
                                    .add("SEND_TIME", dc.getSendTime().getTimeInMillis())
                                    .add("RECEIVE_TIME", dc.getReceiveTime().getTimeInMillis())
                                    .build();

                            chatArrayB.add(jo);
                        }

                        JsonArray chatArray = chatArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_ALL_CHATS_MORE")
                                .add("CHATS", chatArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }

                    break;
                }

                case "GET_ALL_UNREAD_CHATS": {
                    if (isLoggedIn) {

                        List<DualChat> chats = dualChatManagerEJB.readUnreadChats(mUserID);

                        if (chats == null) {
                            break;
                        }

                        //Send Chats
                        JsonArrayBuilder chatArrayB = Json.createArrayBuilder();
                        for (DualChat dc : chats) {

                            String messageType = "";
                            switch (dc.getMessageType()) {
                                case DualChat.TEXT:
                                    messageType = "TEXT";
                                    break;
                                case DualChat.IMAGE_TEXT:
                                    messageType = "IMAGE_TEXT";
                                    break;
                                case DualChat.IMAGES_TEXT:
                                    messageType = "IMAGES_TEXT";
                                    break;
                                case DualChat.CAMERA_IMAGE_TEXT:
                                    messageType = "CAMERA_IMAGE_TEXT";
                                    break;
                                case DualChat.AUDIO_TEXT:
                                    messageType = "AUDIO_TEXT";
                                    break;
                                case DualChat.AUDIOS_TEXT:
                                    messageType = "AUDIOS_TEXT";
                                    break;
                                case DualChat.VOICE_TEXT:
                                    messageType = "VOICE_TEXT";
                                    break;
                                case DualChat.VIDEO_TEXT:
                                    messageType = "VIDEO_TEXT";
                                    break;
                                case DualChat.VIDIEOS_TEXT:
                                    messageType = "VIDIEOS_TEXT";
                                    break;
                                case DualChat.CAMERA_VIDEO_TEXT:
                                    messageType = "CAMERA_VIDEO_TEXT";
                                    break;
                                case DualChat.VOICE_CALL_SUMMERY_TEXT:
                                    messageType = "VOICE_CALL_SUMMERY_TEXT";
                                    break;
                                case DualChat.VIDEO_CALL_SUMMARY_TEXT:
                                    messageType = "VIDEO_CALL_SUMMARY_TEXT";
                                    break;
                                case DualChat.FILE_TEXT:
                                    messageType = "FILE_TEXT";
                                    break;

                                default:
                                    messageType = "TEXT";
                            }

                            /*String seenBy = "";
                            switch (dc.getSeenBy()) {
                                case DualChat.SENDER:
                                    seenBy = "SENDER";
                                    break;
                                case DualChat.SERVER:
                                    seenBy = "SERVER";
                                    break;
                                case DualChat.RECEIVER:
                                    seenBy = "RECEIVER";
                                    break;

                                default:
                                    seenBy = "SENDER";
                            }*/
                            JsonObject jo = Json.createObjectBuilder()
                                    .add("SERVER_CHAT_ID", dc.getId().toString())
                                    .add("SENDER_USER_ID", dc.getSender().getRegister().getUserID())
                                    .add("RECEIVER_USER_ID", dc.getReceiver().getRegister().getUserID())
                                    .add("MESSAGE_TEXT", dc.getMessageText())
                                    .add("MESSAGE_TYPE", messageType)
                                    .add("SEND_TIME", dc.getSendTime().getTimeInMillis())
                                    .build();

                            chatArrayB.add(jo);
                        }

                        JsonArray chatArray = chatArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "GET_ALL_UNREAD_CHATS")
                                .add("CHATS", chatArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }

                    break;
                }

                case "CHAT_MESSAGE_BY_SENDER": {
                    if (isLoggedIn) {
                        Profile sender = profileManagerEJB.readProfileByUserID(mUserID);
                        if (sender == null) {
                            break;
                        }
                        String receiverUserID = json.getString("RECEIVER_USER_ID");
                        Profile receiver = profileManagerEJB.readProfileByUserID(receiverUserID);
                        if (receiver == null) {
                            break;
                        }

                        String messageText = json.getString("MESSAGE_TEXT");
                        String messageType = json.getString("MESSAGE_TYPE");
                        long clientChatID = json.getInt("CLIENT_CHAT_ID");
                        Calendar sendTime = Calendar.getInstance();
                        int iMessageType;
                        switch (messageType) {
                            case "TEXT":
                                iMessageType = DualChat.TEXT;
                                break;
                            case "IMAGE_TEXT":
                                iMessageType = DualChat.IMAGE_TEXT;
                                break;
                            case "IMAGES_TEXT":
                                iMessageType = DualChat.IMAGES_TEXT;
                                break;
                            case "CAMERA_IMAGE_TEXT":
                                iMessageType = DualChat.CAMERA_IMAGE_TEXT;
                                break;
                            case "AUDIO_TEXT":
                                iMessageType = DualChat.AUDIO_TEXT;
                                break;
                            case "AUDIOS_TEXT":
                                iMessageType = DualChat.AUDIOS_TEXT;
                                break;
                            case "VOICE_TEXT":
                                iMessageType = DualChat.VOICE_TEXT;
                                break;
                            case "VIDEO_TEXT":
                                iMessageType = DualChat.VIDEO_TEXT;
                                break;
                            case "VIDIEOS_TEXT":
                                iMessageType = DualChat.VIDIEOS_TEXT;
                                break;
                            case "CAMERA_VIDEO_TEXT":
                                iMessageType = DualChat.CAMERA_VIDEO_TEXT;
                                break;
                            case "VOICE_CALL_SUMMERY_TEXT":
                                iMessageType = DualChat.VOICE_CALL_SUMMERY_TEXT;
                                break;
                            case "VIDEO_CALL_SUMMARY_TEXT":
                                iMessageType = DualChat.VIDEO_CALL_SUMMARY_TEXT;
                                break;
                            case "FILE_TEXT":
                                iMessageType = DualChat.FILE_TEXT;
                                break;

                            default:
                                iMessageType = DualChat.TEXT;
                        }

                        DualChat dc = new DualChat(sender, receiver, messageText, "", iMessageType, sendTime, DualChat.SERVER);
                        DualChat resultDC = dualChatManagerEJB.addChat(dc);

                        if (resultDC != null) {
                            String data = Json.createObjectBuilder()
                                    .add("TYPE", "CHAT_MESSAGE_SEEN_BY_SERVER")
                                    .add("SERVER_CHAT_ID", resultDC.getId())
                                    .add("CLIENT_CHAT_ID", clientChatID)
                                    .build().toString();

                            mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));

                            //Send message to receiver if online
                            /*String messageType="";
                            switch(dc.getMessageType()){
                                case DualChat.TEXT:messageType="TEXT"; break;  
                                case DualChat.IMAGE_TEXT:messageType="IMAGE_TEXT"; break;
                                case DualChat.IMAGES_TEXT:messageType="IMAGES_TEXT"; break;
                                case DualChat.CAMERA_IMAGE_TEXT:messageType="CAMERA_IMAGE_TEXT"; break;
                                case DualChat.AUDIO_TEXT:messageType="AUDIO_TEXT"; break;
                                case DualChat.AUDIOS_TEXT:messageType="AUDIOS_TEXT"; break;
                                case DualChat.VOICE_TEXT:messageType="VOICE_TEXT"; break;
                                case DualChat.VIDEO_TEXT:messageType="VIDEO_TEXT"; break;
                                case DualChat.VIDIEOS_TEXT:messageType="VIDIEOS_TEXT"; break;
                                case DualChat.CAMERA_VIDEO_TEXT:messageType="CAMERA_VIDEO_TEXT"; break;
                                case DualChat.VOICE_CALL_SUMMERY_TEXT:messageType="VOICE_CALL_SUMMERY_TEXT"; break;
                                case DualChat.VIDEO_CALL_SUMMARY_TEXT:messageType="VIDEO_CALL_SUMMARY_TEXT"; break;
                                case DualChat.FILE_TEXT:messageType="FILE_TEXT"; break;
                                                                        
                                default:
                                    messageType="TEXT";
                            }*/
 /*String seenBy="";
                            switch(dc.getSeenBy()){
                                case DualChat.SENDER:seenBy="SENDER"; break;  
                                case DualChat.SERVER:seenBy="SERVER"; break;
                                case DualChat.RECEIVER:seenBy="RECEIVER"; break;                                
                                                                        
                                default:
                                    seenBy="SENDER";
                            }*/
                            String dataR = Json.createObjectBuilder()
                                    .add("TYPE", "CHAT_MESSAGE_TO_RECEIVER")
                                    .add("SERVER_CHAT_ID", resultDC.getId())
                                    .add("SENDER_USER_ID", dc.getSender().getRegister().getUserID())
                                    .add("MESSAGE_TEXT", dc.getMessageText())
                                    .add("MESSAGE_TYPE", messageType)
                                    .add("SEND_TIME", dc.getSendTime().getTimeInMillis())
                                    .build().toString();

                            sendMessageToContact(receiverUserID, dataR);
                        }
                    }

                    break;
                }

                case "IS_CHAT_MESSAGE_SEEN_BY_RECEIVER": {
                    if (isLoggedIn) {
                        long serverChatID = json.getInt("SERVER_CHAT_ID");

                        boolean result = dualChatManagerEJB.isChatSeenByReceiver(mUserID, serverChatID);
                        if (result) {
                            //Send message to sender if online
                            String data = Json.createObjectBuilder()
                                    .add("TYPE", "CHAT_MESSAGE_SEEN_BY_RECEIVER")
                                    .add("SERVER_CHAT_ID", serverChatID)
                                    .build().toString();

                            mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                        }
                    }

                    break;
                }

                case "CHAT_MESSAGE_SEEN_BY_RECEIVER": {
                    if (isLoggedIn) {
                        long serverChatID = json.getInt("SERVER_CHAT_ID");
                        String senderUserID = json.getString("SENDER_USER_ID");

                        boolean result = dualChatManagerEJB.chatSeenByReceiver(mUserID, serverChatID);
                        if (result) {
                            //Send message to sender if online
                            String dataR = Json.createObjectBuilder()
                                    .add("TYPE", "CHAT_MESSAGE_SEEN_BY_RECEIVER")
                                    .add("SERVER_CHAT_ID", serverChatID)
                                    .build().toString();

                            sendMessageToContact(senderUserID, dataR);
                        }
                    }

                    break;
                }

                case "CHAT_MESSAGE_DELETED_BY_SENDER": {
                    if (isLoggedIn) {
                        long serverChatID = json.getInt("SERVER_CHAT_ID");

                        boolean result = dualChatManagerEJB.deleteChatBySender(mUserID, serverChatID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "CHAT_MESSAGE_DELETED_BY_SENDER")
                                .add("CHAT_ID", serverChatID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }

                    break;
                }

                case "CHAT_MESSAGE_DELETED_BY_RECEIVER": {
                    if (isLoggedIn) {
                        long serverChatID = json.getInt("SERVER_CHAT_ID");

                        boolean result = dualChatManagerEJB.deleteChatByReceiver(mUserID, serverChatID);

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "CHAT_MESSAGE_DELETED_BY_RECEIVER")
                                .add("CHAT_ID", serverChatID)
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }

                    break;
                }

                //
                //Map Activities Commands
                //
                case "READ_PROFILE_MAP": {
                    if (isLoggedIn) {

                        String sLatitude = json.getString("LATITUDE");
                        String sLongitude = json.getString("LONGITUDE");
                        float latitude = Float.parseFloat(sLatitude);
                        float longitude = Float.parseFloat(sLongitude);
                        int distance = json.getInt("DISTANCE");

                        List<ProfileMap> pms = mapActivityManagerEJB.readProfilesAround(latitude, longitude, distance);

                        JsonArrayBuilder profileMapArrayB = Json.createArrayBuilder();

                        for (ProfileMap pm : pms) {

                            JsonObject jo = Json.createObjectBuilder()
                                    .add("USER_ID", pm.getProfile().getRegister().getUserID())
                                    .add("PROFILE_NAME", pm.getProfile().getProfileName())
                                    .add("QUOTE", pm.getProfile().getQuote())
                                    .add("PROFILE_IMAGE", pm.getProfile().getProfileImage() == null ? "" : DatatypeConverter.printBase64Binary(pm.getProfile().getProfileImage()))
                                    .add("LATITUDE", pm.getLatitude() + "")
                                    .add("LONGITUDE", pm.getLongitude() + "")
                                    .build();

                            profileMapArrayB.add(jo);
                        }

                        JsonArray profileMapArray = profileMapArrayB.build();

                        String data = Json.createObjectBuilder()
                                .add("TYPE", "READ_PROFILE_MAP")
                                .add("PROFILE_MAP", profileMapArray)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }

                    break;
                }

                case "BROADCAST_PROFILE_LOCATION": {
                    if (isLoggedIn) {
                        String privacy = json.getString("PRIVACY");

                        String sLatitude = json.getString("LATITUDE");
                        String sLongitude = json.getString("LONGITUDE");
                        float latitude = Float.parseFloat(sLatitude);
                        float longitude = Float.parseFloat(sLongitude);

                        if (privacy.equals("PUBLIC")) {
                            mapActivityManagerEJB.updateProfileInMap(mUserID, latitude, longitude);
                        }
                        
                        //Broadcast location to online contacts
                        String data = Json.createObjectBuilder()
                                .add("TYPE", "PROFILE_LOCATION_RECEIVED")
                                .add("USER_ID", mUserID)
                                .add("LATITUDE", sLatitude)
                                .add("LONGITUDE", sLongitude)
                                .build().toString();
                        
                        broadcastMessageToContacts(data);
                    }

                    break;
                }

                case "REMOVE_PROFILE_FROM_MAP": {
                    if (isLoggedIn) {
                        String sLatitude = json.getString("LATITUDE");
                        String sLongitude = json.getString("LONGITUDE");
                        float latitude = Float.parseFloat(sLatitude);
                        float longitude = Float.parseFloat(sLongitude);
                        
                        boolean result = mapActivityManagerEJB.removeProfileFromMap(mUserID, latitude, longitude);
                        
                        String data = Json.createObjectBuilder()
                                .add("TYPE", "REMOVE_PROFILE_FROM_MAP")
                                .add("RESULT", result)
                                .build().toString();

                        mClient.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
                    }

                    break;
                }

                default:
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(UndergroundEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig con) {
        System.out.println("Client Connected On Underground");
        //session.setMaxIdleTimeout(1000);
        mClient = session;

        //Ask for authentication
        String data = Json.createObjectBuilder()
                .add("TYPE", "AUTHENTICATE")
                .build().toString();

        try {
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(data)));
        } catch (IOException ex) {
            Logger.getLogger(UndergroundEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Closing Websocket Connection " + reason);
        //Unregister Session
        unregisterSession(mClient);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println(error.getMessage());
    }

    //Methods for optimizing Session Management
    private void addSession(String contactID, Session session) {
        for (Session onlineSes : session.getOpenSessions()) {
            if (onlineSes.isOpen() && onlineSes.getUserProperties().containsKey("USER_ID")) {
                if (onlineSes.getUserProperties().get("USER_ID").equals(contactID)) {
                    //Exchange Session Details 
                    ((ArrayList<Session>) onlineSes.getUserProperties().get("CONTACT_SESSIONS")).add(session);
                    mRegisteredSessions.add(onlineSes);
                    break;
                }
            }
        }
    }

    private void removeSession(String contactID, Session session) {
        for (Session onlineSes : session.getOpenSessions()) {
            if (onlineSes.isOpen() && onlineSes.getUserProperties().containsKey("USER_ID")) {
                if (onlineSes.getUserProperties().get("USER_ID").equals(contactID)) {
                    //Exchange Session Details 
                    ((ArrayList<Session>) onlineSes.getUserProperties().get("CONTACT_SESSIONS")).remove(session);
                    mRegisteredSessions.remove(onlineSes);
                    break;
                }
            }
        }
    }

    private void registerSession(Session session) {
        for (String contactID : contactManagerEJB.readAllContactUserIDs(mUserID)) {
            for (Session onlineSes : session.getOpenSessions()) {
                if (onlineSes.isOpen() && onlineSes.getUserProperties().containsKey("USER_ID")) {
                    System.out.println("UserID=" + onlineSes.getUserProperties().get("USER_ID") + " ContactID=" + contactID);
                    if (onlineSes.getUserProperties().get("USER_ID").equals(contactID)) {
                        //Exchange Session Details 
                        ((ArrayList<Session>) onlineSes.getUserProperties().get("CONTACT_SESSIONS")).add(session);
                        mRegisteredSessions.add(onlineSes);
                        break;
                    }
                }
            }
        }

    }

    private void unregisterSession(Session session) {
        try {
            for (Session onlineSes : mRegisteredSessions) {
                if (onlineSes.isOpen() && onlineSes.getUserProperties().containsKey("USER_ID")) {
                    ((ArrayList<Session>) onlineSes.getUserProperties().get("CONTACT_SESSIONS")).remove(session);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
        mRegisteredSessions.clear();

    }

    private void sendMessageToContact(String contactID, String message) {
        for (Session onlineSes : mRegisteredSessions) {
            if (onlineSes.isOpen() && onlineSes.getUserProperties().containsKey("USER_ID")) {
                if (onlineSes.getUserProperties().get("USER_ID").equals(contactID)) {
                    try {
                        onlineSes.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(message)));
                    } catch (IOException ex) {
                        System.out.println(ex.getCause());
                    }
                    break;
                }
            }
        }
    }

    private void broadcastMessageToContacts(String message) {
        for (Session onlineSes : mRegisteredSessions) {
            if (onlineSes.isOpen()) {
                try {
                    onlineSes.getBasicRemote().sendBinary(ByteBuffer.wrap(MultiTool.compress(message)));
                } catch (IOException ex) {
                    System.out.println(ex.getCause());
                }

            }
        }
    }

}
