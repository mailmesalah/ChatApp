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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 *
 * @author Sely
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name="ReadProfiles",
                query="Select P From Profile P"
        ),
        @NamedQuery(
                name="ReadProfileByRegister",
                query="Select P From Profile P Where(P.register=:register)"
        ),
        @NamedQuery(
                name="ReadProfileByUserID",
                query="Select P From Profile P Where(P.register.userID=:userID)"
        ),
        @NamedQuery(
                name="ReadProfileListByProfileName",
                query="Select P From Profile P Where(P.profileName Like :profileName)"
        ),
        @NamedQuery(
                name="ReadProfileListByExcludingList",
                query="Select P From Profile P Where(P Not In :profileIDs)"
        ),
        @NamedQuery(
                name="ReadProfileListByProfileNameAndExcludingList",
                query="Select P From Profile P Where(P.profileName Like :profileName) And (P.register.userID Not In :profileIDs)"
        ),
        @NamedQuery(
                name="ReadProfileListByProfileAndExcludingList",
                query="Select P From Profile P Where(P.register.userID=:userID And P Not In :profileIDs)"
        )
})
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Register register;
    private String profileName;
    @Lob
    private byte[] profileImage;
    private String quote;

    public Profile() {
    }

    public Profile(Register register, String profileName, byte[] profileImage, String quote) {
        this.register = register;        
        this.profileName = profileName;
        this.profileImage = profileImage;        
        this.quote = quote;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
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
        if (!(object instanceof Profile)) {
            return false;
        }
        Profile other = (Profile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chatserver.database.Profile[ id=" + id + " ]";
    }
    
}
