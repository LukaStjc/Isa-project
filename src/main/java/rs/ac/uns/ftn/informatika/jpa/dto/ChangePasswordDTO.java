package rs.ac.uns.ftn.informatika.jpa.dto;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChangePasswordDTO {


    private String oldPassword;
    private String newPassword;


}
