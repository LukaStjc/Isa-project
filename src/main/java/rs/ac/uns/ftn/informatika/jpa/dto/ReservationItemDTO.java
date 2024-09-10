package rs.ac.uns.ftn.informatika.jpa.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@Schema(description = "Data Transfer Object for reservation items")
public class ReservationItemDTO {

    @Schema(description = "ID of the equipment being reserved", example = "456")
    @NotNull(message = "Equipment ID cannot be null")
    private Integer equipmentId;

    @Schema(description = "Quantity of the equipment being reserved", example = "2")
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
