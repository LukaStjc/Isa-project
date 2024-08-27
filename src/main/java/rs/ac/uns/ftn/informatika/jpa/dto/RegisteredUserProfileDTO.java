package rs.ac.uns.ftn.informatika.jpa.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;

@Getter @Setter @NoArgsConstructor
public class RegisteredUserProfileDTO {

    private String firstName;
    private String lastName;
    private String telephoneNumber;
    private String occupation;
    private String country;
    private String city;
    private String streetName;
    private String streetNumber;


    public RegisteredUserProfileDTO(RegisteredUser user){
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.telephoneNumber = user.getTelephoneNumber();
        this.occupation = user.getOccupation();
        this.country = user.getLocation().getCountry();
        this.city = user.getLocation().getCity();
        this.streetName = user.getLocation().getStreet();
        this.streetNumber = user.getLocation().getStreetNumber();
        }


        
    }