package rs.ac.uns.ftn.informatika.jpa.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;

@Getter @Setter @NoArgsConstructor
public class RegisteredUserProfileDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String telephoneNumber;
    private int penaltyPoints;
    private String loyaltyProgramName;
    private int loyaltyProgramMinPoints;
    private int loyaltyProgramMaxPoints;

    public RegisteredUserProfileDTO(RegisteredUser user){
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.telephoneNumber = user.getTelephoneNumber();
        this.penaltyPoints = user.getPenaltyPoints();
        this.loyaltyProgramName = user.getLoyaltyProgram().getType().name();
        this.loyaltyProgramMinPoints = user.getLoyaltyProgram().getMinPoints();
        this.loyaltyProgramMaxPoints = user.getLoyaltyProgram().getMaxPoints();
    }







}
