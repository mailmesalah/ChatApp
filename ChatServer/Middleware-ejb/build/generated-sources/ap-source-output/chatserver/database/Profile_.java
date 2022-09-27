package chatserver.database;

import chatserver.database.Register;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-15T17:06:44")
@StaticMetamodel(Profile.class)
public class Profile_ { 

    public static volatile SingularAttribute<Profile, String> profileName;
    public static volatile SingularAttribute<Profile, String> quote;
    public static volatile SingularAttribute<Profile, Long> id;
    public static volatile SingularAttribute<Profile, byte[]> profileImage;
    public static volatile SingularAttribute<Profile, Register> register;

}