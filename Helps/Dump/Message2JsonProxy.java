/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.websocket.data;

import chatserver.constants.GeneralPaths;
import chatserver.database.Profile;
import chatserver.extras.MultiTool;
import chatserver.registration.ProfileRegistration;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Sely
 */
public class Message2JsonProxy implements Encoder.Text<Message> {

    @Override
    public String encode(Message msg) throws EncodeException {
        switch (msg.getMessageType()) {
            case Message.CHECK_HARDWARE_ID:
                ProfileRegistrationMessage prm = (ProfileRegistrationMessage) msg;
                return Json.createObjectBuilder()
                        .add("type", "CHECKHARDWAREID")
                        .add("found", prm.isHarwareIDUsed())
                        .add("exception", "")
                        .build().toString();

            case Message.CHECK_MOBILE_NUMBER:
                prm = (ProfileRegistrationMessage) msg;
                return Json.createObjectBuilder()
                        .add("Type", "CHECKMOBILENUMBER")
                        .add("Found", prm.isMobileNumberUsed())
                        .add("Exception", "")
                        .build().toString();

            case Message.PROFILE_DETAILS:
                prm = (ProfileRegistrationMessage) msg;
                return Json.createObjectBuilder()
                        .add("Type", "PROFILEDETAILS")
                        .add("ProfileID", prm.getProfileID())
                        .add("Exception", "")
                        .build().toString();

            case Message.PROFILE_IMAGE:

                JsonObjectBuilder jsonObj = Json.createObjectBuilder();
                jsonObj.add("Type", "PROFILEIMAGE");

                prm = (ProfileRegistrationMessage) msg;
                jsonObj.add("Completed", false);
                if (prm.IsCompleted()) {
                    jsonObj.add("Completed", true);
                }
                jsonObj.add("SerialNo", prm.getSerialNo());
                return jsonObj.build().toString();

            case Message.CONTACT_UPLOAD:

                prm = (ProfileRegistrationMessage) msg;

                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
                jsonBuilder.add("Type", "CONTACTUPLOAD");
                jsonBuilder.add("SerialNo", prm.getSerialNo());
                jsonBuilder.add("UploadingFinished", false);

                if (prm.isUploadingContactFinished()) {
                    jsonBuilder.add("UploadingFinished", true);
                    prm.setCompleted(false);
                }

                return jsonBuilder.build().toString();

            case Message.SUGGESTLIST_DOWNLOAD:

                prm = (ProfileRegistrationMessage) msg;

                jsonBuilder = Json.createObjectBuilder();

                jsonBuilder.add("Type", "SUGGESTLISTDOWNLOAD");
                jsonBuilder.add("Completed", prm.IsCompleted());
                JsonArrayBuilder jsonArray = Json.createArrayBuilder();
                List<Profile> suggestList = prm.getSuggestListChunk();
                suggestList.stream().map((profile) -> {
                    JsonObjectBuilder jsonObject = Json.createObjectBuilder();
                    jsonObject.add("ProfileID", profile.getProfileID());
                    jsonObject.add("ProfileName", profile.getProfileName());
                    jsonObject.add("Quote", profile.getQuote());
                    
                    File dir = new File(GeneralPaths.PROFILE_IMAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, profile.getProfileID() + "jpg");
                    BufferedImage read = null;
                    try {
                        read = ImageIO.read(file);
                        jsonObject.add("ProfileImage", Base64.encode(MultiTool.getImageAsByteArray(read)));
                    } catch (IOException ex) {
                    }
                    
                    return jsonObject;
                }).forEach((jsonObject) -> {
                    jsonArray.add(jsonObject);
                });
                jsonBuilder.add("SuggestList", jsonArray);

                return jsonBuilder.build().toString();

            default:
                return "";
        }

    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }

}
