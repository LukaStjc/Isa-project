package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import rs.ac.uns.ftn.informatika.jpa.dto.*;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.LocationService;
import rs.ac.uns.ftn.informatika.jpa.service.RegisteredUserService;
import rs.ac.uns.ftn.informatika.jpa.service.ReservationService;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Tag(name = "Company", description = "Endpoints for managing company details")
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
    @Autowired
    private RegisteredUserService registeredUserService;

    @GetMapping
    public ResponseEntity<List<CompanyBasicDTO>> getCompanies() {

        List<Company> companies = companyService.findAll();

        List<CompanyBasicDTO> companyBasicDTOS = new ArrayList<>();
        for (Company c : companies) {
            companyBasicDTOS.add(new CompanyBasicDTO(c));
        }

        return new ResponseEntity<>(companyBasicDTOS, HttpStatus.OK);
    }
    @Operation(summary = "Get Company Details by ID", description = "Retrieve the details of a company, including reservations and admins, for the specified company ID. Requires COMPANY_ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company details retrieved successfully", content = @Content(schema = @Schema(implementation = CompanyDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have COMPANY_ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    @Transactional  // vasilije: posto sam dodao kod company za equipment LAZY, jer u suprotnom ne radi, morao sam ovde da dodam transactional mozda nije najpametnije resenje
    //@PreAuthorize("hasRole('COMPANY_ADMIN')")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Integer id){
        Company company = companyService.findBy(id);
        Hibernate.initialize(company.getEquipment());
        CompanyDTO companyDTO = new CompanyDTO(company);
        companyDTO.setReservationDTOS(reservationService.getAllPredefinedByCompanyAdmin(company.getCompanyAdmins()));
        return new ResponseEntity<>(companyDTO, HttpStatus.OK);
    }

    @Operation(summary = "Get Company Profile by ID", description = "Retrieve company details including reservations and admins for the specified company ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company details retrieved successfully", content = @Content(schema = @Schema(implementation = CompanyDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/profile/{id}")
    @Transactional  // Mora ponovo jer je i ovde isto sto i iznad
    public ResponseEntity<CompanyDTO> getCompanyForUserById(
            @Parameter(description = "ID of company for request", required = true)
            @PathVariable Integer id){
        Company company = companyService.findBy(id);
        CompanyDTO companyDTO = new CompanyDTO(company);
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
    @Operation(
            summary = "Update company details",
            description = "Updates the specified company's location or other details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated successfully", content = @Content(schema = @Schema(implementation = CompanyDTO.class))),
            @ApiResponse(responseCode = "204", description = "No content - company not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to update the company"),
            @ApiResponse(responseCode = "500", description = "Server error while updating company")
    })    @PutMapping ("/update/{id}")
    // Vasilije: za sada nisam hteo ovde da stavim transactional, mozda i treba,
    // ali sam opet imao problem sa duzinom sesije, i pukne program zbog lazyCollection-a
    // sa transakcijom produzim trajanje sesije i onda ne baca exception da ne moze
    // da ucita equipment, jer mu vise nije dostupan...
    @Transactional
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    public HttpEntity<? extends Object> updateCompany(@PathVariable Integer id, @RequestBody CompanyLocationDTO dto) {
        // Validation
//        System.out.println(dto.getStreetNumber());
        if (Integer.parseInt(dto.getStreetNumber())  < 0) {
            return new ResponseEntity<String>("Street number must be a positive integer.", HttpStatus.BAD_REQUEST);
        }

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


    @Operation(summary = "Retrieves all companies that satisfy requested criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the company list!",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompanyProfileDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!",
                    content = @Content)
    })
    @GetMapping("/searchByNameOrLocation")
    public ResponseEntity<List<CompanyProfileDTO>> searchCompanies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        RegisteredUser registeredUser = null;

        if(authentication!=null && authentication.isAuthenticated()){
            registeredUser = registeredUserService.findByEmail(authentication.getName());
        }


        List<CompanyProfileDTO> companies = companyService.searchAndFilter(registeredUser, name, location, minScore, maxDistance, sortBy, sortDirection);

        return new ResponseEntity<>(companies, HttpStatus.OK);

    }




}
