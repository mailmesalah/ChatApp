/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import chatserver.database.ContactBlocked;
import chatserver.database.Contact;
import chatserver.database.ContactRequest;
import chatserver.database.Profile;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Sely
 */
@Local
public interface ContactManager {

    List<Profile> readAllContacts(String userID);
    List<String> readAllContactUserIDs(String userID);
    
    List<Contact> readContactList(String userID);
    List<ContactRequest> readContactReceivedList(String userID);
    List<ContactRequest> readContactSentList(String userID);
    List<ContactBlocked> readBlockList(String userID);
    
    List<Contact> readContactList(String userID, int start, int length);
    List<ContactRequest> readContactReceivedList(String userID, int start, int length);
    List<ContactRequest> readContactSentList(String userID, int start, int length);
    List<ContactBlocked> readBlockList(String userID, int start, int length);
    
    List<Contact> readUnreadContactList(String userID);
    List<ContactRequest> readUnreadContactReceivedList(String userID);
    List<ContactRequest> readUnreadContactSentList(String userID);
    List<ContactBlocked> readUnreadBlockList(String userID);
    
    List<Contact> readUnreadContactList(String userID, int start, int length);
    List<ContactRequest> readUnreadContactReceivedList(String userID, int start, int length);
    List<ContactRequest> readUnreadContactSentList(String userID, int start, int length);
    List<ContactBlocked> readUnreadBlockList(String userID, int start, int length);
    
    boolean addRequest(String userID, String contactID);
    boolean acceptRequest(String userID, String contactID);           
    boolean blockOther(String userID, String contactID);
    boolean blockContact(String userID, String contactID);
    boolean blockContactReceived(String userID, String contactID);   
    boolean blockContactSent(String userID, String contactID);
    boolean removeContact(String userID, String contactID);
    boolean removeRequestSent(String userID, String contactID);
    boolean removeRequestReceived(String userID, String contactID);
    boolean removeBlock(String userID, String contactID); 
    
    boolean changeContactRequestStatus(String userID, String contactID, int status);
    boolean changeContactStatus(String userID, String contactID, int status);
    boolean changeBlockStatus(String userID, String contactID, int status);
    boolean changeContactRequestStatus(String userID, List<String> contactIDs, int status);
    boolean changeContactStatus(String userID, List<String> contactIDs, int status);
    boolean changeBlockStatus(String userID, List<String> contactIDs, int status);
    
    //Search Methods
    List<Contact> searchContactList(String userID, String searchText);
    List<ContactRequest> searchContactReceivedList(String userID, String searchText);
    List<ContactRequest> searchContactSentList(String userID, String searchText); 
    List<ContactBlocked> searchContactBlockedList(String userID, String searchText);
    List<Profile> searchProfile(String userID, String searchText);
    Profile searchProfileByUserID(String userID, String searchUserID);
    
    List<Contact> searchContactList(String userID, String searchText, int start, int length);
    List<ContactRequest> searchContactReceivedList(String userID, String searchText, int start, int length);
    List<ContactRequest> searchContactSentList(String userID, String searchText, int start, int length);
    List<ContactBlocked> searchContactBlockedList(String userID, String searchText, int start, int length);    
    List<Profile> searchProfile(String userID, String searchText, int start, int length);
    Profile searchProfileByUserID(String userID, String searchUserID, int start, int length);
 
}
