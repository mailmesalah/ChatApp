package chatserver.database;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-15T17:06:44")
@StaticMetamodel(Register.class)
public class Register_ { 

    public static volatile SingularAttribute<Register, String> password;
    public static volatile SingularAttribute<Register, String> loginID;
    public static volatile SingularAttribute<Register, String> phoneNumber;
    public static volatile SingularAttribute<Register, Long> id;
    public static volatile SingularAttribute<Register, String> userID;
    public static volatile SingularAttribute<Register, String> deviceID;

}