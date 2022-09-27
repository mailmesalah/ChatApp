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
                name="ReadContactByProfile",
                query="Select C From Contact C Where(C.user=:user)"
        ),
        @NamedQuery(
                name="ReadContactByProfileAndStatus",
                query="Select C From Contact C Where(C.user=:user And C.status=:status)"
        ),
        @NamedQuery(
                name="ReadContactByProfileAndContact",
                query="Select C From Contact C Where(C.user=:user And C.contact=:contact)"
        ),
        @NamedQuery(
                name="ReadContactByProfileAndContactName",
                query="Select C From Contact C Where(C.user=:user And C.contact.profileName Like :contactName) Or (C.contact=:user And C.user.profileName Like :contactName)"
        ),
        @NamedQuery(
                name="ReadContactByContact",
                query="Select C From Contact C Where(C.contact=:contact)"
        ),
        @NamedQuery(
                name="ReadContactByContactAndStatus",
                query="Select C From Contact C Where(C.contact=:contact And C.status=:status)"
        ),
        @NamedQuery(
                name="ReadContactByProfileOrContact",
                query="Select C From Contact C Where(C.user=:user Or C.contact=:contact)"
        ),
        @NamedQuery(
                name="ReadContactByProfileOrContactAndStatus",
                query="Select C From Contact C Where(C.user=:user Or C.contact=:contact) And (C.status=:status)"
        ),
        @NamedQuery(
                name="ReadAllContactByProfileOrContact",
                query="Select C From Contact C Where(C.user=:user Or C.contact=:contact) And (C.status>=:status)"
        )
})
public class Contact implements Serializable {
    public final static int SENT_BY_CLIENT=0;
    public final static int RECEIVED_BY_SERVER=1;
    public final static int RECEIVED_BY_CLIENT=2;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    
    @ManyToOne
    private Profile user;
    @ManyToOne
    private Profile contact;
    private int status;

    public Contact() {
    }

    public Contact(Profile user, Profile contact, int status) {
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
        if (!(object instanceof Contact)) {
            return false;
        }
        Contact other = (Contact) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chatserver.database.Contact[ id=" + id + " ]";
    }
    
}
