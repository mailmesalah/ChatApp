/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import chatserver.database.DualChat;
import chatserver.database.MapData;
import chatserver.database.Profile;
import chatserver.database.ProfileMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class MapActivityManagerBean implements MapActivityManager{

    @PersistenceContext(unitName = "DatabasePU")
    EntityManager em;

    @EJB
    private ProfileManager profileManager;

    @Override
    public List<ProfileMap> readProfilesAround(float latitude, float longitude, int distance) {
        int xMin=(int) (latitude*PRECISION);
        int yMin=(int) (longitude*PRECISION);
        int xMax=xMin+distance;
        int yMax=yMin+distance;
        
        TypedQuery<MapData> mdQuery = em.createNamedQuery("ReadMapDataByRange", MapData.class);
        mdQuery.setParameter("xMin", xMin);
        mdQuery.setParameter("xMax", xMax);
        mdQuery.setParameter("yMin", yMin);
        mdQuery.setParameter("yMax", yMax);
        
        List<MapData> mdList = mdQuery.getResultList();
        List<ProfileMap> pmList = new ArrayList<>();
        for (MapData mapData : mdList) {
            pmList.addAll(mapData.getProfiles());
        }
        
        return pmList;
    }

    @Override
    public boolean updateProfileInMap(String userID, float latitude, float longitude) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return false
        if (profile == null) {
            return false;
        }
        
        int x=(int) (latitude*PRECISION);
        int y=(int) (longitude*PRECISION);
        
        TypedQuery<MapData> md1Query = em.createNamedQuery("ReadMapDataByXYProfile", MapData.class);
        md1Query.setParameter("x", x);
        md1Query.setParameter("y", y);
        md1Query.setParameter("profile", profile);
                
        //If already exist update it
        List<MapData> md1List = md1Query.getResultList();
        for (MapData mapData : md1List) {
            for (ProfileMap pm : mapData.getProfiles()) {
                if(pm.getProfile().getRegister().getUserID().equals(userID)){
                    //Update and save                                       
                    pm.setLatitude(latitude);
                    pm.setLongitude(longitude);
                    em.persist(md1List);
                    return true;
                }
            }
        }
        
        //check if it exist in any other area if yes remove from it and ass to current MapData item.
        TypedQuery<MapData> md2Query = em.createNamedQuery("ReadMapDataByProfile", MapData.class);
        md2Query.setParameter("profile", profile);
        //If already remove it
        boolean found = false;
        List<MapData> md2List = md2Query.getResultList();
        for (MapData mapData : md2List) {
            for (ProfileMap pm : mapData.getProfiles()) {
                if(pm.getProfile().getRegister().getUserID().equals(userID)){
                    //remove and save
                    mapData.getProfiles().remove(pm);                    
                    found = true;
                }
            }
        }
        
        if(found){
            em.persist(md2List);
        }
               
        if(md1List!=null){
            //Add to the current MapData Item
            ProfileMap pm = new ProfileMap(latitude, longitude, profile);
            md1List.get(0).getProfiles().add(pm);
            
            em.persist(md1List);
        }else{
            //If there is no current MapData, create one
            List<ProfileMap> pmList = new ArrayList<>();
            pmList.add(new ProfileMap(latitude, longitude, profile));
            MapData md = new MapData(x, y, pmList);
            
            em.persist(md);
        }
        
        return true;
    }

    @Override
    public boolean removeProfileFromMap(String userID, float latitude, float longitude) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return false
        if (profile == null) {
            return false;
        }
        
        int x=(int) (latitude*PRECISION);
        int y=(int) (longitude*PRECISION);
        
        TypedQuery<MapData> md1Query = em.createNamedQuery("ReadMapDataByXYProfile", MapData.class);
        md1Query.setParameter("x", x);
        md1Query.setParameter("y", y);
        md1Query.setParameter("profile", profile);
                
        //If already exist remove it
        List<MapData> md1List = md1Query.getResultList();
        for (MapData mapData : md1List) {
            for (ProfileMap pm : mapData.getProfiles()) {
                if(pm.getProfile().getRegister().getUserID().equals(userID)){
                    //Remove and Save                 
                    mapData.getProfiles().remove(pm);
                   
                    em.persist(md1List);
                    return true;
                }
            }
        }
        
        //Search if exist anywhere else
        TypedQuery<MapData> md2Query = em.createNamedQuery("ReadMapDataByProfile", MapData.class);
        md2Query.setParameter("profile", profile);
        //If already remove it
        boolean found = false;
        List<MapData> md2List = md2Query.getResultList();
        for (MapData mapData : md2List) {
            for (ProfileMap pm : mapData.getProfiles()) {
                if(pm.getProfile().getRegister().getUserID().equals(userID)){
                    //remove and save
                    mapData.getProfiles().remove(pm);                    
                    found = true;
                }
            }
        }
        if(found){
            em.persist(md2List);            
            return true;
        }
        
        return false;
    }

    

}
