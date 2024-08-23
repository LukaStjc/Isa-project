package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.authentication.PasswordEncoderParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.ChangePasswordDTO;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.*;

// Primer kontrolera cijim metodama mogu pristupiti samo autorizovani korisnici
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;


	// Za pristup ovoj metodi neophodno je da ulogovani korisnik ima ADMIN ulogu
	// Ukoliko nema, server ce vratiti gresku 403 Forbidden
	// Korisnik jeste autentifikovan, ali nije autorizovan da pristupi resursu
	@GetMapping("/user/{userId}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN')")
	public User loadById(@PathVariable Long userId) {
		return this.userService.findById(userId);
	}

	// neko testiranje, nista specijalno
	@GetMapping("/user/all")
	@PreAuthorize("hasRole('SYSTEM_ADMIN')")
	public List<String> loadAll() {
		List<User> users = this.userService.findAll();
		List<String> strings = new ArrayList<String>();

		for (User user : users) {
			strings.add(user.getEmail());
		}

		return strings;
	}


	@GetMapping("/whoami")
	@PreAuthorize("hasAnyRole('REGISTERED_USER', 'SYSTEM_ADMIN', 'COMPANY_ADMIN')")
//	@PreAuthorize("hasRole('REGISTERED_USER')")
	public User user(Principal user) {
		return this.userService.findByEmail(user.getName());
	}
	
	@GetMapping("/foo")
    public Map<String, String> getFoo() {
        Map<String, String> fooObj = new HashMap<>();
        fooObj.put("foo", "bar");
        return fooObj;
    }

	@PutMapping("/user/changepassword")
	@PreAuthorize("hasRole('REGISTERED_USER')")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(authentication.getName());

		if(passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())){
			String newPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
			user.setPassword(newPassword);
			userService.save(user);
			return new ResponseEntity<>("Password successfully changed!", HttpStatus.OK);
		}

		return new ResponseEntity<>("Wrong old password!", HttpStatus.BAD_REQUEST);



	}

}
