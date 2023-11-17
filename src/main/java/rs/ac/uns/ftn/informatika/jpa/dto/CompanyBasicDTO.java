package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.model.Company;

import java.util.Date;

public class CompanyBasicDTO {

    private Integer id;

    private String name;
    private String description;
    private Date openingTime;
    private Date closingTime;
    private Double averageScore;

    public CompanyBasicDTO(Company company){
        this.id = company.getId();
        this.name = company.getName();
        this.description= company.getDescription();
        this.openingTime = company.getOpeningTime();
        this.closingTime = company.getClosingTime();
        this.averageScore = company.getAverageScore();
    }

    public CompanyBasicDTO() {
    }

    public CompanyBasicDTO(Integer id, String name, String description, Date openingTime, Date closingTime, Double averageScore) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.averageScore = averageScore;
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

    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    public Date getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}
