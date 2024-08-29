package rs.ac.uns.ftn.informatika.jpa.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChangePasswordProfileDTO {


        private String oldPassword;
        private String newPassword;



}
