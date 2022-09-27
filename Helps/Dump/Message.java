/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.websocket.data;

/**
 *
 * @author Sely
 */
public interface Message {

    public static final int CHECK_HARDWARE_ID=1;
    public static final int CHECK_MOBILE_NUMBER=2;
    public static final int PROFILE_DETAILS=3;
    public static final int PROFILE_IMAGE=4;
    public static final int CONTACT_UPLOAD=5;
    public static final int SUGGESTLIST_DOWNLOAD=6;
    public static final int COMPLETED=7;
   
    
    public int getMessageType();
    public String getProfileID();
}
