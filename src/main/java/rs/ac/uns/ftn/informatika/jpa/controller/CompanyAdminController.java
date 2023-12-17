package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyLocationDTO;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyAdminService;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyAdminDTO;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;

import java.util.Optional;

@RestController
@RequestMapping(value = "api/company-admins")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyAdminController {

    @Autowired
    CompanyAdminService companyAdminService;

    @Autowired
    CompanyService companyService;

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<CompanyAdminDTO> createCompanyAdmin(@RequestBody CompanyAdminDTO companyAdminDTO){

        Company c = companyService.findExistingByName(companyAdminDTO.getCompanyName());
        if(c == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CompanyAdmin companyAdmin = new CompanyAdmin(companyAdminDTO.getEmail(), companyAdminDTO.getFirstName(),
                companyAdminDTO.getLastName(), companyAdminDTO.getPassword(), c);

        try{
            companyAdminService.save(companyAdmin);
        }
        catch(RuntimeException e){
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(companyAdminDTO, HttpStatus.CREATED);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<CompanyAdminDTO> updateCompanyAdmin(@PathVariable Integer id, @RequestBody CompanyAdminDTO dto){

        CompanyAdmin companyAdmin = companyAdminService.findBy(id);
        companyAdmin.setFirstName(dto.getFirstName());
        companyAdmin.setLastName(dto.getLastName());
        companyAdmin.setEmail(dto.getEmail());
        companyAdmin.setPassword(dto.getPassword());
        companyAdminService.save(companyAdmin);
        return new ResponseEntity<>(new CompanyAdminDTO(companyAdmin), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CompanyAdminDTO> findById(@PathVariable Integer id){
        CompanyAdmin admin = companyAdminService.findBy(id);
        CompanyAdminDTO adminDTO = new CompanyAdminDTO(admin);
        adminDTO.setCompanyId(admin.getCompany().getId().toString());
        return new ResponseEntity<>(adminDTO, HttpStatus.OK);
    }

    @GetMapping("/exsists/{id}")    //TODO izbrisati, dodato zbog 2. kt
    public ResponseEntity<Boolean> doesExsist(@PathVariable Integer id){
        Optional<CompanyAdmin> optionalCompanyAdmin = companyAdminService.findById(id);

        if(optionalCompanyAdmin.isPresent()) return new ResponseEntity<>(true, HttpStatus.OK);
        else return new ResponseEntity<>(false, HttpStatus.OK);
    }








}
