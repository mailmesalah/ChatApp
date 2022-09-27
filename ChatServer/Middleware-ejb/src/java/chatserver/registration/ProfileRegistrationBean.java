/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.registration;

import chatserver.database.PhoneContacts;
import chatserver.database.PhoneUser;
import chatserver.database.Profile;
import chatserver.database.Register;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Sely
 */
@Stateless
public class ProfileRegistrationBean implements ProfileRegistration {

    @PersistenceContext(unitName = "DatabasePU")
    EntityManager em;

  
    @Override
    public boolean isUserIDUsed(String userID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Register> cq = cb.createQuery(Register.class);
        Root<Register> r = cq.from(Register.class);
        cq.where(cb.equal(r.get("userID"), userID));

        TypedQuery<Register> q = em.createQuery(cq);
        List<Register> results = q.getResultList();
        return !results.isEmpty();
    }

    @Override
    public boolean isPhoneNumberUsed(String phoneNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Register> cq = cb.createQuery(Register.class);
        Root<Register> r = cq.from(Register.class);
        cq.where(cb.equal(r.get("phoneNumber"), phoneNumber));

        TypedQuery<Register> q = em.createQuery(cq);
        List<Register> results = q.getResultList();
        return !results.isEmpty();
    }

    @Override
    public Register createRegister(String userID, String phoneNumber, String deviceID, String password) {
        TypedQuery<Register> query = em.createNamedQuery("IsUserIDPhoneNumberUsed", Register.class);
        query.setParameter("userID", userID);
        query.setParameter("phoneNumber", phoneNumber);

        List<Register> registerList = query.getResultList();

        if (!registerList.isEmpty()) {
            return null;
        }

        String loginID = userID + phoneNumber + Instant.now();
        //Creating Register
        Register r = new Register(userID, loginID, deviceID, phoneNumber, password);
        em.persist(r);

        return r;
    }

    @Override
    public boolean createPhoneContacts(String userID, Map<String, String> contacts) {
        try {
            List<PhoneUser> phoneContacts = new ArrayList<>();
            for (Map.Entry<String, String> entry : contacts.entrySet()) {
                String phone = entry.getKey();
                String userName = entry.getValue();

                PhoneUser pu = new PhoneUser(userName, phone);
                if (!phoneContacts.contains(pu)) {
                    phoneContacts.add(pu);
                }
            }

            PhoneContacts pc = new PhoneContacts(userID, phoneContacts);
            em.persist(pc);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Profile createProfile(String userID, String profileName, byte[] profileImage, String quote) {

        //Creating profile
        Profile profile = null;
        try {           
            //Check if profile already created on the userID
            TypedQuery<Profile> query = em.createNamedQuery("ReadProfileByUserID", Profile.class);
            query.setParameter("userID", userID);

            List<Profile> profileList = query.getResultList();

            if (!profileList.isEmpty()) {
                return null;
            }
            
            Register register = readRegister(userID);            
            //Create Profile
            profile = new Profile(register, profileName, profileImage, quote);
            em.persist(profile);
        } catch (Exception e) {
            return null;
        }

        return profile;
    }

    @Override
    public Register readRegister(String userID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Register> cq = cb.createQuery(Register.class);
        Root<Register> r = cq.from(Register.class);
        cq.where(cb.equal(r.get("userID"), userID));

        TypedQuery<Register> q = em.createQuery(cq);
        
        try{
            return q.getSingleResult();
        }catch(Exception e){}
        
        return null;
    }

    @Override
    public Register recoverByPhoneNumber(String phoneNumber, String deviceID, String password) {
        TypedQuery<Register> query = em.createNamedQuery("ReadRegisterByPhoneNumberAndPassword", Register.class);
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("password", password);

        Register register = null;
        try{
            register = query.getSingleResult();
        }catch(Exception e){}
        
        if (register != null) {
            register.setDeviceID(deviceID);
            //Save with updated device id
            em.persist(register);
        }
        return register;
    }

    @Override
    public Register recoverByUserID(String userID, String deviceID, String password) {
        TypedQuery<Register> query = em.createNamedQuery("ReadRegisterByUserIDAndPassword", Register.class);
        query.setParameter("userID", userID);
        query.setParameter("password", password);

        Register register = null;
        try{
            register = query.getSingleResult();
        }catch(Exception e){}
        
        if (register != null) {
            register.setDeviceID(deviceID);
            //Save with updated device id
            em.persist(register);
        }
        return register;
    }
}
