package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.*;
import rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.EquipmentService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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

    @GetMapping(value = "/ordering/search")
    public ResponseEntity<List<EquipmentOrderingDTO>> findByNameAndTypeAndScore(
            @RequestParam("name") String name,
            @RequestParam("type") int equipmentType,
            @RequestParam("score") double averageScore){

        List<Equipment> foundEquipment;

        if(equipmentType <= -1 && averageScore == 0){
            foundEquipment = equipmentService.findByName(name);
        }
        else if(equipmentType <= -1){
            foundEquipment = equipmentService.
                    findByNameContainsAndCompany_AverageScoreGreaterThanEquals(name, averageScore);
        }
        else if(averageScore == 0){
            EquipmentType type;
            try{
                type = intToEquipmentType(equipmentType);
            }
            catch(IllegalStateException e){
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            foundEquipment = equipmentService.
                    findByNameContainsAndTypeEquals(name, type);
        }
        else{
            foundEquipment = equipmentService.
                    findByNameContainsAndCompany_AverageScoreGreaterThanEqualsAndTypeEquals(
                            name, averageScore, equipmentType);
        }


        List<EquipmentOrderingDTO> equipmentOrderingDTOS = new ArrayList<>();
        for(Equipment e : foundEquipment){
            equipmentOrderingDTOS.add(new EquipmentOrderingDTO(e));
        }

        return new ResponseEntity<>(equipmentOrderingDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/ordering")
    public ResponseEntity<List<EquipmentOrderingDTO>> getAll(){
        List<Equipment> foundEquipment = equipmentService.findAll();

        List<EquipmentOrderingDTO> equipmentOrderingDTOS = new ArrayList<>();
        for(Equipment e : foundEquipment){
            equipmentOrderingDTOS.add(new EquipmentOrderingDTO(e));
        }

        return new ResponseEntity<>(equipmentOrderingDTOS, HttpStatus.OK);
    }

    public EquipmentType intToEquipmentType(int type) {
        EquipmentType equipmentType;
        switch(type){
            case 0:
                equipmentType = EquipmentType.type1;
                break;
            case 1:
                equipmentType = EquipmentType.type2;
                break;
            case 2:
                equipmentType = EquipmentType.type3;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        return equipmentType;
    }
    @PostMapping(consumes = "application/json")
    public ResponseEntity<EquipmentBasicDTO> createEquipment(@RequestBody EquipmentBasicDTO dto){

        // TODO dodati proveru da li je korisnik Admin sistema
        Company company = companyService.findBy(dto.getCompanyId());
        Equipment equipment = new Equipment(dto, company);
        try{
            equipment = equipmentService.save(equipment);
        }
        catch(RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new EquipmentBasicDTO(equipment), HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEquipment(@PathVariable Integer id){
        Equipment equipment = equipmentService.findBy(id);
        equipmentService.delete(equipment);
        return new ResponseEntity<>("Equipment succesfully deleted", HttpStatus.OK);
    }
    @PutMapping ("/update/{id}")
    public ResponseEntity<EquipmentDTO> updateEquipment(@PathVariable Integer id, @RequestBody EquipmentBasicDTO dto){
        Equipment equipment = equipmentService.findBy(id);
        equipment.updateProperties(dto);
        if(dto.getQuantity() == 0){
            deleteEquipment(id);
            return new ResponseEntity<>(HttpStatus.OK); // Return success response without saving
        }
        equipmentService.save(equipment);
        return new ResponseEntity<>(new EquipmentDTO(equipment), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentBasicDTO> getById(@PathVariable Integer id){
        Equipment equipment = equipmentService.findBy(id);

        return new ResponseEntity<>(new EquipmentBasicDTO(equipment), HttpStatus.OK);
    }

}
