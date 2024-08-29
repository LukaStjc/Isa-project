package rs.ac.uns.ftn.informatika.jpa.model;



import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserDTO;

import javax.persistence.*;

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

    @ManyToOne
    private Hospital hospital;
    @Column
    private Integer points;

    @Column
    private String occupation;

    @ManyToOne
    private Location location;

    @Column
    private String activationCode;

    public RegisteredUser() {
        super();
    }

    public RegisteredUser(String email, String firstName, String lastName, String password, String telephoneNumber, String occupation) {
        super(email, firstName, lastName, password, false);
        this.telephoneNumber = telephoneNumber;
        this.penaltyPoints = 0;
        this.points = 0;
        this.occupation = occupation;
        this.activationCode = RandomStringUtils.randomAlphanumeric(32);
    }

    public RegisteredUser(RegisteredUserDTO registeredUserDTO) {
        super(registeredUserDTO.getEmail(), registeredUserDTO.getFirstName(), registeredUserDTO.getLastName(), registeredUserDTO.getPassword(), false);
        this.telephoneNumber = registeredUserDTO.getTelephoneNumber();
        this.penaltyPoints = 0;
        this.points = 0;
        this.occupation = registeredUserDTO.getOccupation();
        this.activationCode = RandomStringUtils.randomAlphanumeric(32);
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

    public LoyaltyProgram getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public void setLoyaltyProgram(LoyaltyProgram loyaltyProgram) {
        this.loyaltyProgram = loyaltyProgram;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

//    public boolean isActive() {
//        return this.active;
//    }

//    public void setActive(boolean active) {
//        this.active = active;
//    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
