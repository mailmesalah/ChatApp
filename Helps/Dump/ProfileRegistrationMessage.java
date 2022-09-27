/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.websocket.data;

import chatserver.database.Profile;
import chatserver.registration.ProfileRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

/**
 *
 * @author Sely
 */
public class ProfileRegistrationMessage implements Message {

    @EJB
    private ProfileRegistration mRegister;
    int mMessageType = Message.CHECK_HARDWARE_ID;
    String mHardwareID = "";
    String mProfileID = "";
    String mMobileNumber = "";
    String mProfileName = "";
    String mQuote = "";    
    String mPassword = "";
    byte[] mImageArray;
    int mImageArraySize=0;
    Boolean mIsCompleted=false;
    Boolean mUploadingContactFinished=false;
    Map<String, String> mMobileContacts = new HashMap();
    List<Profile> mSuggestListChunk = new ArrayList<>();
    int mSerialNo = 0;
    private Profile mProfile;

    public ProfileRegistrationMessage() {
    }

    public ProfileRegistrationMessage(String hardwareID) {
        this.mHardwareID = hardwareID;
    }

    public String getProfileName() {
        return mProfileName;
    }

    public ProfileRegistrationMessage setProfileName(String mProfileName) {
        this.mProfileName = mProfileName;
        return this;
    }

    public String getQuote() {
        return mQuote;
    }

    public ProfileRegistrationMessage setQuote(String mQuote) {
        this.mQuote = mQuote;
        return this;
    }

    public String getPassword() {
        return mPassword;
    }
    
    public ProfileRegistrationMessage setPassword(String mPassword) {
        this.mPassword = mPassword;
        return this;
    }

    public int getImageArraySize() {
        return mImageArraySize;
    }

    public int getCurrentChuckSize(){
        return mImageArray.length;
    }
    
    public ProfileRegistrationMessage setImageArraySize(int mImageArraySize) {
        this.mImageArraySize = mImageArraySize;
        return this;
    }

    public Boolean isUploadingContactFinished() {
        return mUploadingContactFinished;
    }

    public ProfileRegistrationMessage setUploadingContactFinished(Boolean mUploadingContactFinished) {
        this.mUploadingContactFinished = mUploadingContactFinished;
        return this;
    }

    public Boolean IsCompleted() {
        return mIsCompleted;
    }

    public ProfileRegistrationMessage setCompleted(Boolean mIsCompleted) {
        this.mIsCompleted = mIsCompleted;
        return this;
    }

    public byte[] getImageArray() {
        return mImageArray;
    }

    public ProfileRegistrationMessage setImageArray(byte[] mImageArray) {
        this.mImageArray = mImageArray;
        return this;
    }

    public Profile setProfileImage(Profile profile, String profileImage) {                
        return mRegister.updateProfileImage(profile, profileImage);                
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public ProfileRegistrationMessage setMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
        return this;
    }

    public String getHardwareID() {
        return mHardwareID;
    }

    public List<Profile> getSuggestListChunk() {
        return mSuggestListChunk;
    }

    public ProfileRegistrationMessage setSuggestListChunk(List<Profile> suggestListChunk) {
        this.mSuggestListChunk = suggestListChunk;
        return this;
    }

    public ProfileRegistrationMessage setHardwareID(String hardwareID) {
        this.mHardwareID = hardwareID;
        return this;
    }

    public ProfileRegistrationMessage setMessageType(int messageType) {
        this.mMessageType = messageType;
        return this;
    }

    public ProfileRegistrationMessage setProfileID(String profileID) {
        this.mProfileID = profileID;
        return this;
    }

    public Map<String, String> getMobileContacts() {
        return mMobileContacts;
    }

    public ProfileRegistrationMessage setMobileContacts(Map<String, String> mMobileContacts) {
        this.mMobileContacts = mMobileContacts;
        return this;
    }

    public int getSerialNo() {
        return mSerialNo;
    }

    public ProfileRegistrationMessage setSerialNo(int serialNo) {
        this.mSerialNo = serialNo;
        return this;
    }

    @Override
    public int getMessageType() {
        return this.mMessageType;
    }

    @Override
    public String getProfileID() {
        return this.mProfileID;
    }

    //EJB Methods
    public Boolean isMobileNumberUsed() {
        return mRegister.isMobileNumberUsed(mMobileNumber);
    }

    public Boolean isHarwareIDUsed() {
        return mRegister.isHardwareIDUsed(mHardwareID);
    }

    public Profile createProfile() {
        this.mProfile = mRegister.createProfile(mProfileName, mMobileNumber, mHardwareID, mQuote, mPassword);
        return mProfile;
    }

    public void saveMobilesnGenerateSuggestList(Profile profile,Map<String,String> mobileContacts) {
        mRegister.createMobileNumbers(profile, mobileContacts);        
    }

    public List<Profile> getSuggestList() {
        return mRegister.readSuggestList(mProfile);
    }

    public void setProfile(Profile mProfile) {
        this.mProfile = mProfile;
    }
}
