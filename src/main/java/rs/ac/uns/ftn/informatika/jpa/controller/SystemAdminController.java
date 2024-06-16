package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.UserDTO;
import rs.ac.uns.ftn.informatika.jpa.model.SystemAdmin;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.service.SystemAdminService;
import rs.ac.uns.ftn.informatika.jpa.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping(value = "api/system-admins")
@CrossOrigin(origins = "http://localhost:3000")
public class SystemAdminController {

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping(value = "/create", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
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

    @PutMapping(value = "/update-password")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<Void> updatePassword(@RequestBody UserDTO userDTO){

        SystemAdmin systemAdmin = getUserCredentials();
        userDTO.setId(systemAdmin.getId());

        Optional<User> optionalUser = userService.findById(userDTO.getId());
        if(optionalUser.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        String newPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(newPassword);

        try{
            userService.save(user);
        }
        catch (RuntimeException e){
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/check-password")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<Boolean> isCurrentPassword(@RequestParam("password") String password){
        SystemAdmin systemAdmin = getUserCredentials();

        Boolean matchesCurrentPassword = systemAdminService.isCurrentPassword(password, systemAdmin.getId());

        return new ResponseEntity<>(matchesCurrentPassword, HttpStatus.OK);
    }

    private SystemAdmin getUserCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (SystemAdmin) authentication.getPrincipal();
    }
}
