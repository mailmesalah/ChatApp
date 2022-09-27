package chatserver.database;

import chatserver.database.PhoneUser;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-15T17:06:44")
@StaticMetamodel(PhoneContacts.class)
public class PhoneContacts_ { 

    public static volatile ListAttribute<PhoneContacts, PhoneUser> phoneContactList;
    public static volatile SingularAttribute<PhoneContacts, Long> id;
    public static volatile SingularAttribute<PhoneContacts, String> userID;

}