package rs.ac.uns.ftn.informatika.jpa.model;

import javax.persistence.*;

@Entity
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String comment;

    @ManyToOne
    @JoinColumn(name = "issued_by_user")
    private RegisteredUser registeredUser;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "company_admin_id")
    private CompanyAdmin companyAdmin;

    @Column
    private String reply;

    @ManyToOne
    @JoinColumn(name = "replied_by_admin")
    private SystemAdmin systemAdmin;


    public Complaint(){}

    public Complaint(Integer id, String comment, RegisteredUser registeredUser, Company company,
                     CompanyAdmin companyAdmin, String reply, SystemAdmin systemAdmin) {
        this.id = id;
        this.comment = comment;
        this.registeredUser = registeredUser;
        this.company = company;
        this.companyAdmin = companyAdmin;
        this.reply = reply;
        this.systemAdmin = systemAdmin;
    }

    public String getComment() {
        return comment;
    }

    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    public Company getCompany() {
        return company;
    }

    public CompanyAdmin getCompanyAdmin() {
        return companyAdmin;
    }

    public String getReply() {
        return reply;
    }

    public SystemAdmin getSystemAdmin() {
        return systemAdmin;
    }

    public Integer getId() {
        return id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRegisteredUser(RegisteredUser registeredUser) {
        this.registeredUser = registeredUser;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setCompanyAdmin(CompanyAdmin companyAdmin) {
        this.companyAdmin = companyAdmin;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setSystemAdmin(SystemAdmin systemAdmin) {
        this.systemAdmin = systemAdmin;
    }
}
