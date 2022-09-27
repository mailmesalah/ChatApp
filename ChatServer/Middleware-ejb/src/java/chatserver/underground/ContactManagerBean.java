/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import chatserver.database.ContactBlocked;
import chatserver.database.Contact;
import chatserver.database.Profile;
import chatserver.database.ContactRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Sely
 */
@Stateless
public class ContactManagerBean implements ContactManager {

    @PersistenceContext(unitName = "DatabasePU")
    EntityManager em;

    @EJB
    private ProfileManager profileManager;

    @Override
    public List<Profile> readAllContacts(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        List<Profile> contacts = new ArrayList<>();
        
        TypedQuery<Contact> cQuery = em.createNamedQuery("ReadAllContactByProfileOrContact", Contact.class);
        cQuery.setParameter("user", profile);
        cQuery.setParameter("contact", profile);
        cQuery.setParameter("status", Contact.SENT_BY_CLIENT);
        List<Contact> contactList = cQuery.getResultList();
        
        for(Contact contact:contactList){
            if(userID.equals(contact.getUser().getRegister().getUserID())){
                contacts.add(contact.getContact());    
            }else{
                contacts.add(contact.getUser());    
            }            
        }
        
        TypedQuery<ContactRequest> crQuery = em.createNamedQuery("ReadAllContactRequestByReceiver", ContactRequest.class);
        crQuery.setParameter("receiver", profile);
        crQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        List<ContactRequest> requestReceivedList = crQuery.getResultList();
        
        for(ContactRequest contact:requestReceivedList){
                contacts.add(contact.getSender());                            
        }
        
        TypedQuery<ContactRequest> csQuery = em.createNamedQuery("ReadAllContactRequestBySender", ContactRequest.class);
        csQuery.setParameter("sender", profile);
        csQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        List<ContactRequest> requestSentList = csQuery.getResultList();

        for(ContactRequest contact:requestSentList){
                contacts.add(contact.getReceiver());                            
        }
       
        return contacts;
    }
    
    @Override
    public List<String> readAllContactUserIDs(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        List<String> contacts = new ArrayList<>();
        
        TypedQuery<Contact> cQuery = em.createNamedQuery("ReadAllContactByProfileOrContact", Contact.class);
        cQuery.setParameter("user", profile);
        cQuery.setParameter("contact", profile);
        cQuery.setParameter("status", Contact.SENT_BY_CLIENT);
        List<Contact> contactList = cQuery.getResultList();
        
        for(Contact contact:contactList){
            if(userID.equals(contact.getUser().getRegister().getUserID())){
                contacts.add(contact.getContact().getRegister().getUserID());    
            }else{
                contacts.add(contact.getUser().getRegister().getUserID());    
            }            
        }
        
        TypedQuery<ContactRequest> crQuery = em.createNamedQuery("ReadAllContactRequestByReceiver", ContactRequest.class);
        crQuery.setParameter("receiver", profile);
        crQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        List<ContactRequest> requestReceivedList = crQuery.getResultList();
        
        for(ContactRequest contact:requestReceivedList){
                contacts.add(contact.getSender().getRegister().getUserID());                            
        }
        
        TypedQuery<ContactRequest> csQuery = em.createNamedQuery("ReadAllContactRequestBySender", ContactRequest.class);
        csQuery.setParameter("sender", profile);
        csQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        List<ContactRequest> requestSentList = csQuery.getResultList();

        for(ContactRequest contact:requestSentList){
                contacts.add(contact.getReceiver().getRegister().getUserID());                            
        }
      
        return contacts;
    }
    
    @Override
    public List<Contact> readContactList(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadAllContactByProfileOrContact", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contact", profile);
        clQuery.setParameter("status", Contact.RECEIVED_BY_CLIENT);
        List<Contact> readList = clQuery.getResultList();
        
        //Get all unread contact
        List<Contact> unReadList = readUnreadContactList(profile);

