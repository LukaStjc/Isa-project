package rs.ac.uns.ftn.informatika.jpa.model;

import rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id")
    private Set<ReservationItem> items  = new HashSet<>();

    @ManyToOne
    public RegisteredUser user;

    @ManyToOne
    public CompanyAdmin admin;

    @ManyToOne
    public Hospital hospital;

    public ReservationStatus status;
    public Double totalSum;

    public Reservation() {
    }

    public Reservation(Integer id, Set<ReservationItem> items, RegisteredUser user, CompanyAdmin admin, Hospital hospital, ReservationStatus status, Double totalSum) {
        this.id = id;
        this.items = items;
        this.user = user;
        this.admin = admin;
        this.hospital = hospital;
        this.status = status;
        this.totalSum = totalSum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(Double totalSum) {
        this.totalSum = totalSum;
    }

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public CompanyAdmin getAdmin() {
        return admin;
    }

    public void setAdmin(CompanyAdmin admin) {
        this.admin = admin;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Set<ReservationItem> getItems() {
        return items;
    }

    public void setItems(Set<ReservationItem> items) {
        this.items = items;
    }
}
