package rs.ac.uns.ftn.informatika.jpa.dto;

import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;

public class CompanyAdminDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String companyName;
    private String companyId;

    public CompanyAdminDTO(){}

    public CompanyAdminDTO(String email, String firstName, String lastName, String password, String companyName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.companyName = companyName;
    }

    public CompanyAdminDTO(CompanyAdmin ca) {
        this.email = ca.getEmail();
        this.firstName = ca.getFirstName();
        this.lastName = ca.getLastName();
        this.companyName = ca.getCompany().getName();

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String company) {
        this.companyName = company;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
