package rs.ac.uns.ftn.informatika.jpa.model;

import javax.persistence.*;

@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String country;
    @Column
    private String city;
    @Column
    private String streetName;
    @Column
    private String streetNumber;
    @Column
    private Double longitude; //Sirina

    @Column
    private Double latitude; //Duzina

    public Location() {
    }


    public Location(Integer id, String country, String city, String streetName, String streetNumber, Double longitude, Double latitude) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location(String country, String city, String streetName, String streetNumber) {
        this.country = country;
        this.city = city;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStreet() {
        return streetName;
    }

    public void setStreet(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
