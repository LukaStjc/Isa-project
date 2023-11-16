package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import static rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType.*;

public class EquipmentDTO {

    private Integer id;

    private String name;

    private String description;

    private String equipmentType;

    private Double price;

    private Integer quantity;

    private String companyName;

    private Integer companyId;

    public EquipmentDTO() {
    }

    public EquipmentDTO(Equipment e) {
        this.id = e.getId();
        this.name = e.getName();
        this.description = e.getDescription();
        if (e.getType() == type1)
            equipmentType = "Tip 1";
        if (e.getType() == type2)
            equipmentType = "Tip 2";
        if (e.getType() == type3)
            equipmentType = "Tip 3";
        this.price = e.getPrice();
        this.quantity = e.getQuantity();
        this.companyName = e.getCompany().getName();
        this.companyId = e.getCompany().getId();
    }

    public EquipmentDTO(Integer id, String name, String description, String equipmentType, Double price, Integer quantity, String companyName, Integer companyId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.equipmentType = equipmentType;
        this.price = price;
        this.quantity = quantity;
        this.companyName = companyName;
        this.companyId = companyId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}
