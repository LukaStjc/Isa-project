package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.model.Company;

public class DeliveryCompanyDTO {
    private String name;
    private double latitude;
    private double longitude;

    public DeliveryCompanyDTO() {};

    public DeliveryCompanyDTO(Company company) {
        this.name = company.getName();
        this.latitude = company.getLocation().getLatitude();
        this.longitude = company.getLocation().getLongitude();
    }

    public DeliveryCompanyDTO(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
