package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping
    public ResponseEntity<List<Contract>> getAll(){
        return ResponseEntity.ok(contractService.findAll());
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ContractDTO dto){
        Company company = companyService.findBy(dto.getCompanyId());
        Hospital hospital = hospitalService.findByName(dto.getHospitalName());
        Equipment equipment = equipmentService.findByNameMQ(dto.getEquipment().getName());
        return contractService.create(dto, company, hospital, equipment);
    }
}
