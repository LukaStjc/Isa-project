package rs.ac.uns.ftn.informatika.jpa.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for creating a reservation")
public class ReservationPremadeDTO {

    @Schema(description = "The selected date and time for the reservation in ISO 8601 format", example = "2023-12-01T14:30:00.000Z", required = true)
    private String selectedDateTime;

    @Schema(description = "Duration of the reservation in minutes", example = "60", required = true)
    private String durationMinutes;

    @Schema(description = "ID of the company admin making the reservation", example = "1", required = true)
    private String adminId;
    public ReservationPremadeDTO() {
    }

    public ReservationPremadeDTO(String selectedDateTime, String durationMinutes, String adminId) {
        this.selectedDateTime = selectedDateTime;
        this.durationMinutes = durationMinutes;
        this.adminId = adminId;
    }
    public String getSelectedDateTime() {
        return selectedDateTime;
    }

    public void setSelectedDateTime(String selectedDateTime) {
        this.selectedDateTime = selectedDateTime;
    }

    public void setDurationMinutes(String durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDurationMinutes() {
        return durationMinutes;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}
