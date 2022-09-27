package chatserver.database;

import chatserver.database.ProfileMap;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-15T17:06:44")
@StaticMetamodel(MapData.class)
public class MapData_ { 

    public static volatile SingularAttribute<MapData, Integer> x;
    public static volatile ListAttribute<MapData, ProfileMap> profiles;
    public static volatile SingularAttribute<MapData, Integer> y;
    public static volatile SingularAttribute<MapData, Long> id;

}