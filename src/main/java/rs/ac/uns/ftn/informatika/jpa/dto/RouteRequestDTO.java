package rs.ac.uns.ftn.informatika.jpa.dto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.DecimalMax;

public class RouteRequestDTO {
    @DecimalMin(value = "-90.0", message = "Start latitude must be greater than or equal to -90.0")
    @DecimalMax(value = "90.0", message = "Start latitude must be less than or equal to 90.0")
    private double startLatitude;

    @DecimalMin(value = "-180.0", message = "Start longitude must be greater than or equal to -180.0")
    @DecimalMax(value = "180.0", message = "Start longitude must be less than or equal to 180.0")
    private double startLongitude;

    @DecimalMin(value = "-90.0", message = "End latitude must be greater than or equal to -90.0")
    @DecimalMax(value = "90.0", message = "End latitude must be less than or equal to 90.0")
    private double endLatitude;

    @DecimalMin(value = "-180.0", message = "End longitude must be greater than or equal to -180.0")
    @DecimalMax(value = "180.0", message = "End longitude must be less than or equal to 180.0")
    private double endLongitude;

    // Constructor, getters, and setters
    // Nije htelo da deserijalizuje prilikom obrade rest zahteva bez defaultnog konstruktora iako ga ne koristi
    public RouteRequestDTO() {};

    public RouteRequestDTO(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }
}
