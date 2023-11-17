package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.Location;

import java.util.ArrayList;
import java.util.List;

public class CompanyDTO {
    private Integer id;
    private String name;
    private Location location;
    private Double averageScore;
    private List<EquipmentDTO> equipment;

    public CompanyDTO() {
    }

    public CompanyDTO(Integer id, String name, Location location, Double averageScore, List<EquipmentDTO> equipment) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.averageScore = averageScore;
        this.equipment = equipment;
    }
    public  CompanyDTO(Company company){
        this.id = company.getId();
        this.name = company.getName();
        this.location = company.getLocation();
        this.averageScore = company.getAverageScore();
        this.equipment = EquipmentToDTO(company.getEquipment());
    }
    public List<EquipmentDTO> EquipmentToDTO(List<Equipment> equipment){
        List<EquipmentDTO> equipmentDTOS = new ArrayList<>();
        for (Equipment e: equipment) {
            EquipmentDTO equipmentDTO = new EquipmentDTO(e);

            equipmentDTOS.add(equipmentDTO);
        }
        return equipmentDTOS;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public List<EquipmentDTO> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<EquipmentDTO> equipment) {
        this.equipment = equipment;
    }
}
