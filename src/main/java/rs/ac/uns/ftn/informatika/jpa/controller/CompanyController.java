package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.CourseDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Course;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.CourseService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/companies")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

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

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyDTO companyDTO){



        return new ResponseEntity<>(companyDTO, HttpStatus.OK); // TODO ispraviti ako treba jer sam stavio samo da ne bi bio error
    }

}