        List<Contact> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    @Override
    public List<ContactRequest> readContactReceivedList(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadAllContactRequestByReceiver", ContactRequest.class);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        List<ContactRequest> readList = clQuery.getResultList();
        
        //Get all unread contact
        List<ContactRequest> unReadList = readUnreadContactReceivedList(profile);

        List<ContactRequest> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    @Override
    public List<ContactRequest> readContactSentList(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadAllContactRequestBySender", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        List<ContactRequest> readList = clQuery.getResultList();
        
        //Get all unread contact
        List<ContactRequest> unReadList = readUnreadContactSentList(profile);

        List<ContactRequest> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    @Override
    public List<ContactBlocked> readBlockList(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByProfile", ContactBlocked.class);
        clQuery.setParameter("user", profile);
        return clQuery.getResultList();
    }

    @Override
    public List<Contact> readContactList(String userID, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadAllContactByProfileOrContact", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contact", profile);
        clQuery.setParameter("status", Contact.RECEIVED_BY_CLIENT);
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);
        List<Contact> readList = clQuery.getResultList();
        
        ///Get all unread contact
        List<Contact> unReadList = readUnreadContactList(profile);

        List<Contact> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    @Override
    public List<ContactRequest> readContactReceivedList(String userID, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadAllContactRequestByReceiver", ContactRequest.class);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);
        List<ContactRequest> readList = clQuery.getResultList();
        
        //Get all unread contact
        List<ContactRequest> unReadList = readUnreadContactReceivedList(profile);

        List<ContactRequest> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    @Override
    public List<ContactRequest> readContactSentList(String userID, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadAllContactRequestBySender", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);
        List<ContactRequest> readList = clQuery.getResultList();
        
        //Get all unread contact
        List<ContactRequest> unReadList = readUnreadContactSentList(profile);

        List<ContactRequest> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    @Override
    public List<ContactBlocked> readBlockList(String userID, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByProfile", ContactBlocked.class);
        clQuery.setParameter("user", profile);
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);
        return clQuery.getResultList();
    }

    @Override
    public List<Contact> readUnreadContactList(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadContactByProfileOrContactAndStatus", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contact", profile);
        clQuery.setParameter("status", Contact.RECEIVED_BY_SERVER);

        //Get all unread contact
        List<Contact> unReadContacts = clQuery.getResultList();
        for (Contact contact : unReadContacts) {
            contact.setStatus(Contact.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    @Override
    public List<ContactRequest> readUnreadContactReceivedList(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequestByReceiverAndStatus", ContactRequest.class);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_SERVER);

        //Get all unread contact
        List<ContactRequest> unReadContacts = clQuery.getResultList();
        for (ContactRequest contact : unReadContacts) {
            contact.setStatus(ContactRequest.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    @Override
    public List<ContactRequest> readUnreadContactSentList(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequestBySenderAndStatus", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_SERVER);

        //Get all unread contact
        List<ContactRequest> unReadContacts = clQuery.getResultList();
        for (ContactRequest contact : unReadContacts) {
            contact.setStatus(ContactRequest.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    @Override
    public List<ContactBlocked> readUnreadBlockList(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByProfileAndStatus", ContactBlocked.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("status", ContactBlocked.RECEIVED_BY_SERVER);

        return clQuery.getResultList();
    }

    @Override
    public List<Contact> readUnreadContactList(String userID, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadContactByProfileOrContactAndStatus", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contact", profile);
        clQuery.setParameter("status", Contact.RECEIVED_BY_SERVER);
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        //Get all unread contact
        List<Contact> unReadContacts = clQuery.getResultList();
        for (Contact contact : unReadContacts) {
            contact.setStatus(Contact.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    @Override
    public List<ContactRequest> readUnreadContactReceivedList(String userID, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequestByReceiverAndStatus", ContactRequest.class);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_SERVER);
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        //Get all unread contact
        List<ContactRequest> unReadContacts = clQuery.getResultList();
        for (ContactRequest contact : unReadContacts) {
            contact.setStatus(ContactRequest.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    @Override
    public List<ContactRequest> readUnreadContactSentList(String userID, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequestBySenderAndStatus", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_SERVER);
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        //Get all unread contact
        List<ContactRequest> unReadContacts = clQuery.getResultList();
        for (ContactRequest contact : unReadContacts) {
            contact.setStatus(ContactRequest.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    @Override
    public List<ContactBlocked> readUnreadBlockList(String userID, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByProfileAndStatus", ContactBlocked.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("status", ContactBlocked.RECEIVED_BY_SERVER);
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        return clQuery.getResultList();
    }

    private List<Contact> readContactList(Profile profile) {

        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadAllContactByProfileOrContact", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contact", profile);
        clQuery.setParameter("status", Contact.RECEIVED_BY_CLIENT);
        List<Contact> readList = clQuery.getResultList();
        
        //Get all unread contact
        List<Contact> unReadList = readUnreadContactList(profile);

        List<Contact> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    private List<ContactRequest> readContactReceivedList(Profile profile) {

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadAllContactRequestByReceiver", ContactRequest.class);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        List<ContactRequest> readList = clQuery.getResultList();
        
        //Get all unread contact
        List<ContactRequest> unReadList = readUnreadContactReceivedList(profile);

        List<ContactRequest> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    private List<ContactRequest> readContactSentList(Profile profile) {

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadAllContactRequestBySender", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_CLIENT);
        List<ContactRequest> readList = clQuery.getResultList();
        
        //Get all unread contact
        List<ContactRequest> unReadList = readUnreadContactSentList(profile);

        List<ContactRequest> contacts = Stream.concat(unReadList.stream(), readList.stream()).collect(Collectors.toList());
        return contacts;
    }

    private List<ContactBlocked> readBlockList(Profile profile) {

        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByProfile", ContactBlocked.class);
        clQuery.setParameter("user", profile);
        return clQuery.getResultList();
    }
    
    private List<ContactBlocked> readBlockedByList(Profile profile) {

        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByContact", ContactBlocked.class);
        clQuery.setParameter("contact", profile);
        return clQuery.getResultList();
    }

    private List<Contact> readUnreadContactList(Profile profile) {

        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadContactByProfileOrContactAndStatus", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contact", profile);
        clQuery.setParameter("status", Contact.RECEIVED_BY_SERVER);

        //Get all unread contact
        List<Contact> unReadContacts = clQuery.getResultList();
        for (Contact contact : unReadContacts) {
            contact.setStatus(Contact.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    private List<ContactRequest> readUnreadContactReceivedList(Profile profile) {

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequestByReceiverAndStatus", ContactRequest.class);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_SERVER);

        //Get all unread contact
        List<ContactRequest> unReadContacts = clQuery.getResultList();
        for (ContactRequest contact : unReadContacts) {
            contact.setStatus(ContactRequest.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    private List<ContactRequest> readUnreadContactSentList(Profile profile) {

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequestBySenderAndStatus", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("status", ContactRequest.RECEIVED_BY_SERVER);

        //Get all unread contact
        List<ContactRequest> unReadContacts = clQuery.getResultList();
        for (ContactRequest contact : unReadContacts) {
            contact.setStatus(ContactRequest.RECEIVED_BY_CLIENT);
            em.persist(contact);
        }

        return unReadContacts;
    }

    private Contact readContactByProfileAndContact(Profile profile, Profile contact) {

        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadContactByProfileAndContact", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contact", contact);

        try {
            return clQuery.getSingleResult();
        } catch (Exception e) {
        }

        return null;
    }

    private ContactRequest readContactRequestBySenderAndReceiver(Profile profile, Profile contact) {

        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequestBySenderAndReceiver", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("receiver", contact);

        try {
            return clQuery.getSingleResult();
        } catch (Exception e) {
        }

        return null;
    }

    private ContactBlocked readContactBlockedByProfileAndContact(Profile profile, Profile contact) {

        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByProfileAndContact", ContactBlocked.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contact", contact);

        try {
            return clQuery.getSingleResult();
        } catch (Exception e) {
        }

        return null;
    }

    private boolean isContactRelatedToProfile(Profile profile, Profile contact) {

        if (readContactByProfileAndContact(profile, contact) != null) {
            return true;
        }

        if (readContactByProfileAndContact(contact, profile) != null) {
            return true;
        }
        //check if user send request
        if (readContactRequestBySenderAndReceiver(profile, contact) != null) {
            return true;
        }

        //Check if contact send request
        if (readContactRequestBySenderAndReceiver(contact, profile) != null) {
            return true;
        }

        if (readContactBlockedByProfileAndContact(profile, contact) != null) {
            return true;
        }

        return readContactBlockedByProfileAndContact(contact, profile) != null;

    }

    @Override
    public boolean addRequest(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }
        //Check if its valid for Request
        //Check if the contact profile not in Contact/RequestReceived/RequestAccepted/Blocked/BlockedBy
        if (isContactRelatedToProfile(profile, contact)) {
            return false;
        }

        //Add to Contact Request in user
        ContactRequest cs = new ContactRequest(profile, contact, ContactRequest.SENT_BY_CLIENT);
        em.persist(cs);

        return true;
    }

    @Override
    public boolean changeContactRequestStatus(String userID, String contactID, int status) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }
        //Change status
        ContactRequest cp = readContactRequestBySenderAndReceiver(profile, contact);
        if (cp != null) {
            cp.setStatus(status);
            em.persist(cp);
        }

        ContactRequest cc = readContactRequestBySenderAndReceiver(contact, profile);
        if (cc != null) {
            cc.setStatus(status);
            em.persist(cc);
        }

        return true;
    }

    @Override
    public boolean changeContactStatus(String userID, String contactID, int status) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);

        //Change status
        Contact cp = readContactByProfileAndContact(profile, contact);
        if (cp != null) {
            cp.setStatus(status);
            em.persist(cp);
        }

        Contact cc = readContactByProfileAndContact(contact, profile);
        if (cc != null) {
            cc.setStatus(status);
            em.persist(cc);
        }

        return true;
    }

    @Override
    public boolean changeBlockStatus(String userID, String contactID, int status) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);

        //Change status
        ContactBlocked cs = readContactBlockedByProfileAndContact(profile, contact);
        if (cs != null) {
            cs.setStatus(status);
            em.persist(cs);
        }

        return true;
    }

    @Override
    public boolean changeContactRequestStatus(String userID, List<String> contactIDs, int status) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        for (String sContactID : contactIDs) {
            Profile contact = profileManager.readProfileByUserID(sContactID);

            //Change status
            ContactRequest cp = readContactRequestBySenderAndReceiver(profile, contact);
            if (cp != null) {
                cp.setStatus(status);
                em.persist(cp);
            }

            ContactRequest cc = readContactRequestBySenderAndReceiver(contact, profile);
            if (cc != null) {
                cc.setStatus(status);
                em.persist(cc);
            }
        }

        return true;
    }

    @Override
    public boolean changeContactStatus(String userID, List<String> contactIDs, int status) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        for (String sContactID : contactIDs) {
            Profile contact = profileManager.readProfileByUserID(sContactID);

            //Change status
            Contact cp = readContactByProfileAndContact(profile, contact);
            if (cp != null) {
                cp.setStatus(status);
                em.persist(cp);
            }

            Contact cc = readContactByProfileAndContact(contact, profile);
            if (cc != null) {
                cc.setStatus(status);
                em.persist(cc);
            }
        }

        return true;
    }

    @Override
    public boolean changeBlockStatus(String userID, List<String> contactIDs, int status) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        for (String sContactID : contactIDs) {
            Profile contact = profileManager.readProfileByUserID(sContactID);

            //Change status
            ContactBlocked cs = readContactBlockedByProfileAndContact(profile, contact);
            if (cs != null) {
                cs.setStatus(status);
                em.persist(cs);
            }
        }

        return true;
    }

    public boolean changeContactRequestStatus(Profile user, List<Profile> contacts, int status) {

        if (user == null) {
            return false;
        }

        for (Profile contact : contacts) {

            //Change status
            ContactRequest cu = readContactRequestBySenderAndReceiver(user, contact);
            if (cu != null) {
                cu.setStatus(status);
                em.persist(cu);
            }

            ContactRequest cc = readContactRequestBySenderAndReceiver(contact, user);
            if (cc != null) {
                cc.setStatus(status);
                em.persist(cc);
            }
        }

        return true;
    }

    public boolean changeContactStatus(Profile user, List<Profile> contacts, int status) {

        if (user == null) {
            return false;
        }

        for (Profile contact : contacts) {

            //Change status
            Contact cu = readContactByProfileAndContact(user, contact);
            if (cu != null) {
                cu.setStatus(status);
                em.persist(cu);
            }

            Contact cc = readContactByProfileAndContact(contact, user);
            if (cc != null) {
                cc.setStatus(status);
                em.persist(cc);
            }
        }

        return true;
    }

    public boolean changeBlockStatus(Profile user, List<Profile> contacts, int status) {

        if (user == null) {
            return false;
        }

        for (Profile contact : contacts) {

            //Change status
            ContactBlocked cs = readContactBlockedByProfileAndContact(user, contact);
            if (cs != null) {
                cs.setStatus(status);
                em.persist(cs);
            }
        }

        return true;
    }

    @Override
    public boolean removeContact(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }

        boolean found = false;
        //Search by profile
        Contact cp = readContactByProfileAndContact(profile, contact);
        if (cp != null) {
            found = true;
            //Remove from Contact
            em.remove(cp);
        }

        Contact cc = readContactByProfileAndContact(contact, profile);
        if (cc != null) {
            found = true;
            //Remove from Contact
            em.remove(cc);
        }

        return found;
    }

    @Override
    public boolean removeRequestSent(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }

        //Search by contact receiver
        boolean found = false;
        ContactRequest cr = readContactRequestBySenderAndReceiver(contact, profile);
        if (cr != null) {
            found =true;
            //Remove from ContactRequest
            em.remove(cr);
        }
        
        ContactRequest cs = readContactRequestBySenderAndReceiver(profile, contact);
        if (cs != null) {
            found =true;
            //Remove from ContactRequest
            em.remove(cs);
        }

        return found;
    }

    @Override
    public boolean removeRequestReceived(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }

        //Search by contact receiver
        boolean found = false;
        ContactRequest cr = readContactRequestBySenderAndReceiver(contact, profile);
        if (cr != null) {
            found =true;
            //Remove from ContactRequest
            em.remove(cr);
        }
        
        ContactRequest cs = readContactRequestBySenderAndReceiver(profile, contact);
        if (cs != null) {
            found =true;
            //Remove from ContactRequest
            em.remove(cs);
        }

        return found;

    }

    @Override
    public boolean acceptRequest(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }

        //Search by contact
        ContactRequest cr = readContactRequestBySenderAndReceiver(contact, profile);
        if (cr == null) {
            return false;
        }

        //Create Contact
        Contact c = new Contact(profile, contact, Contact.SENT_BY_CLIENT);

        //Remove from ContactRequest
        em.remove(cr);

        //Add contact
        em.persist(c);

        return true;
    }

    @Override
    public boolean blockOther(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }
        //Check if the contact profile not in Contact/RequestReceived/RequestAccepted/Blocked/BlockedBy
        if (isContactRelatedToProfile(profile, contact)) {
            return false;
        }
        //Create Contact
        ContactBlocked cb = new ContactBlocked(profile, contact, ContactBlocked.RECEIVED_BY_SERVER);

        //Add contact
        em.persist(cb);

        return true;
    }

    @Override
    public boolean blockContact(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }

        boolean found = false;
        //Search by contact
        Contact cc = readContactByProfileAndContact(contact, profile);
        if (cc != null) {
            found = true;
            //Remove from Contact
            em.remove(cc);
        }

        //Search by profile
        Contact cp = readContactByProfileAndContact(profile, contact);
        if (cp != null) {
            found = true;
            //Remove from Contact
            em.remove(cp);
        }

        if (!found) {
            return false;
        }

        //Create Contact
        ContactBlocked cb = new ContactBlocked(profile, contact, ContactBlocked.RECEIVED_BY_SERVER);

        //Add contact
        em.persist(cb);

        return true;
    }

    @Override
    public boolean blockContactReceived(String userID, String contactID) {

        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }

        //Search by contact receiver
        boolean found = false;
        ContactRequest cr = readContactRequestBySenderAndReceiver(contact, profile);
        if (cr != null) {
            found =true;
            //Remove from ContactRequest
            em.remove(cr);
        }
        
        ContactRequest cs = readContactRequestBySenderAndReceiver(profile, contact);
        if (cs != null) {
            found =true;
            //Remove from ContactRequest
            em.remove(cs);
        }

        if(!found){
            return false;
        }
        
        //Create Contact
        ContactBlocked cb = new ContactBlocked(profile, contact, ContactBlocked.RECEIVED_BY_SERVER);

        //Add contact
        em.persist(cb);

        return true;
    }

    @Override
    public boolean blockContactSent(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }

        //Search by contact receiver
        boolean found = false;
        ContactRequest cr = readContactRequestBySenderAndReceiver(contact, profile);
        if (cr != null) {
            found =true;
            //Remove from ContactRequest
            em.remove(cr);
        }
        
        ContactRequest cs = readContactRequestBySenderAndReceiver(profile, contact);
        if (cs != null) {
            found =true;
            //Remove from ContactRequest
            em.remove(cs);
        }

        if(!found){
            return false;
        }

        //Create Contact
        ContactBlocked cb = new ContactBlocked(profile, contact, ContactBlocked.RECEIVED_BY_SERVER);

        //Add contact
        em.persist(cb);

        return true;
    }

    @Override
    public boolean removeBlock(String userID, String contactID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return false;
        }

        Profile contact = profileManager.readProfileByUserID(contactID);
        if (contact == null) {
            return false;
        }

        //Search by contact sender
        ContactBlocked cb = readContactBlockedByProfileAndContact(profile, contact);
        if (cb == null) {
            return false;
        }

        //Remove from ContactBlock
        em.remove(cb);

        return true;
    }

    @Override
    public List<Contact> searchContactList(String userID, String searchText) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadContactByProfileAndContactName", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contactName", "%" + searchText + "%");

        return clQuery.getResultList();
    }

    @Override
    public List<ContactRequest> searchContactReceivedList(String userID, String searchText) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequesBySenderAndReceiverName", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("receiverName", "%" + searchText + "%");

        return clQuery.getResultList();
    }

