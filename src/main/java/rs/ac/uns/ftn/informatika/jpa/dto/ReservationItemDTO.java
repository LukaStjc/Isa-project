package rs.ac.uns.ftn.informatika.jpa.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ReservationItemDTO {
    @NotNull(message = "Equipment ID cannot be null")
    private Integer equipmentId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public ReservationItemDTO() {}

    public ReservationItemDTO(Integer equipmentId, Integer quantity) {
        this.equipmentId = equipmentId;
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }
}
