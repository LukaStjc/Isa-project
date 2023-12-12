package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.model.Reservation;

import java.util.Date;

public class ReservationDTO {

    private Integer id;
    private Date vremePocetka;
    private int durationMinutes;
    private String name;
    private String lastName;

    public ReservationDTO(){}

    public ReservationDTO(Integer id, Date vremePocetka, int durationMinutes, String name, String lastName) {
        this.id = id;
        this.vremePocetka = vremePocetka;
        this.durationMinutes = durationMinutes;
        this.name = name;
        this.lastName = lastName;
    }

    public ReservationDTO(Reservation r){
        this.id = r.getId();
        this.vremePocetka = r.getStartingDate();
        this.durationMinutes = r.getDurationMinutes();
        this.name = r.getUser().getFirstName();
        this.lastName = r.getUser().getLastName();
    }

    public Integer getId() {
        return id;
    }

    public Date getVremePocetka() {
        return vremePocetka;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setVremePocetka(Date vremePocetka) {
        this.vremePocetka = vremePocetka;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
