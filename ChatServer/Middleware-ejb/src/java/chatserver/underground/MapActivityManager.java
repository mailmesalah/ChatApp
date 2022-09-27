/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import chatserver.database.ProfileMap;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Sely
 */
@Local
public interface MapActivityManager {

    public final static int PRECISION=100000;
    List<ProfileMap> readProfilesAround(float latitude, float longitude, int distance);
    
    boolean updateProfileInMap(String userID, float latitude, float longitude);
 
    boolean removeProfileFromMap(String userID, float latitude, float longitude);
}
