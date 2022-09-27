/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.database;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Sely
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name="ReadRegisterByUserIDLoginIDDeviceID",
                query="Select R From Register R Where(R.userID=:userID AND R.loginID=:loginID AND R.deviceID=:deviceID)"
        ),
        @NamedQuery(
                name="IsUserIDPhoneNumberUsed",
                query="Select R From Register R Where(R.userID=:userID Or R.phoneNumber=:phoneNumber)"
        ),
        @NamedQuery(
                name="ReadRegisterByPhoneNumberAndPassword",
                query="Select R From Register R Where(R.phoneNumber=:phoneNumber And R.password=:password)"
        ),
        @NamedQuery(
                name="ReadRegisterByUserIDAndPassword",
                query="Select R From Register R Where(R.userID=:userID And R.password=:password)"
        )
})
public class Register implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userID;
    private String loginID;
    private String deviceID;
    private String phoneNumber;
    private String password;

    public Register() {
    }

    public Register(String userID, String loginID, String deviceID, String phoneNumber, String password) {
        this.userID = userID;
        this.loginID = loginID;
        this.deviceID = deviceID;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Register)) {
            return false;
        }
        Register other = (Register) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chatserver.database.Register[ id=" + id + " ]";
    }
    
}
