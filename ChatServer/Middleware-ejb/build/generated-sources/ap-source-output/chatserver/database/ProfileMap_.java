package chatserver.database;

import chatserver.database.Profile;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-15T17:06:44")
@StaticMetamodel(ProfileMap.class)
public class ProfileMap_ { 

    public static volatile SingularAttribute<ProfileMap, Float> latitude;
    public static volatile SingularAttribute<ProfileMap, Profile> profile;
    public static volatile SingularAttribute<ProfileMap, Long> id;
    public static volatile SingularAttribute<ProfileMap, Float> longitude;

}