package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.enumeration.DeliveryStatus;

import java.util.Date;

public class ContractDTO {

    private Integer id;
    private String companyName;
    private String  hospitalName;
    private EquipmentBasicDTO equipment;
    private Integer quantity;
    private Date time;
    private DeliveryStatus status;

    public ContractDTO() {
    }
    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public EquipmentBasicDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentBasicDTO equipment) {
        this.equipment = equipment;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }
}
