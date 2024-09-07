package rs.ac.uns.ftn.informatika.jpa.dto;


import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReservationByExtraOrdinaryAppointmentDTO {

    private String selectedDateTime;

    @NotNull(message = "Reservation items cannot be null!")
    @Size(min = 1, message = "At least one reservation item is required!")
    @Valid
    private List<ReservationItemDTO> reservationItems;

    private Integer availableAdminId;



}
