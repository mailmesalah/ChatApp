/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import chatserver.database.Profile;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Sely
 */
@Local
public interface ProfileManager {

    boolean loginToRegister(String userID, String loginID, String deviceID);
    List<Profile> readProfiles();
    List<Profile> readProfiles(int start, int length);
    Profile readProfileByUserID(String userID);
    List<Profile> readProfileListByProfileName(String searchText);
    List<Profile> readProfileListByExcludingList(List<String> excludeList);
    List<Profile> readProfileListByProfileNameAndExcludingList(String searchText, List<String> excludeList);
    Profile readProfileListByUserIDAndExcludingList(String userID, List<String> excludeList);
    List<Profile> readProfileListByExcludingList(List<String> excludeList, int start, int length);
    Profile readProfileListByUserIDAndExcludingList(String userID, List<String> excludeList, int start, int length);
    List<Profile> readProfileListByProfileNameAndExcludingList(String searchText, List<String> excludeList, int start, int length);
}
