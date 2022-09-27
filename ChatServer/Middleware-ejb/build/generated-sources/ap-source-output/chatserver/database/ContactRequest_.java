package chatserver.database;

import chatserver.database.Profile;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-15T17:06:44")
@StaticMetamodel(ContactRequest.class)
public class ContactRequest_ { 

    public static volatile SingularAttribute<ContactRequest, Profile> receiver;
    public static volatile SingularAttribute<ContactRequest, Profile> sender;
    public static volatile SingularAttribute<ContactRequest, Long> id;
    public static volatile SingularAttribute<ContactRequest, Integer> status;

}