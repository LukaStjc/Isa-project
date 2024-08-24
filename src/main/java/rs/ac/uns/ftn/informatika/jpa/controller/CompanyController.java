package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyBasicDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyLocationDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.LocationService;
import rs.ac.uns.ftn.informatika.jpa.service.ReservationService;

import javax.transaction.Transactional;
import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping(value = "api/companies")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<CompanyBasicDTO>> getCompanies() {

        List<Company> companies = companyService.findAll();

        List<CompanyBasicDTO> companyBasicDTOS = new ArrayList<>();
        for (Company c : companies) {
            companyBasicDTOS.add(new CompanyBasicDTO(c));
        }

        return new ResponseEntity<>(companyBasicDTOS, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    @Transactional  // vasilije: posto sam dodao kod company za equipment LAZY, jer u suprotnom ne radi, morao sam ovde da dodam transactional mozda nije najpametnije resenje
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Integer id){
        Company company = companyService.findBy(id);
        CompanyDTO companyDTO = new CompanyDTO(companyService.findBy(id));
        companyDTO.setReservationDTOS(reservationService.getAllPredefinedByCompanyAdmin(company.getCompanyAdmins()));
        return new ResponseEntity<>(companyDTO, HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    @Transactional  // Mora ponovo jer je i ovde isto sto i iznad
    public ResponseEntity<CompanyDTO> getCompanyForUserById(@PathVariable Integer id){
        Company company = companyService.findBy(id);
        CompanyDTO companyDTO = new CompanyDTO(companyService.findBy(id));
        companyDTO.setReservationDTOS(reservationService.getAllPredefinedByCompanyAdmin(company.getCompanyAdmins()));
        companyDTO.setAdmins(new ArrayList<>());
        return new ResponseEntity<>(companyDTO, HttpStatus.OK);
    }

//    @PutMapping ("/update/{id}")
//    @PreAuthorize("hasRole('COMPANY_ADMIN')")
//    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Integer id,   @RequestBody CompanyLocationDTO dto) {
//        Company company = companyService.findBy(id);
//        company.setName(dto.getName());
//        company.setDescription((dto.getDescription()));
//        Location location = company.getLocation();
//        location.setCountry(dto.getCountry());
//        location.setCity(dto.getCity());
//        location.setStreet(dto.getStreetName());
//        location.setStreetNumber(dto.getStreetNumber());
//        locationService.save(location);
//        company.setLocation(location);
//    }
    @PutMapping ("/update/{id}")
    // Vasilije: za sada nisam hteo ovde da stavim transactional, mozda i treba,
    // ali sam opet imao problem sa duzinom sesije, i pukne program zbog lazyCollection-a
    // sa transakcijom produzim trajanje sesije i onda ne baca exception da ne moze
    // da ucita equipment, jer mu vise nije dostupan...
    @Transactional
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    public HttpEntity<? extends Object> updateCompany(@PathVariable Integer id, @RequestBody CompanyLocationDTO dto){

        Company company;

        try {
            company = companyService.updateCompany(id, dto);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(new CompanyDTO(company), HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyLocationDTO companyLocationDTO){

        // TODO dodati proveru da li je korisnik Admin sistema

        Location location = new Location(companyLocationDTO.getCountry(), companyLocationDTO.getCity(), companyLocationDTO.getStreetName(),
                companyLocationDTO.getStreetNumber());
        try{
            location = locationService.save(location);
        }
        catch(RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Company company = new Company(companyLocationDTO.getName(), companyLocationDTO.getDescription(), companyLocationDTO.getOpeningTime(),
                companyLocationDTO.getClosingTime(), location);
        try{
            company = companyService.save(company);
        }
        catch (RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new CompanyDTO(company), HttpStatus.CREATED);
    }


    @GetMapping(value = "/search")
    public ResponseEntity<Collection<CompanyDTO>> searchCompaniesByName(@RequestParam("text") String text) {

        List<Company> foundCompanies = (List<Company>) companyService.findByNameContaining(text);

        List<CompanyDTO> companyDTOS = new ArrayList<>();
        for (Company c : foundCompanies) {
            companyDTOS.add(new CompanyDTO(c));
        }

        return new ResponseEntity<Collection<CompanyDTO>>(companyDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/names")
    public ResponseEntity<String> getCompanyNames(){
        List<Company> companies = companyService.findAll();

        StringBuilder companyNames = new StringBuilder();

        int i = 0;
        companyNames.append(companies.get(i).getName());

        for(i=1; i<companies.size(); ++i){
            companyNames.append(", ");
            companyNames.append(companies.get(i).getName());
        }

        return new ResponseEntity<>(companyNames.toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/has-name")
    public Boolean doesCompanyExistByName(@RequestParam("name") String name){
        Company c = companyService.findExistingByName(name);

        return c != null;
    }




}
