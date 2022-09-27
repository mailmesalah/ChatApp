/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import chatserver.database.DualChat;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Sely
 */
@Local
public interface DualChatManager {

    List<DualChat> readChats(String userID, int start, int length);    
    List<DualChat> readUnreadChats(String userID);
    
    DualChat addChat(DualChat dc);        
    boolean deleteChatBySender(String userID, Long id);        
    boolean deleteChatByReceiver(String userID, Long id);        
    boolean chatSeenByReceiver(String userID, Long id);
    
    boolean isChatSeenByReceiver(String userID, Long id);
    
    boolean addChats(List<DualChat> dcList);        
    boolean deleteChatsBySender(String userID, List<Long> dcList);        
    boolean deleteChatsByReceiver(String userID, List<Long> dcList);        
    boolean chatsSeenByReceiver(String userID, List<Long> dcList);
    
    //Search Methods
    List<DualChat> searchChatTexts(String userID, String searchText, int start, int length);

 
}
