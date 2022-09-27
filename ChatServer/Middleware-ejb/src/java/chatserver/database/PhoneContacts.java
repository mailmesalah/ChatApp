/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.database;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Sely
 */
@Entity
public class PhoneContacts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String userID;
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<PhoneUser> phoneContactList;

    public PhoneContacts() {
    }

    public PhoneContacts(String userID, List<PhoneUser> phoneContactList) {
        this.userID = userID;
        this.phoneContactList = phoneContactList;        
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<PhoneUser> getPhoneContactList() {
        return phoneContactList;
    }

    public void setPhoneContactList(List<PhoneUser> phoneContactList) {
        this.phoneContactList = phoneContactList;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof PhoneContacts)) {
            return false;
        }
        PhoneContacts other = (PhoneContacts) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chatserver.database.PhoneContacts[ id=" + id + " ]";
    }
    
}
