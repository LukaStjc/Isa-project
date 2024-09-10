package rs.ac.uns.ftn.informatika.jpa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;


@Schema(description = "Data Transfer Object for Company Admin information.")
public class CompanyAdminDTO {

    @Schema(description = "Email address of the company admin", example = "admin@example.com", required = true)
    private String email;

    @Schema(description = "First name of the company admin", example = "John", required = true)
    private String firstName;

    @Schema(description = "Last name of the company admin", example = "Doe", required = true)
    private String lastName;

    @Schema(description = "Password for the company admin's account", example = "password123", required = true)
    private String password;

    @Schema(description = "Name of the company the admin is associated with", example = "TechCorp")
    private String companyName;

    @Schema(description = "ID of the company the admin is associated with", example = "123")
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
