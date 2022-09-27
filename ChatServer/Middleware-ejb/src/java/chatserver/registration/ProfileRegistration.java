/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.registration;

import chatserver.database.Profile;
import chatserver.database.Register;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Sely
 */
@Local
public interface ProfileRegistration {

    boolean isUserIDUsed(final String userID);  
    boolean isPhoneNumberUsed(final String phoneNumber);  
    Register createRegister(String userID, String phoneNumber, String deviceID, String password);
    boolean createPhoneContacts(String userID, Map<String,String> contacts);
    Profile createProfile(String userID, String profileName,byte[] profileImage, String quote);
    Register readRegister(String userID);
    Register recoverByPhoneNumber(final String phoneNumber, final String deviceID, final String password);
    Register recoverByUserID(final String userID, final String deviceID, final String password);
    /*Boolean isHardwareIDUsed(final String hardwareID);
    Boolean isMobileNumberUsed(final String mobileNumber);  
    Profile createProfile(String profileName,String mobileNumber, String hardwareID, String quote, String password);
    Profile updateProfileImage(Profile profile, String profileImage);
    Profile readProfile(String mobileNumber, String hardwareID);
    Profile readProfile(String profileID);
    Boolean checkPassword(Profile profile, String password);
    void createMobileNumbers(Profile profile, Map<String,String> contacts);
    List<Profile> readSuggestList(Profile profile);
    Boolean suspendProfile(Profile profile);    
    List<Profile> readContactList(Profile profile);
    List<Profile> readBlockList(Profile profile);    
    
    */
}
