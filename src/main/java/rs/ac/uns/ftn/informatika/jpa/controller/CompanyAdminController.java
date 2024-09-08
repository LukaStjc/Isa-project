package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.*;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyAdminService;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Tag(name = "Company Admin", description = "Handles everything about managing company by it's admins")
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


    @Operation(summary = "Update Company Admin", description = "Updates the details of a company admin with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company admin successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyAdminDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company admin not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access denied for non-admin users")
    })
    @PutMapping ("/update/{id}")
    public ResponseEntity<CompanyAdminDTO> updateCompanyAdmin(
            @Parameter(description = "ID of the company admin to be updated", example = "1", required = true)
            @PathVariable Integer id,

            @Parameter(description = "ID of the company admin to be updated", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyAdminDTO.class)))
            @RequestBody CompanyAdminDTO dto){

        CompanyAdmin companyAdmin = companyAdminService.findBy(id);
        companyAdmin.setFirstName(dto.getFirstName());
        companyAdmin.setLastName(dto.getLastName());
        companyAdmin.setEmail(dto.getEmail());
        companyAdmin.setPassword(dto.getPassword());
        companyAdminService.save(companyAdmin);
        return new ResponseEntity<>(new CompanyAdminDTO(companyAdmin), HttpStatus.OK);
    }

    @Operation(summary = "Find a Company Admin by ID", description = "Returns the details of a specific company admin based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company Admin found"),
            @ApiResponse(responseCode = "404", description = "Company Admin not found")
    })
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

    @Operation(summary = "Get Company ID by Admin ID", description = "Returns the company ID associated with a specific company admin based on the provided admin ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company ID found"),
            @ApiResponse(responseCode = "404", description = "Company Admin not found")
    })
    @GetMapping("/company/{id}")
    public ResponseEntity<Integer> getCompanyIdBy(@PathVariable Integer id) {
        return new ResponseEntity<>(companyAdminService.findCompanyIdBy(id), HttpStatus.OK);
    }


    @Operation(summary = "Get all Company Admins for a Company", description = "Retrieves a list of all company admins associated with a specific company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company Admins found"),
            @ApiResponse(responseCode = "404", description = "No company admins found for the provided company ID")
    })
    @GetMapping("/all/{id}")
    public ResponseEntity<List<CompanyAdminBasicDTO>> getCompanyAdmins(@PathVariable Integer id){
        return new ResponseEntity<>(companyAdminService.getCompanyAdmins(id), HttpStatus.OK);
    }

    @Operation(summary = "Change Company Admin Password", description = "Allows a company admin to change their password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully", content = @Content(schema = @Schema(type = "boolean"))),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data"),
            @ApiResponse(responseCode = "404", description = "Company admin not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Transactional
    @PutMapping("/change-password")
    public ResponseEntity<Boolean> changePassword(@RequestBody ChangePasswordDTO dto){

        return companyAdminService.changePassword(dto);
    }







}
