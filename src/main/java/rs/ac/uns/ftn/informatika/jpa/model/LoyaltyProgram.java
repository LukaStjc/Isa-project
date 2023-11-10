package rs.ac.uns.ftn.informatika.jpa.model;

import rs.ac.uns.ftn.informatika.jpa.enumeration.LoyaltyType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class LoyaltyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LoyaltyType type;

    @OneToMany(mappedBy="LoyaltyProgram",fetch = FetchType.EAGER)
    private ArrayList<RegisteredUser> users = new ArrayList<RegisteredUser>();
    @Column
    private int minPoints;
    @Column
    private int maxPoints;

    public LoyaltyProgram() {
    }

    public LoyaltyProgram(Integer id, LoyaltyType type, ArrayList<RegisteredUser> users, int minPoints, int maxPoints) {
        this.id = id;
        this.type = type;
        this.users = users;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LoyaltyType getType() {
        return type;
    }

    public void setType(LoyaltyType type) {
        this.type = type;
    }

    public List<RegisteredUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<RegisteredUser> users) {
        this.users = users;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }
}
