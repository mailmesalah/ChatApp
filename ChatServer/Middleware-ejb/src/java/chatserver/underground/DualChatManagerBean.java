/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.underground;

import chatserver.database.DualChat;
import chatserver.database.Profile;
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
public class DualChatManagerBean implements DualChatManager {

    @PersistenceContext(unitName = "DatabasePU")
    EntityManager em;

    @EJB
    private ProfileManager profileManager;

    @Override
    public List<DualChat> readChats(String userID, int start, int length) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChat", DualChat.class);
        dcQuery.setParameter("sender", profile);
        dcQuery.setParameter("receiver", profile);
        dcQuery.setFirstResult(start);
        dcQuery.setMaxResults(length);

        return dcQuery.getResultList();
    }

    @Override
    public List<DualChat> readUnreadChats(String userID) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChatUnread", DualChat.class);
        dcQuery.setParameter("receiver", profile);
        dcQuery.setParameter("seenBy", DualChat.SERVER);

        List<DualChat> chats = dcQuery.getResultList();
        
        if(chats==null){
            return null;
        }
        
        return chats;
    }

    @Override
    public DualChat addChat(DualChat dc) {
        try {
            em.persist(dc);
        } catch (Exception e) {
            return null;
        }
        return dc;
    }

    @Override
    public boolean deleteChatBySender(String userID, Long id) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return false;
        }

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChatByIDAndSender", DualChat.class);
        dcQuery.setParameter("sender", profile);
        dcQuery.setParameter("ID", id);

        DualChat dc = null;
        try {
            dc = dcQuery.getSingleResult();
        } catch (Exception e) {
            return false;
        }

        if (dc == null) {
            return false;
        }

        dc.setDeletedBySender(true);

        em.persist(dc);

        return true;
    }

    @Override
    public boolean deleteChatByReceiver(String userID, Long id) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return false;
        }

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChatByIDAndReceiver", DualChat.class);
        dcQuery.setParameter("receiver", profile);
        dcQuery.setParameter("ID", id);

        DualChat dc = null;
        try {
            dc = dcQuery.getSingleResult();
        } catch (Exception e) {
            return false;
        }

        if (dc == null) {
            return false;
        }

        dc.setDeletedBySender(true);

        em.persist(dc);

        return true;
    }

    @Override
    public boolean chatSeenByReceiver(String userID, Long id) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return false;
        }

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChatByIDAndReceiver", DualChat.class);
        dcQuery.setParameter("receiver", profile);
        dcQuery.setParameter("ID", id);

        DualChat dc = null;
        try {
            dc = dcQuery.getSingleResult();
        } catch (Exception e) {
            return false;
        }

        if (dc == null) {
            return false;
        }

        dc.setSeenBy(DualChat.RECEIVER);

        em.persist(dc);

        return true;
    }
    
    @Override
    public boolean isChatSeenByReceiver(String userID, Long id) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return false;
        }

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChatByIDAndReceiver", DualChat.class);
        dcQuery.setParameter("receiver", profile);
        dcQuery.setParameter("ID", id);

        DualChat dc = null;
        try {
            dc = dcQuery.getSingleResult();
        } catch (Exception e) {
            return false;
        }

        if (dc == null) {
            return false;
        }

        return  dc.getSeenBy()==DualChat.RECEIVER;
    }

    @Override
    public boolean addChats(List<DualChat> dcList) {
        try {
            for (DualChat dc : dcList) {
                addChat(dc);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteChatsBySender(String userID, List<Long> idList) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return false;
        }

        try {
            for (Long id : idList) {
                deleteChatBySender(profile, id);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteChatsByReceiver(String userID, List<Long> idList) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return false;
        }
        try {
            for (Long id : idList) {
                deleteChatByReceiver(profile, id);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean chatsSeenByReceiver(String userID, List<Long> idList) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return false;
        }
        try {
            for (Long id : idList) {
                chatSeenByReceiver(profile, id);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<DualChat> searchChatTexts(String userID, String searchText, int start, int length) {
        Profile profile = profileManager.readProfileByUserID(userID);
        //If wrong userID return null
        if (profile == null) {
            return null;
        }

        //Search
        TypedQuery<DualChat> clQuery = em.createNamedQuery("ReadDualChatBySenderOrReceiverAndMessageText", DualChat.class);
        clQuery.setParameter("sender", profile);
        clQuery.setParameter("receiver", profile);
        clQuery.setParameter("messageText", "%" + searchText + "%");
        clQuery.setFirstResult(start);
        clQuery.setMaxResults(length);

        return clQuery.getResultList();
    }
    
    //private methods
    private boolean deleteChatBySender(Profile profile, Long id) {

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChatByIDAndSender", DualChat.class);
        dcQuery.setParameter("sender", profile);
        dcQuery.setParameter("ID", id);

        DualChat dc = null;
        try {
            dc = dcQuery.getSingleResult();
        } catch (Exception e) {
            return false;
        }

        if (dc == null) {
            return false;
        }

        dc.setDeletedBySender(true);

        em.persist(dc);

        return true;
    }

    private boolean deleteChatByReceiver(Profile profile, Long id) {

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChatByIDAndReceiver", DualChat.class);
        dcQuery.setParameter("receiver", profile);
        dcQuery.setParameter("ID", id);

        DualChat dc = null;
        try {
            dc = dcQuery.getSingleResult();
        } catch (Exception e) {
            return false;
        }

        if (dc == null) {
            return false;
        }

        dc.setDeletedBySender(true);

        em.persist(dc);

        return true;
    }

    private boolean chatSeenByReceiver(Profile profile, Long id) {

        TypedQuery<DualChat> dcQuery = em.createNamedQuery("ReadDualChatByIDAndReceiver", DualChat.class);
        dcQuery.setParameter("receiver", profile);
        dcQuery.setParameter("ID", id);

        DualChat dc = null;
        try {
            dc = dcQuery.getSingleResult();
        } catch (Exception e) {
            return false;
        }

        if (dc == null) {
            return false;
        }

        dc.setSeenBy(DualChat.RECEIVER);

        em.persist(dc);

        return true;
    }
  

}
