package rs.ac.uns.ftn.informatika.jpa.dto;

import java.util.Date;

public class CompanyLocationDTO {
    private String name;
    private String description;
    private String country;
    private String city;
    private String streetName;
    private String streetNumber;
    private Date openingTime;
    private Date closingTime;

    public CompanyLocationDTO(){}

    public CompanyLocationDTO(String country, String city, String streetName, String streetNumber, String name, String description, Date openingTime, Date closingTime) {
        this.country = country;
        this.city = city;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.name = name;
        this.description = description;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
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
}
