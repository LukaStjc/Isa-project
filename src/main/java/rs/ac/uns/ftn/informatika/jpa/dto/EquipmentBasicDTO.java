package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.model.Equipment;

public class EquipmentBasicDTO {

    private String name;
    private String description;
    private Integer companyId;
    private String equipmentType;
    private Double price;
    private Integer quantity;
    private Integer version = 0;

    public EquipmentBasicDTO() {
    }

    public EquipmentBasicDTO(String name, String description, Integer companyId, String equipmentType, Double price, Integer quantity) {
        this.name = name;
        this.description = description;
        this.companyId = companyId;
        this.equipmentType = equipmentType;
        this.price = price;
        this.quantity = quantity;
    }
    public EquipmentBasicDTO(Equipment equipment){
        this.name = equipment.getName();
        this.description = equipment.getDescription();
        this.companyId = equipment.getCompany().getId();
        this.equipmentType = equipment.getType().toString();
        this.price = equipment.getPrice();
        this.quantity = equipment.getQuantity();
        this.version = equipment.getVersion();

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
