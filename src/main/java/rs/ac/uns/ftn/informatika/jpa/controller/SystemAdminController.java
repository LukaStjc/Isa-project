package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyAdminDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.UserDTO;
import rs.ac.uns.ftn.informatika.jpa.model.SystemAdmin;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.service.SystemAdminService;

@RestController
@RequestMapping(value = "api/system-admins")
@CrossOrigin(origins = "http://localhost:3000")
public class SystemAdminController {

    @Autowired
    private SystemAdminService systemAdminService;

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<Void> createSystemAdmin(@RequestBody UserDTO userDTO){
        SystemAdmin systemAdmin = new SystemAdmin(userDTO.getEmail(), userDTO.getFirstName(), userDTO.getLastName());

        try{
            systemAdminService.save(systemAdmin);
        }
        catch (RuntimeException e){
            e.printStackTrace();

            return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
