package rs.ac.uns.ftn.informatika.jpa.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.informatika.jpa.model.Company;

import java.util.Date;

@Getter @Setter @NoArgsConstructor
public class CompanyProfileDTO {

    private Integer id;
    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openingTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closingTime;
    private Double averageScore;
    private String country;
    private String city;
    private String streetName;
    private String streetNumber;
    private Double distance;

    public CompanyProfileDTO(Company company){
        this.id = company.getId();
        this.name = company.getName();
        this.description= company.getDescription();
        this.openingTime = company.getOpeningTime();
        this.closingTime = company.getClosingTime();
        this.averageScore = company.getAverageScore();
        this.country = company.getLocation().getCountry();
        this.city = company.getLocation().getCity();
        this.streetName = company.getLocation().getStreet();
        this.streetNumber = company.getLocation().getStreetNumber();
        this.distance = null;
    }
}
