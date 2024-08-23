package rs.ac.uns.ftn.informatika.jpa.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;

@Getter @Setter @NoArgsConstructor
public class RegisteredUserProfileDTO {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String telephoneNumber;
    private Integer penaltyPoints;
    private String loyaltyProgramName;
    private int loyaltyProgramMinPoints;
    private int loyaltyProgramMaxPoints;
    private String hospitalName;
    private String occupation;
    private String country;
    private String city;
    private String streetName;
    private String streetNumber;


    public RegisteredUserProfileDTO(RegisteredUser user){
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.telephoneNumber = user.getTelephoneNumber();
        this.penaltyPoints = user.getPenaltyPoints();
        this.loyaltyProgramName = user.getLoyaltyProgram().getType().name();
        this.loyaltyProgramMinPoints = user.getLoyaltyProgram().getMinPoints();
        this.loyaltyProgramMaxPoints = user.getLoyaltyProgram().getMaxPoints();
        this.hospitalName = user.getHospital().getName();
        this.occupation = user.getOccupation();
        this.country = user.getLocation().getCountry();
        this.city = user.getLocation().getCity();
        this.streetName = user.getLocation().getStreet();
        this.streetNumber = user.getLocation().getStreetNumber();
    }







}
