package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyAdminDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.UserDTO;
import rs.ac.uns.ftn.informatika.jpa.model.SystemAdmin;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.service.SystemAdminService;
import rs.ac.uns.ftn.informatika.jpa.service.UserService;

import java.util.Optional;

@Tag(name = "System Admins", description = "Handles everything about managing the app by admins")
@RestController
@RequestMapping(value = "api/system-admins")
@CrossOrigin(origins = "http://localhost:3000")
public class SystemAdminController {

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserService userService;

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

    //TODO ovo je hardcodovano, treba sa onim tokenima izvuci id iz ulogovanog korisnika
    @PutMapping(value = "/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody UserDTO userDTO){
        //hardcodovan deo
        userDTO.setId(7);

        Optional<User> optionalUser = userService.findById(userDTO.getId());
        if(!optionalUser.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        user.setPassword(userDTO.getPassword());

        try{
            userService.save(user);
        }
        catch (RuntimeException e){
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<>(HttpStatus.OK);
    }

    //TODO potrebno je dodati proveru passworda koji je vezan za id admina
    @GetMapping(value = "/check-password")
    public ResponseEntity<Boolean> isCurrentPassword(@RequestParam("password") String password){
        Boolean matchesCurrentPassword = systemAdminService.isCurrentPassword(password, 7);

        return new ResponseEntity<>(matchesCurrentPassword, HttpStatus.OK);


    }
}
