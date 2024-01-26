package rs.ac.uns.ftn.informatika.jpa.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy="company",fetch = FetchType.LAZY) // greska sa eager kod spring security-ja
    private List<Equipment> equipment;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="company")
    private List<CompanyAdmin> companyAdmins;   //maybe refactor to Admins

    @ManyToOne
    @JoinColumn(name = "created_by_admin")
    private SystemAdmin systemAdmin;

    @Column
    private String description;

    @OneToOne
    @JoinColumn(unique = true)
    private Location location;

    @Column
    private Date openingTime;

    @Column
    private Date closingTime;

    @Column
    private Double averageScore;

    public Company() {
    }

    public Company(Integer id, String name, String description, Date openingTime, Date closingTime, Double averageScore) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.averageScore = averageScore;
    }

    public Company(String name, String description, Date openingTime, Date closingTime, Location location) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.equipment = new ArrayList<>();

        this.averageScore = (double) 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public List<Equipment> getEquipment() {
        return equipment;
    }
    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public List<CompanyAdmin> getCompanyAdmins() {
        return companyAdmins;
    }

    public void setCompanyAdmins(List<CompanyAdmin> companyAdmins) {
        this.companyAdmins = companyAdmins;
    }

    public SystemAdmin getSystemAdmin() {
        return systemAdmin;
    }

    public void setSystemAdmin(SystemAdmin systemAdmin) {
        this.systemAdmin = systemAdmin;
    }
}
