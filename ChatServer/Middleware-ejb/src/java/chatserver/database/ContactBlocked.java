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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Sely
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name="ReadContactBlockedByProfile",
                query="Select B From ContactBlocked B Where(B.user=:user)"
        ),
        @NamedQuery(
                name="ReadContactBlockedByProfileAndStatus",
                query="Select B From ContactBlocked B Where(B.user=:user And B.status=:status)"
        ),
        @NamedQuery(
                name="ReadContactBlockedByProfileAndContact",
                query="Select B From ContactBlocked B Where(B.user=:user And B.contact=:contact)"
        ),
        @NamedQuery(
                name="ReadContactBlockedByProfileAndContactName",
                query="Select C From Contact C Where(C.user=:user And C.contact.profileName Like :contactName)"
        ),@NamedQuery(
                name="ReadContactBlockedByContact",
                query="Select B From ContactBlocked B Where(B.contact=:contact)"
        )
})
public class ContactBlocked implements Serializable {
    public final static int SENT_BY_CLIENT=0;
    public final static int RECEIVED_BY_SERVER=1;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    private Profile user;
    @ManyToOne
    private Profile contact;
    private int status;

    public ContactBlocked() {
    }

    public ContactBlocked(Profile user, Profile contact, int status) {
        this.user = user;
        this.contact = contact;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }

    public Profile getContact() {
        return contact;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
        if (!(object instanceof ContactBlocked)) {
            return false;
        }
        ContactBlocked other = (ContactBlocked) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chatserver.database.ContactBlocked[ id=" + id + " ]";
    }
    
}
