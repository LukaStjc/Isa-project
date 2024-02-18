package rs.ac.uns.ftn.informatika.jpa.dto;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ReservationByPremadeAppointmentDTO {

    private Integer reservationId;

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
