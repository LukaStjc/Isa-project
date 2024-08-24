package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserProfileDTO;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.service.EmailService;
import rs.ac.uns.ftn.informatika.jpa.service.RegisteredUserService;

import java.security.Principal;
import java.util.Map;

@Tag(name = "Registered User Controllers", description = "Handles user registration and account activation.")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class RegisteredUserController {

	private Logger logger = LoggerFactory.getLogger(RegisteredUserController.class);

	@Autowired
	private EmailService emailService;

	@Autowired
	private RegisteredUserService registeredUserService;

	@Operation(summary = "Registers a new user by their email and sends an activation email.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content),
			@ApiResponse(responseCode = "400", description = "Unsuccessful: email already exists or error during the email sending process", content = @Content)
	})
	@PostMapping("/signup")
	public ResponseEntity<String> signUpSync(@RequestBody RegisteredUserDTO registeredUserDTO){

		// todo: handle-uj exception?
		User existUser = registeredUserService.getByEmail(registeredUserDTO.getEmail().trim().toLowerCase());

		if (existUser != null) {
			return new ResponseEntity<>("Unsuccessful: email already exists", HttpStatus.BAD_REQUEST);
		}

		try {
//				System.out.println("Thread id: " + Thread.currentThread().getId());
			emailService.sendNotificationSync(registeredUserDTO);
		} catch(PessimisticLockingFailureException e){
			logger.info("An error occurred while registering the user; please try again. Transactional error." + e.getMessage());
			return new ResponseEntity<>("unsuccessful", HttpStatus.BAD_REQUEST);
		} catch( Exception e){
			logger.info("Error during the email-sending process: " + e.getMessage());
			return new ResponseEntity<>("unsuccessful", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("successful", HttpStatus.CREATED);
	}

	@Operation(summary = "Activates a user's account using a provided activation code.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Account successfully activated!", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid request: either activation code is incorrect, or account is already enabled!", content = @Content)
	})
	@GetMapping("/activate")
	public ResponseEntity activateUser(@RequestParam("text") String activationCode) {

		RegisteredUser registeredUser = registeredUserService.getByActivationCode(activationCode);

		if (registeredUser == null || registeredUser.isEnabled() == true)
			return new ResponseEntity("Invalid request!", HttpStatus.BAD_REQUEST);

		registeredUser.setEnabled(true);

		registeredUserService.save(registeredUser);

		return new ResponseEntity("You have successfully activated the account!", HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@GetMapping("/profile")
	public ResponseEntity<RegisteredUser> showUserProfile(Principal principal){

		RegisteredUser registeredUser = registeredUserService.findByEmail(principal.getName());

		if (registeredUser == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		//RegisteredUserProfileDTO profile = new RegisteredUserProfileDTO(registeredUser);

		return ResponseEntity.ok(registeredUser);
	}


	@PreAuthorize("hasRole('REGISTERED_USER')")
	@PatchMapping("/profile/update")
	public ResponseEntity<RegisteredUser> updateUserProfile(@RequestBody /*Map<String, Object> updates*/ RegisteredUserProfileDTO updateDTO, Principal principal){

		/*if (updates.containsKey("email")) {
			return new ResponseEntity<>("Updating email is not allowed", HttpStatus.BAD_REQUEST);
		}*/

		RegisteredUser registeredUser = registeredUserService.findByEmail(principal.getName());

		if (registeredUser == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		//registeredUserService.updateUserProfile(registeredUser, updateDTO);

		//RegisteredUserProfileDTO profile = new RegisteredUserProfileDTO(registeredUser);

		return ResponseEntity.ok(registeredUserService.updateUserProfile(registeredUser, updateDTO));
	}

}