    @Override
    public List<ContactRequest> searchContactSentList(String userID, String searchText) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequesByReceiverAndSenderName", ContactRequest.class);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("senderName", "%" + searchText + "%");

        return clQuery.getResultList();
    }

    @Override
    public List<ContactBlocked> searchContactBlockedList(String userID, String searchText) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByProfileAndContactName", ContactBlocked.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contactName", "%" + searchText + "%");

        return clQuery.getResultList();
    }

    @Override
    public List<Profile> searchProfile(String userID, String searchText) {

        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        List<String> pList = new ArrayList<>();
        pList.add(profile.getRegister().getUserID());
        //Read All Related Profiles
        List<Contact> cList = readContactList(profile);
        for (Contact contact : cList) {
            pList.add(contact.getContact().getRegister().getUserID());
        }
        List<ContactRequest> crList = readContactReceivedList(profile);
        for (ContactRequest request : crList) {
            pList.add(request.getSender().getRegister().getUserID());
        }
        List<ContactRequest> csList = readContactSentList(profile);
        for (ContactRequest request : csList) {
            pList.add(request.getReceiver().getRegister().getUserID());
        }
        List<ContactBlocked> bList = readBlockList(profile);
        for (ContactBlocked contactB : bList) {
            pList.add(contactB.getContact().getRegister().getUserID());
        }
        List<ContactBlocked> bcList = readBlockedByList(profile);
        for (ContactBlocked contactB : bcList) {
            pList.add(contactB.getUser().getRegister().getUserID());
        }

        return profileManager.readProfileListByProfileNameAndExcludingList(searchText, pList);
    }

    @Override
    public Profile searchProfileByUserID(String userID, String searchUserID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        List<String> pList = new ArrayList<>();
        pList.add(profile.getRegister().getUserID());
        //Read All Related Profiles
        List<Contact> cList = readContactList(profile);
        for (Contact contact : cList) {
            pList.add(contact.getContact().getRegister().getUserID());
        }
        List<ContactRequest> crList = readContactReceivedList(profile);
        for (ContactRequest request : crList) {
            pList.add(request.getSender().getRegister().getUserID());
        }
        List<ContactRequest> csList = readContactSentList(profile);
        for (ContactRequest request : csList) {
            pList.add(request.getReceiver().getRegister().getUserID());
        }
        List<ContactBlocked> bList = readBlockList(profile);
        for (ContactBlocked contactB : bList) {
            pList.add(contactB.getContact().getRegister().getUserID());
        }
        List<ContactBlocked> bcList = readBlockedByList(profile);
        for (ContactBlocked contactB : bcList) {
            pList.add(contactB.getUser().getRegister().getUserID());
        }
        
        return profileManager.readProfileListByUserIDAndExcludingList(userID, pList);
    }

    @Override
    public List<Contact> searchContactList(String userID, String searchText, int start, int length) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<Contact> clQuery = em.createNamedQuery("ReadContactByProfileAndContactName", Contact.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contactName", "%" + searchText + "%");
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        return clQuery.getResultList();
    }

    @Override
    public List<ContactRequest> searchContactReceivedList(String userID, String searchText, int start, int length) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequesBySenderAndReceiverName", ContactRequest.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("receiverName", "%" + searchText + "%");
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        return clQuery.getResultList();
    }

    @Override
    public List<ContactRequest> searchContactSentList(String userID, String searchText, int start, int length) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<ContactRequest> clQuery = em.createNamedQuery("ReadContactRequesByReceiverAndSenderName", ContactRequest.class);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("senderName", "%" + searchText + "%");
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        return clQuery.getResultList();
    }

    @Override
    public List<ContactBlocked> searchContactBlockedList(String userID, String searchText, int start, int length) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<ContactBlocked> clQuery = em.createNamedQuery("ReadContactBlockedByProfileAndContactName", ContactBlocked.class);
        clQuery.setParameter("user", profile);
        clQuery.setParameter("contactName", "%" + searchText + "%");
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        return clQuery.getResultList();
    }

    @Override
    public List<Profile> searchProfile(String userID, String searchText, int start, int length) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        List<String> pList = new ArrayList<>();
        pList.add(profile.getRegister().getUserID());
        //Read All Related Profiles
        List<Contact> cList = readContactList(profile);
        for (Contact contact : cList) {
            pList.add(contact.getContact().getRegister().getUserID());
        }
        List<ContactRequest> crList = readContactReceivedList(profile);
        for (ContactRequest request : crList) {
            pList.add(request.getSender().getRegister().getUserID());
        }
        List<ContactRequest> csList = readContactSentList(profile);
        for (ContactRequest request : csList) {
            pList.add(request.getReceiver().getRegister().getUserID());
        }
        List<ContactBlocked> buList = readBlockList(profile);
        for (ContactBlocked contactB : buList) {
            pList.add(contactB.getContact().getRegister().getUserID());
        }
        List<ContactBlocked> bcList = readBlockedByList(profile);
        for (ContactBlocked contactB : bcList) {
            pList.add(contactB.getUser().getRegister().getUserID());
        }

        return profileManager.readProfileListByProfileNameAndExcludingList(searchText, pList, start, length);
    }

    @Override
    public Profile searchProfileByUserID(String userID, String searchUserID, int start, int length) {
        Profile profile = profileManager.readProfileByUserID(userID);
        if (profile == null) {
            return null;
        }

        List<String> pList = new ArrayList<>();
        pList.add(profile.getRegister().getUserID());
        //Read All Related Profiles
        List<Contact> cList = readContactList(profile);
        for (Contact contact : cList) {
            pList.add(contact.getContact().getRegister().getUserID());
        }
        List<ContactRequest> crList = readContactReceivedList(profile);
        for (ContactRequest request : crList) {
            pList.add(request.getSender().getRegister().getUserID());
        }
        List<ContactRequest> csList = readContactSentList(profile);
        for (ContactRequest request : csList) {
            pList.add(request.getReceiver().getRegister().getUserID());
        }
        List<ContactBlocked> bList = readBlockList(profile);
        for (ContactBlocked contactB : bList) {
            pList.add(contactB.getContact().getRegister().getUserID());
        }
        List<ContactBlocked> bcList = readBlockedByList(profile);
        for (ContactBlocked contactB : bcList) {
            pList.add(contactB.getUser().getRegister().getUserID());
        }
        
        return profileManager.readProfileListByUserIDAndExcludingList(userID, pList, start, length);
    }
}
