package chatserver.database;

import chatserver.database.Profile;
import java.util.Calendar;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-15T17:06:44")
@StaticMetamodel(DualChat.class)
public class DualChat_ { 

    public static volatile SingularAttribute<DualChat, Calendar> receiveTime;
    public static volatile SingularAttribute<DualChat, String> messageText;
    public static volatile SingularAttribute<DualChat, Integer> messageType;
    public static volatile SingularAttribute<DualChat, Profile> receiver;
    public static volatile SingularAttribute<DualChat, Profile> sender;
    public static volatile SingularAttribute<DualChat, String> filePath;
    public static volatile SingularAttribute<DualChat, Boolean> deletedBySender;
    public static volatile SingularAttribute<DualChat, Long> id;
    public static volatile SingularAttribute<DualChat, Integer> seenBy;
    public static volatile SingularAttribute<DualChat, Boolean> deletedByReceiver;
    public static volatile SingularAttribute<DualChat, Calendar> sendTime;

}