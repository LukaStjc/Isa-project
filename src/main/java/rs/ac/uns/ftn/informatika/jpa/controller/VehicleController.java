package rs.ac.uns.ftn.informatika.jpa.controller;

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
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.HospitalService;
import rs.ac.uns.ftn.informatika.jpa.service.VehicleLocationService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/delivery", produces = MediaType.APPLICATION_JSON_VALUE)
public class VehicleController {

    @Autowired
    private VehicleLocationService vehicleLocationService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private HospitalService hospitalService;

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

    @GetMapping("/companies")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<DeliveryCompanyDTO>> loadAllCompanies() {
        List<Company> companies = companyService.findAll();
        List<DeliveryCompanyDTO> companyDTOs = new ArrayList<>();

        for (Company company : companies) {
            companyDTOs.add(new DeliveryCompanyDTO(company));
        }

        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
    }

    @GetMapping("/hospitals")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<DeliveryHospitalDTO>> loadAllHospitals() {
        List<Hospital> hospitals = hospitalService.findAll();
        List<DeliveryHospitalDTO> hospitalDTOs = new ArrayList<>();

        for (Hospital hospital : hospitals) {
            hospitalDTOs.add(new DeliveryHospitalDTO(hospital));
        }

        return new ResponseEntity<>(hospitalDTOs, HttpStatus.OK);
    }

}
