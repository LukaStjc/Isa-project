package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.ContractDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.Hospital;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.ContractService;
import rs.ac.uns.ftn.informatika.jpa.service.EquipmentService;
import rs.ac.uns.ftn.informatika.jpa.service.HospitalService;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Tag(name = "Contract", description = "Endpoints for managing contract between companies and hospitals")
@RestController
@RequestMapping(value = "api/contracts")
@CrossOrigin(origins = "http://localhost:3000")
public class ContractController {

    @Autowired
    private ContractService contractService;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private EquipmentService equipmentService;


    @Operation(
            summary = "Retrieve all contracts",
            description = "Fetches a list of all contracts available in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of contracts"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping
    public ResponseEntity<List<Contract>> getAll(){
        return ResponseEntity.ok(contractService.findAll());
    }

    @Operation(
            summary = "Create a new contract",
            description = "Creates a new contract with the specified details. The contract is associated with a company, hospital, and equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the contract"),
            @ApiResponse(responseCode = "404", description = "Company, hospital, or equipment not found",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ContractDTO dto){
        Company company = companyService.findBy(dto.getCompanyName());
        Hospital hospital = hospitalService.findByName(dto.getHospitalName());
        Equipment equipment = equipmentService.findByNameMQ(dto.getEquipment().getName());

        if (company == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Company with name '%s' not found.", dto.getCompanyName()));
        }
        if (hospital == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Hospital with name '%s' not found.", dto.getHospitalName()));
        }

        if (equipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Equipment with name '%s' not found.", dto.getEquipment().getName()));
        }

        return contractService.create(dto, company, hospital, equipment);
    }

    @Operation(
            summary = "Cancel a specific contract",
            description = "Cancels the contract identified by the specified ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully canceled the contract"),
            @ApiResponse(responseCode = "404", description = "Contract not found"),
            @ApiResponse(responseCode = "400", description = "Bad request or invalid contract state"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?>  cancelContract(@PathVariable Integer id){
        return contractService.cancelContract(id);
    }

    public List<Contract> findByDayOfMonthAndTime(int day, int hours, int minutes) {
        return contractService.findByDayOfMonthAndTime(day, hours, minutes);
    }

    @Operation(
            summary = "Cancel this month's delivery for a contract",
            description = "Cancels the delivery scheduled for the current month for the specified contract."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully canceled this month's delivery"),
            @ApiResponse(responseCode = "404", description = "Contract not found"),
            @ApiResponse(responseCode = "400", description = "Bad request or invalid contract state"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/cancel-delivery")
    public ResponseEntity<?> cancelThisMonthsDelivery(@PathVariable Integer id){
        return contractService.cancelThisMonthsDelivery(id);
    }

//    @GetMapping("/getnow")
    public List<Contract> findActiveByDayAndTime(Date date) {
        return contractService.findActiveByDayAndTime(date);
    }

    public void markDeliveryAsSent(Contract contract) {
        contractService.markDeliveryAsSent(contract);
    }

    public void save(Contract contract) {
        contractService.save(contract);
    }

    @Operation(
            summary = "Get all active contracts for a company",
            description = "Fetches all active contracts associated with the specified company ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of active contracts"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/active")
    ResponseEntity<List<ContractDTO>> findAllActiveContractsByCompany(@PathVariable Integer id){
        return new ResponseEntity<>(contractService.findAllActiveContractsByCompany(id), HttpStatus.OK);
    }

    public List<Contract> checkContractsAtCurrentTime() {
        return contractService.checkContractsAtCurrentTime();
    }
}
