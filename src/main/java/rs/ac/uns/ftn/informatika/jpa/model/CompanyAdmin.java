package rs.ac.uns.ftn.informatika.jpa.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CompanyAdmin extends User{

    @ManyToOne
    private Company company;

    @ManyToOne
    @JoinColumn(name = "registered_by_admin")
    private SystemAdmin systemAdmin;

    //polje sa listom slobnodnih termina
    public CompanyAdmin() {
    }

    public CompanyAdmin(Company company) {
        this.company = company;
    }

    public CompanyAdmin(String email, String firstName, String lastName, String password, Company company) {
        super(email, firstName, lastName, password, true); // po default-u je nalog company admin-a enabled, jer se ne registruje
        this.company = company;
    }

    public CompanyAdmin(String email, String firstName, String lastName, String password, Company company, SystemAdmin systemAdmin) {
        super(email, firstName, lastName, password, true); // po default-u je nalog company admin-a enabled, jer se ne registruje
        this.company = company;
        this.systemAdmin = systemAdmin;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
