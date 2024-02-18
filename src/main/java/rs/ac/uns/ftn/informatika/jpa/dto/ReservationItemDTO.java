package rs.ac.uns.ftn.informatika.jpa.dto;

public class ReservationItemDTO {
    private Integer equipmentId;
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
