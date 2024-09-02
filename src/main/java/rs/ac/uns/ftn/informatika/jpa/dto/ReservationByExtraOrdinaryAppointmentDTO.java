package rs.ac.uns.ftn.informatika.jpa.dto;


import lombok.*;

import java.util.Date;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReservationByExtraOrdinaryAppointmentDTO {

    private String selectedDateTime;

    private List<ReservationItemDTO> reservationItems;

    private Integer availableAdminId;



}
