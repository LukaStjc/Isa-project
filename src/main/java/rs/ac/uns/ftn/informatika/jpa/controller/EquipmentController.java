package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.CourseDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.EquipmentDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Course;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.EquipmentService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("api/equipment")
@CrossOrigin(origins = "http://localhost:3000")
public class EquipmentController {

    @Autowired
    CompanyService companyService;

    @Autowired
    EquipmentService equipmentService;

    @GetMapping("/company/{id}")
    public ResponseEntity<List<EquipmentDTO>> getCompanyEquipment(@PathVariable Integer id) {

        Company company =  companyService.findOne(id);

        // company must exist
        if (company == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Equipment> equipment = company.getEquipment();

        List<EquipmentDTO> equipmentDTOS = new ArrayList<>();
        for (Equipment e : equipment) {
            equipmentDTOS.add(new EquipmentDTO(e));
        }

        return new ResponseEntity<>(equipmentDTOS, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getEquipment() {

        List<Equipment> equipment = equipmentService.findAll();

        // company must exist
        if (equipment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<EquipmentDTO> equipmentDTOS = new ArrayList<>();
        for (Equipment e : equipment) {
            equipmentDTOS.add(new EquipmentDTO(e));
        }

        return new ResponseEntity<>(equipmentDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Collection<EquipmentDTO>> searchEquipmentByName(@RequestParam("text") String text) {
        List<Equipment> foundEquipment = (List<Equipment>) equipmentService.findByName(text);

        List<EquipmentDTO> equipmentDTOS = new ArrayList<>();
        for (Equipment e : foundEquipment) {
            equipmentDTOS.add(new EquipmentDTO(e));
        }

        return new ResponseEntity<Collection<EquipmentDTO>>(equipmentDTOS, HttpStatus.OK);
    }


}
