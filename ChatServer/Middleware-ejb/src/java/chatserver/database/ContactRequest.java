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
                name="ReadContactRequestBySender",
                query="Select RL From ContactRequest RL Where(RL.sender=:sender)"
        ),
        @NamedQuery(
                name="ReadContactRequestBySenderAndStatus",
                query="Select RL From ContactRequest RL Where(RL.sender=:sender And RL.status=:status)"
        ),
        @NamedQuery(
                name="ReadContactRequestBySenderAndReceiver",
                query="Select RL From ContactRequest RL Where(RL.sender=:sender And RL.receiver=:receiver)"
        ),
        @NamedQuery(
                name="ReadContactRequestByReceiver",
                query="Select RL From ContactRequest RL Where(RL.receiver=:receiver)"
        ),
        @NamedQuery(
                name="ReadContactRequestByReceiverAndStatus",
                query="Select RL From ContactRequest RL Where(RL.receiver=:receiver And RL.status=:status)"
        ),
        @NamedQuery(
                name="ReadContactRequestBySenderAndReceiverName",
                query="Select C From ContactRequest C Where(C.sender=:sender And C.receiver.profileName Like :receiverName)"
        ),
        @NamedQuery(
                name="ReadContactRequestByReceiverAndSenderName",
                query="Select C From ContactRequest C Where(C.receiver=:receiver And C.sender.profileName Like :receiverName)"
        ),
        @NamedQuery(
                name="ReadAllContactRequestBySenderAndReceiver",
                query="Select RL From ContactRequest RL Where(RL.sender=:sender And RL.receiver=:receiver) And (RL.status>=:status)"
        ),
        @NamedQuery(
                name="ReadAllContactRequestByReceiver",
                query="Select RL From ContactRequest RL Where(RL.receiver=:receiver And RL.status>=:status)"
        ),
        @NamedQuery(
                name="ReadAllContactRequestBySender",
                query="Select RL From ContactRequest RL Where(RL.sender=:sender And RL.status>=:status)"
        )
        
})
public class ContactRequest implements Serializable {

    public final static int SENT_BY_CLIENT=0;
    public final static int RECEIVED_BY_SERVER=1;
    public final static int RECEIVED_BY_CLIENT=2;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    private Profile sender;
    @ManyToOne
    private Profile receiver;
    private int status;

    public ContactRequest() {
    }

    public ContactRequest(Profile sender, Profile receiver, int status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }  

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getSender() {
        return sender;
    }

    public void setSender(Profile sender) {
        this.sender = sender;
    }

    public Profile getReceiver() {
        return receiver;
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
        if (!(object instanceof ContactRequest)) {
            return false;
        }
        ContactRequest other = (ContactRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chatserver.database.ContactRequest[ id=" + id + " ]";
    }
    
}
