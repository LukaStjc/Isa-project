package rs.ac.uns.ftn.informatika.jpa.dto;

public class ReservationPremadeDTO {
    private String selectedDateTime;
    private String durationMinutes;
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
