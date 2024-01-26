package rs.ac.uns.ftn.informatika.jpa.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserDTO;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.service.EmailService;
import rs.ac.uns.ftn.informatika.jpa.service.RegisteredUserService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("api/")
public class RegisteredUserController {

	private Logger logger = LoggerFactory.getLogger(RegisteredUserController.class);

	@Autowired
	private EmailService emailService;

	@Autowired
	private RegisteredUserService registeredUserService;

	@PostMapping("/signup")
	public ResponseEntity<String> signUpSync(@RequestBody RegisteredUserDTO registeredUserDTO){

		// todo: handle-uj exception?
		User existUser = registeredUserService.getByEmail(registeredUserDTO.getEmail().trim().toLowerCase());

		if (existUser != null) {
			return new ResponseEntity<>("Unsuccessful: email already exists", HttpStatus.BAD_REQUEST);
		}

		try {
//				System.out.println("Thread id: " + Thread.currentThread().getId());
			emailService.sendNotificaitionSync(registeredUserDTO);
		}catch( Exception e ){
			logger.info("Error during the email-sending process: " + e.getMessage());
			return new ResponseEntity<>("unsuccessful", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("successful", HttpStatus.CREATED);
	}

	@GetMapping("/activate")
	public ResponseEntity activateUser(@RequestParam("text") String activationCode) {

		RegisteredUser registeredUser = registeredUserService.getByActivationCode(activationCode);

		if (registeredUser == null || registeredUser.isEnabled() == true)
			return new ResponseEntity("Invalid request!", HttpStatus.BAD_REQUEST);

		registeredUser.setEnabled(true);

		registeredUserService.save(registeredUser);

		return new ResponseEntity("You have successfully activated the account!", HttpStatus.OK);
	}

}
