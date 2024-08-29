package rs.ac.uns.ftn.informatika.jpa.dto;

import org.apache.commons.lang3.tuple.Pair;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class ReservationByPremadeAppointmentDTO {

    @NotNull(message = "Reservation ID cannot be null!")
    private Integer reservationId;

    @NotNull(message = "Reservation items cannot be null!")
    @Size(min = 1, message = "At least one reservation item is required!")
    @Valid
    private List<ReservationItemDTO> reservationItems;

    public ReservationByPremadeAppointmentDTO() {
    }

    public ReservationByPremadeAppointmentDTO(Integer reservationId, List<ReservationItemDTO> reservationItems) {
        this.reservationId = reservationId;
        this.reservationItems = reservationItems;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public List<ReservationItemDTO> getReservationItems() {
        return reservationItems;
    }

    public void setReservationItems(List<ReservationItemDTO> reservationItems) {
        this.reservationItems = reservationItems;
    }
}
