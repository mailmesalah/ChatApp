/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.websocket.data;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Sely
 */
public class Json2MessageProxy implements Decoder.Text<Message> {

    @Override
    public Message decode(String arg0) throws DecodeException {
        JsonObject json = Json.createReader(new StringReader(arg0)).readObject();

        switch (json.getString("Type")) {
            case "CHECKHARDWAREID":
                return new ProfileRegistrationMessage().setHardwareID(json.getString("HardwareID")).setMessageType(Message.CHECK_HARDWARE_ID);
            case "CHECKMOBILENUMBER":
                return new ProfileRegistrationMessage().setMobileNumber(json.getString("MobileNumber")).setMessageType(Message.CHECK_MOBILE_NUMBER);
            case "PROFILEDETAILS":
                return new ProfileRegistrationMessage().setProfileName(json.getString("ProfileName")).setPassword(json.getString("Password")).setQuote(json.getString("Quote")).setMessageType(Message.PROFILE_DETAILS);
            case "PROFILEIMAGE":
                String data = json.getString("Data");

                byte[] bData = null;
                 {
                    try {
                        bData = Base64.decode(data);
                    } catch (Base64DecodingException ex) {

                    }
                }
                return new ProfileRegistrationMessage().setImageArraySize(json.getInt("ImageArraySize")).setSerialNo(json.getInt("SerialNo")).setImageArray(bData).setCompleted(json.getBoolean("Completed")).setMessageType(Message.PROFILE_IMAGE);
            case "CONTACTUPLOAD":
                JsonArray contacts = json.getJsonArray("MobileContacts");

                Map<String, String> mobileContacts = new HashMap();
                for (int i = 0; i < contacts.size(); ++i) {
                    mobileContacts.put(contacts.getJsonObject(i).getString("PhoneNumber"), contacts.getJsonObject(i).getString("Name"));
                }

                return new ProfileRegistrationMessage().setMobileContacts(mobileContacts).setSerialNo(json.getInt("SerialNo")).setUploadingContactFinished(json.getBoolean("UploadFinished")).setMessageType(Message.CONTACT_UPLOAD);
                
                case "SUGGESTLISTDOWNLOAD":
                    return new ProfileRegistrationMessage().setCompleted(false).setMessageType(Message.SUGGESTLIST_DOWNLOAD);
                    
            default:
        }

        return null;
    }

    @Override
    public boolean willDecode(String arg0) {
        try {
            Json.createReader(new StringReader(arg0)).readObject();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }

}
