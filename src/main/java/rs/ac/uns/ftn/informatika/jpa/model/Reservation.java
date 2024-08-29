package rs.ac.uns.ftn.informatika.jpa.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationPremadeDTO;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id")
    private Set<ReservationItem> items  = new HashSet<>(); // private, a vecina ostalih polja je public?

    @ManyToOne
    public RegisteredUser user;

    @ManyToOne
    public CompanyAdmin admin;  // preko njega dobavis kompaniju gde radi, 1 admin kompanije radi u najvise 1 kompaniji

    @ManyToOne
    public Hospital hospital;

    public ReservationStatus status;

    public Double totalSum;

    private Date startingDate;

    private int durationMinutes;

    @Version
    private Integer version;

    public Reservation() {
        items = new HashSet<>();
    }

    public Reservation(Integer id, Set<ReservationItem> items, RegisteredUser user, CompanyAdmin admin, Hospital hospital,
                       ReservationStatus status, Double totalSum, Date startingDate, int durationMinutes) {
        this.id = id;
        this.items = items;
        this.user = user;
        this.admin = admin;
        this.hospital = hospital;
        this.status = status;
        this.totalSum = totalSum;
        this.startingDate = startingDate;
        this.durationMinutes = durationMinutes;
    }

    public Reservation(CompanyAdmin admin, ReservationPremadeDTO dto) {
        this.items = new HashSet<>();
        this.user = null;
        this.hospital = null;
        this.status = ReservationStatus.Created;
        this.totalSum = 0.0;
        this.startingDate = new Date(dto.getSelectedDateTime());
        this.admin = admin;
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

    public Date getStartingDate() {
        return startingDate;
    }

    public int getDurationMinutes() {
        return durationMinutes;
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

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
