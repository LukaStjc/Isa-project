package rs.ac.uns.ftn.informatika.jpa.model;

import rs.ac.uns.ftn.informatika.jpa.enumeration.ContractStatus;
import rs.ac.uns.ftn.informatika.jpa.enumeration.DeliveryStatus;
import rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Hospital hospital;

    @ManyToOne
    private Company company;

//    @ManyToMany
//    private List<Equipment> equipment;
    @OneToOne
    private Equipment equipment;
    private Integer quantity;
    private Date date;
    private ContractStatus status;
    private Integer deliveryDay = 15;

    private DeliveryStatus thisMonthsDeliveryStatus = DeliveryStatus.PENDING;


    public Contract() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Integer getDeliveryDay(){
        return this.deliveryDay;
    }

    public Equipment getEquipment() {
        return equipment;
    }
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public void setDeliveryDay(Integer deliveryDay) {
        // Validate that deliveryDay is between 1 and 28
        if (deliveryDay < 1 || deliveryDay > 28) {
            throw new IllegalArgumentException("Delivery day must be between 1 and 28.");
        }
        this.deliveryDay = deliveryDay;
    }

    public DeliveryStatus getThisMonthsDeliveryStatus() {
        return thisMonthsDeliveryStatus;
    }

    public void setThisMonthsDeliveryStatus(DeliveryStatus thisMonthsDeliveryStatus) {
        this.thisMonthsDeliveryStatus = thisMonthsDeliveryStatus;
    }
}
