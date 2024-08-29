package rs.ac.uns.ftn.informatika.jpa.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReservationItemProfileDTO {

    private String equipmentName;

    private Integer quantity;


}
