package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.model.Equipment;

import static rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType.*;

public class EquipmentOrderingDTO {
    private Integer id;
    private String name;
    private String description;
    private String equipmentType;
    private String companyName;
    private Double averageScore;

    public EquipmentOrderingDTO(){}

    public EquipmentOrderingDTO(Integer id, String name, String description, String equipmentType, String companyName, Double score) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.equipmentType = equipmentType;
        this.companyName = companyName;
        this.averageScore = score;
    }

    public EquipmentOrderingDTO(Equipment equipment){
        this.id = equipment.getId();
        this.name = equipment.getName();
        if (equipment.getType() == type1)
            equipmentType = "Tip 1";
        if (equipment.getType() == type2)
            equipmentType = "Tip 2";
        if (equipment.getType() == type3)
            equipmentType = "Tip 3";
        this.description = equipment.getDescription();
        this.companyName = equipment.getCompany().getName();
        this.averageScore = equipment.getCompany().getAverageScore();
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}
