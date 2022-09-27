/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import chatserver.database.Profile;
import chatserver.database.Register;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Sely
 */
@Stateless
public class ProfileManagerBean implements ProfileManager {

    @PersistenceContext(unitName = "DatabasePU")
    EntityManager em;

    @Override
    public Profile readProfileByUserID(String userID) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileByUserID", Profile.class);
        pQuery.setParameter("userID", userID);
        Profile profile = null;
        try {
            profile = pQuery.getSingleResult();
        } catch (Exception e) {
        }

        return profile;        
    }

    @Override
    public List<Profile> readProfileListByProfileName(String searchText) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileListByProfileName", Profile.class);
        pQuery.setParameter("profileName", "%"+searchText+"%");       

        return pQuery.getResultList(); 
    }

    @Override
    public List<Profile> readProfileListByExcludingList(List<String> excludeList) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileListByProfileName", Profile.class);
        pQuery.setParameter("profileIDs", excludeList);       

        return pQuery.getResultList();
    }
    
    @Override
    public List<Profile> readProfileListByProfileNameAndExcludingList(String searchText, List<String> excludeList) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileListByProfileNameAndExcludingList", Profile.class);
        pQuery.setParameter("profileIDs", excludeList);  
        pQuery.setParameter("profileName", "%"+searchText+"%");

        return pQuery.getResultList();
    }

    @Override
    public Profile readProfileListByUserIDAndExcludingList(String userID, List<String> excludeList) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileListByProfileAndExcludingList", Profile.class);
        pQuery.setParameter("userID", userID);       
        pQuery.setParameter("profileIDs", excludeList);       

        try{
            return pQuery.getSingleResult();
        }catch(Exception e){}
        
        return null;
    }

    @Override
    public List<Profile> readProfileListByExcludingList(List<String> excludeList, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileListByExcludingList", Profile.class);
        pQuery.setParameter("profileIDs", excludeList);   
        pQuery.setFirstResult(start);
        pQuery.setMaxResults(length);

        return pQuery.getResultList();
    }

    @Override
    public Profile readProfileListByUserIDAndExcludingList(String userID, List<String> excludeList, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileListByProfileAndExcludingList", Profile.class);
        pQuery.setParameter("userID", userID);       
        pQuery.setParameter("profileIDs", excludeList);       
        pQuery.setFirstResult(start);
        pQuery.setMaxResults(length);

        try{
            return pQuery.getSingleResult();
        }catch(Exception e){}
        
        return null;
    }
    
    @Override
    public List<Profile> readProfileListByProfileNameAndExcludingList(String searchText, List<String> excludeList, int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfileListByProfileNameAndExcludingList", Profile.class);
        pQuery.setParameter("profileIDs", excludeList);  
        pQuery.setParameter("profileName", "%"+searchText+"%"); 
        pQuery.setFirstResult(start);
        pQuery.setMaxResults(length);

        return pQuery.getResultList();
    }

    @Override
    public boolean loginToRegister(String userID, String loginID, String deviceID) {
        TypedQuery<Register> rQuery = em.createNamedQuery("ReadRegisterByUserIDLoginIDDeviceID", Register.class);
        rQuery.setParameter("userID", userID);       
        rQuery.setParameter("loginID", loginID);
        rQuery.setParameter("deviceID", deviceID);
        
        Register reg=null;
        try{
            reg=rQuery.getSingleResult();
        }catch(Exception e){}
        
        return reg!=null;
    }

    @Override
    public List<Profile> readProfiles() {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfiles", Profile.class);      

        return pQuery.getResultList();
    }

    @Override
    public List<Profile> readProfiles(int start, int length) {
        TypedQuery<Profile> pQuery = em.createNamedQuery("ReadProfiles", Profile.class);
        pQuery.setFirstResult(start);
        pQuery.setMaxResults(length);

        return pQuery.getResultList();
    }

    
}
