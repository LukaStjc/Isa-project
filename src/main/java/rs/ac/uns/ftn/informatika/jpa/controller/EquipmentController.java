package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.informatika.jpa.dto.*;
import rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType;
import rs.ac.uns.ftn.informatika.jpa.exception.CustomOptimisticLockingException;
import rs.ac.uns.ftn.informatika.jpa.exception.EquipmentNotFoundException;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.model.ReservationItem;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.EquipmentService;
import rs.ac.uns.ftn.informatika.jpa.service.ReservationItemService;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Tag(name = "Equipment Controller", description = "Managing equipment for companies.")
@RestController
@RequestMapping("api/equipment")
@CrossOrigin(origins = "http://localhost:3000")
public class EquipmentController {

    @Autowired
    CompanyService companyService;

    @Autowired
    EquipmentService equipmentService;
    @Autowired
    ReservationItemService reservationItemService;

    @Operation(summary = "Retrieve equipment for a specific company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the equipment list!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EquipmentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found!",
                    content = @Content)
    })

    @GetMapping("/company/{id}")
    @Transactional // vasilije dodao
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

    @Operation(summary = "Retrieve all equipment. Fetches a list of all equipment items available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all equipment!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EquipmentDTO.class))),
            @ApiResponse(responseCode = "404", description = "No equipment found!",
                    content = @Content)
    })
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

    @Operation(summary = "Searches for equipment based on the provided name substring.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment search results fetched successfully!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EquipmentDTO.class))),
            @ApiResponse(responseCode = "404", description = "No equipment matching the search criteria!",
                    content = @Content)
    })
    @GetMapping(value = "/search")
    public ResponseEntity<Collection<EquipmentDTO>> searchEquipmentByName(@RequestParam("text") String text) {
        List<Equipment> foundEquipment = (List<Equipment>) equipmentService.findByName(text);

        List<EquipmentDTO> equipmentDTOS = new ArrayList<>();
        for (Equipment e : foundEquipment) {
            equipmentDTOS.add(new EquipmentDTO(e));
        }

        if (foundEquipment.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    @Operation(
            summary = "Create a new equipment",
            description = "Creates a new equipment entry and associates it with the specified company."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipment created successfully", content = @Content(schema = @Schema(implementation = EquipmentBasicDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to create equipment")
    })
    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    @Transactional
    public ResponseEntity<EquipmentBasicDTO> createEquipment(
            @Parameter(description = "Details of the equipment to be created", required = true, content = @Content(schema = @Schema(implementation = EquipmentBasicDTO.class)))
            @RequestBody EquipmentBasicDTO dto) {

        // Log incoming request
        System.out.println("Received equipment DTO: " + dto);

        // Validate inputs
        if (dto.getName() == null || dto.getName().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (dto.getDescription() == null || dto.getDescription().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (dto.getEquipmentType() == null || dto.getEquipmentType().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            if (dto.getPrice() <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }


            Company company = companyService.findBy(dto.getCompanyId());
            Equipment equipment = new Equipment(dto, company);
            Equipment newEquipment = equipmentService.save(equipment);

            // Log successful creation
            System.out.println("Created equipment: " + newEquipment);

            return new ResponseEntity<>(new EquipmentBasicDTO(newEquipment), HttpStatus.CREATED);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Delete equipment", description = "Deletes the equipment with the specified ID. Only accessible to users with the COMPANY_ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Equipment not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access denied for non-admin users")
    })
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('COMPANY_ADMIN')")

    public ResponseEntity<String> deleteEquipment(@PathVariable Integer id){
        // Find the equipment by its ID
        Equipment equipment = equipmentService.findBy(id);

        if (equipment == null) {
            return new ResponseEntity<>("Equipment not found.", HttpStatus.NOT_FOUND);
        }

        // Remove associated ReservationItems before deleting the equipment
        List<ReservationItem> reservationItems = reservationItemService.findByEquipment(equipment);

        for (ReservationItem item : reservationItems) {
            reservationItemService.delete(item);
        }

        // Now delete the equipment
        equipmentService.delete(equipment);

        return new ResponseEntity<>("Equipment successfully deleted.", HttpStatus.OK);
    }

    @Operation(
            summary = "Update equipment",
            description = "Updates the details of the specified equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment updated successfully", content = @Content(schema = @Schema(implementation = EquipmentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Equipment not found"),
            @ApiResponse(responseCode = "409", description = "Optimistic locking failure")
    })
    @PutMapping("/update/{id}")
    @Transactional
// @PreAuthorize("hasRole('COMPANY_ADMIN')")
    public ResponseEntity<EquipmentDTO> updateEquipment(
            @Parameter(description = "ID of the equipment to be updated", required = true)
            @PathVariable Integer id,

            @RequestBody(required = true)
            @Parameter(description = "Equipment details", required = true)
            EquipmentBasicDTO dto
    ){
        Equipment equipment = equipmentService.findBy(id);

        if (equipment == null) {
            throw new EquipmentNotFoundException("The equipment has been deleted by another company admin   .");
        }

        if (!equipment.getVersion().equals(dto.getVersion())) {
            throw new CustomOptimisticLockingException("The equipment has been modified by another user. Please reload and try again.");
        }
        // Validate inputs
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name field cannot be empty.");        }

        if (dto.getDescription() == null || dto.getDescription().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (dto.getEquipmentType() == null || dto.getEquipmentType().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(dto.getQuantity()< 0 || dto.getQuantity() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }


        equipment.updateProperties(dto);

        if (dto.getQuantity() == 0) {
            deleteEquipment(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        Equipment updatedEquipment = equipmentService.save(equipment);
        return new ResponseEntity<>(new EquipmentDTO(updatedEquipment), HttpStatus.OK);
    }

    @Operation(
            summary = "Get equipment by ID",
            description = "Retrieves the details of the equipment specified by the ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment retrieved successfully", content = @Content(schema = @Schema(implementation = EquipmentBasicDTO.class))),
            @ApiResponse(responseCode = "404", description = "Equipment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentBasicDTO> getById(@PathVariable Integer id){
        Equipment equipment = equipmentService.findBy(id);

        return new ResponseEntity<>(new EquipmentBasicDTO(equipment), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all equipment types",
            description = "Retrieves a list of all available equipment types from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of equipment types"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-types")
    public ResponseEntity<List<String>> getEquipmentTypes(){
        return new ResponseEntity(equipmentService.getEquipmentTypes(), HttpStatus.OK);
    }

}
