package rs.ac.uns.ftn.informatika.jpa.model;

import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="registered_user")
public class RegisteredUser extends User
{
    @Column(unique=false, nullable=true)
    private String telephoneNumber;
    @Column(unique=false)
    private Integer penaltyPoints;

    @ManyToOne
    private LoyaltyProgram loyaltyProgram;
    @Column
    private Integer points;

    @Column
    private String occupation;

    //loyalty nesto


    public RegisteredUser() {
    }

    public RegisteredUser(Integer id, String email, String firstName, String lastName, String password, String occupation) {
        super(email, firstName, lastName, password);
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public Integer getPenaltyPoints() {
        return penaltyPoints;
    }

    public void setPenaltyPoints(Integer penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
