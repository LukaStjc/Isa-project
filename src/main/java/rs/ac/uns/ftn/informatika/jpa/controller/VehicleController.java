package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.*;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Hospital;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.HospitalService;
import rs.ac.uns.ftn.informatika.jpa.service.VehicleLocationService;

import java.util.ArrayList;
import java.util.List;


@Tag(name = "Vehicle Management Controllers", description = "Handles tracking of equipment delivery between companies and hospitals.")
@RestController
@RequestMapping(value = "/delivery", produces = MediaType.APPLICATION_JSON_VALUE)
public class VehicleController {

    @Autowired
    private VehicleLocationService vehicleLocationService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private HospitalService hospitalService;

    @Operation(summary ="Request a route for the following hospital and company.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route request sent successfully!",
                content = { @Content(mediaType = "application/json") } ),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!",
                content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to send route request!",
                content = @Content)
    })
    @PostMapping("/request-route")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<?> requestRoute(@RequestBody RouteRequestDTO routeRequestDTO) {
        try {
            vehicleLocationService.sendRouteRequest(routeRequestDTO);
            return ResponseEntity.ok("Route request sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send route request");
        }
    }

    @Operation(summary = "Load all registered companies.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all companies!",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryCompanyDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!",
                    content = @Content),
            @ApiResponse(responseCode = "204", description = "There is no companies!",
                    content = @Content)
    })
    @GetMapping("/companies")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<DeliveryCompanyDTO>> loadAllCompanies() {
        List<Company> companies = companyService.findAll();
        List<DeliveryCompanyDTO> companyDTOs = new ArrayList<>();

        for (Company company : companies) {
            companyDTOs.add(new DeliveryCompanyDTO(company));
        }

        if (companies.isEmpty()) {
            return new ResponseEntity<>(companyDTOs, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
    }


//    @GetMapping("/companies")
//    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
//    public ResponseEntity<List<DeliveryCompanyDTO>> loadAllCompanies() {
//        List<Company> companies = companyService.findAll();
//        List<DeliveryCompanyDTO> companyDTOs = new ArrayList<>();
//
//        for (Company company : companies) {
//            companyDTOs.add(new DeliveryCompanyDTO(company));
//        }
//
//        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
//    }

    @Operation(summary = "Load all registered hospitals.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all hospitals!",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryHospitalDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!",
                    content = @Content),
            @ApiResponse(responseCode = "204", description = "There are no hospitals available!",
                    content = @Content)
    })
    @GetMapping("/hospitals")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<DeliveryHospitalDTO>> loadAllHospitals() {
        List<Hospital> hospitals = hospitalService.findAll();
        List<DeliveryHospitalDTO> hospitalDTOs = new ArrayList<>();

        for (Hospital hospital : hospitals) {
            hospitalDTOs.add(new DeliveryHospitalDTO(hospital));
        }

        if (hospitals.isEmpty()) {
            return new ResponseEntity<>(hospitalDTOs, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(hospitalDTOs, HttpStatus.OK);
    }
//    @GetMapping("/hospitals")
//    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
//    public ResponseEntity<List<DeliveryHospitalDTO>> loadAllHospitals() {
//        List<Hospital> hospitals = hospitalService.findAll();
//        List<DeliveryHospitalDTO> hospitalDTOs = new ArrayList<>();
//
//        for (Hospital hospital : hospitals) {
//            hospitalDTOs.add(new DeliveryHospitalDTO(hospital));
//        }
//
//        return new ResponseEntity<>(hospitalDTOs, HttpStatus.OK);
//    }

}
