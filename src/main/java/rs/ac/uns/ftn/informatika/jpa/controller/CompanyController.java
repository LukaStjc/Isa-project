package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyLocationDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.repository.LocationRepository;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.LocationService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/companies")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getCompanies() {

        List<Company> companies = companyService.findAll();

        // convert courses to DTOs
        List<CompanyDTO> companyDTOS = new ArrayList<>();
        for (Company c : companies) {
            companyDTOS.add(new CompanyDTO(c));
        }

        return new ResponseEntity<>(companyDTOS, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyLocationDTO companyLocationDTO){

        Location location = new Location(companyLocationDTO.getCountry(), companyLocationDTO.getCity(), companyLocationDTO.getStreetName(), companyLocationDTO.getStreetNumber());
        location = locationService.save(location);
        if(location == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Company company = new Company(companyLocationDTO.getName(), companyLocationDTO.getDescription(), location);
        company = companyService.save(company);

        return new ResponseEntity<>(new CompanyDTO(company), HttpStatus.CREATED);
    }

}
