/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.database;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Sely
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name="ReadDualChat",
                query="Select DC From DualChat DC Where(DC.sender=:sender And DC.deletedBySender<>TRUE) Or (DC.receiver=:receiver And DC.deletedByReceiver<>TRUE) Order By DC.sendTime"
        ),
        @NamedQuery(
                name="ReadDualChatUnread",
                query="Select DC From DualChat DC Where(DC.receiver=:receiver And DC.seenBy=:seenBy) Order By DC.sendTime"
        ),
        @NamedQuery(
                name="ReadDualChatByIDAndSenderOrReceiver",
                query="Select DC From DualChat DC Where(DC.sender=:sender Or DC.receiver=:receiver) And (DC.id=:ID)"
        ),
        @NamedQuery(
                name="ReadDualChatByIDAndSender",
                query="Select DC From DualChat DC Where(DC.sender=:sender) And (DC.id=:ID)"
        ),
        @NamedQuery(
                name="ReadDualChatByIDAndReceiver",
                query="Select DC From DualChat DC Where(DC.receiver=:receiver) And (DC.id=:ID)"
        ),
        @NamedQuery(
                name="ReadDualChatBySenderOrReceiverAndMessageText",
                query="Select DC From DualChat DC Where(DC.sender=:sender Or DC.receiver=:receiver) And (DC.messageText Like :messageText)"
        )
})
public class DualChat implements Serializable {
    //Seen by
    public final static int SENDER=0;
    public final static int SERVER=1;
    public final static int RECEIVER=2;
    
    //Message Type
    public final static int TEXT=0;
    public final static int IMAGE_TEXT=1;
    public final static int IMAGES_TEXT=2;
    public final static int CAMERA_IMAGE_TEXT=3;
    public final static int AUDIO_TEXT=4;
    public final static int AUDIOS_TEXT=5;
    public final static int VOICE_TEXT=6;   
    public final static int VIDEO_TEXT=7;
    public final static int VIDIEOS_TEXT=8;
    public final static int CAMERA_VIDEO_TEXT=9;
    public final static int VOICE_CALL_SUMMERY_TEXT=10;
    public final static int VIDEO_CALL_SUMMARY_TEXT=11;
    public final static int FILE_TEXT=12;    
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    
    @ManyToOne
    private Profile sender;
    @ManyToOne
    private Profile receiver;
    private String messageText;
    private String filePath;
    private int messageType;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar sendTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar receiveTime;
    private int seenBy;
    private boolean deletedBySender;
    private boolean deletedByReceiver;

    public DualChat() {
    }

    public DualChat(Profile sender, Profile receiver, String messageText, String filePath, int messageType, Calendar sendTime, int seenBy) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
        this.filePath = filePath;
        this.messageType = messageType;
        this.sendTime = sendTime;
        this.seenBy = seenBy;
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

    public void setReceiver(Profile receiver) {
        this.receiver = receiver;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public Calendar getSendTime() {
        return sendTime;
    }

    public void setSendTime(Calendar sendTime) {
        this.sendTime = sendTime;
    }

    public Calendar getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Calendar receiveTime) {
        this.receiveTime = receiveTime;
    }

    public int getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(int seenBy) {
        this.seenBy = seenBy;
    }

    public boolean isDeletedBySender() {
        return deletedBySender;
    }

    public void setDeletedBySender(boolean deletedBySender) {
        this.deletedBySender = deletedBySender;
    }

    public boolean isDeletedByReceiver() {
        return deletedByReceiver;
    }

    public void setDeletedByReceiver(boolean deletedByReceiver) {
        this.deletedByReceiver = deletedByReceiver;
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
        if (!(object instanceof DualChat)) {
            return false;
        }
        DualChat other = (DualChat) object;
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
