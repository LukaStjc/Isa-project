package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserProfileDTO;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.service.EmailService;
import rs.ac.uns.ftn.informatika.jpa.service.RegisteredUserService;

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

	@Operation(summary = "Show information about registered user profile", security = {@SecurityRequirement(name = "bearerAuth")})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profile data is fetched successfully", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid request: User is not existing", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!", content = @Content)
	})
	@PreAuthorize("hasRole('REGISTERED_USER')")
	@GetMapping("/profile")
	public ResponseEntity<RegisteredUser> showUserProfile(){

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		RegisteredUser registeredUser = registeredUserService.findByEmail(authentication.getName());
		if(registeredUser == null){
			return new ResponseEntity("User cannot be found!", HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok(registeredUser);
	}


	@Operation(summary = "Updates personal user's personal info ",
			security = {@SecurityRequirement(name = "bearerAuth")})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Update is successfully executed!",
					content = { @Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!", content = @Content)
	})
	@PreAuthorize("hasRole('REGISTERED_USER')")
	@PatchMapping("/profile/update")
	public ResponseEntity<RegisteredUser> updateUserProfile(@RequestBody RegisteredUserProfileDTO updateDTO){

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		RegisteredUser registeredUser = registeredUserService.findByEmail(authentication.getName());

		if (registeredUser == null) {
			return ResponseEntity.notFound().build();
		}

		RegisteredUser updatedUser = registeredUserService.updateUserProfile(registeredUser, updateDTO);
		return ResponseEntity.ok(updatedUser);
	}

}
