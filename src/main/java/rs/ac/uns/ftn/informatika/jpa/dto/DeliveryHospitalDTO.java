package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.model.Hospital;

public class DeliveryHospitalDTO {
    private String name;
    private double latitude;
    private double longitude;

    public DeliveryHospitalDTO() {};

    public DeliveryHospitalDTO(Hospital hospital) {
        this.name = hospital.getName();
        this.latitude = hospital.getLocation().getLatitude();
        this.longitude = hospital.getLocation().getLongitude();
    }

    public DeliveryHospitalDTO(String name, double latitude, double longitude) {
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
