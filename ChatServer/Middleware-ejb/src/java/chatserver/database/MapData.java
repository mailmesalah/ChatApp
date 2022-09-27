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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author Sely
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name="ReadMapDataByRange",
                query="Select MD From MapData MD Where(MD.x>=:xMin And MD.x<=:xMax And MD.y>=:yMin And MD.y<=:yMax)"
        ),
        @NamedQuery(
                name="ReadMapDataByXY",
                query="Select MD From MapData MD Where(MD.x=:x And MD.y=:y)"
        ),
        @NamedQuery(
                name="ReadMapDataByProfile",
                query="Select MD From MapData MD Where(:profile Member Of MD.profiles)"
        ),
        @NamedQuery(
                name="ReadMapDataByXYProfile",
                query="Select MD From MapData MD Where(MD.x=:x And MD.y=:y) And (:profile Member Of MD.profiles)"
        )
        
})
public class MapData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int x;
    private int y;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<ProfileMap> profiles;

    public MapData() {
    }

    public MapData(int x, int y, List<ProfileMap> profiles) {
        this.x = x;
        this.y = y;
        this.profiles = profiles;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<ProfileMap> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileMap> profiles) {
        this.profiles = profiles;
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
        if (!(object instanceof MapData)) {
            return false;
        }
        MapData other = (MapData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chatserver.database.MapData[ id=" + id + " ]";
    }
    
}
