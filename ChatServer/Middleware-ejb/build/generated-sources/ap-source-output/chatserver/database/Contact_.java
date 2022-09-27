package chatserver.database;

import chatserver.database.Profile;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-15T17:06:44")
@StaticMetamodel(Contact.class)
public class Contact_ { 

    public static volatile SingularAttribute<Contact, Profile> contact;
    public static volatile SingularAttribute<Contact, Long> id;
    public static volatile SingularAttribute<Contact, Profile> user;
    public static volatile SingularAttribute<Contact, Integer> status;

}