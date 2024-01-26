package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserDTO;
import rs.ac.uns.ftn.informatika.jpa.model.*;
import rs.ac.uns.ftn.informatika.jpa.repository.HospitalRepository;

import java.util.List;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private RegisteredUserService registeredUserService;

	@Autowired
	private LoyaltyProgramService loyaltyProgramService;

	@Autowired
	private LocationService locationService;

	@Autowired
	HospitalRepository hospitalRepository;

	@Autowired
	HospitalService hospitalService;

	@Autowired
	private Environment env;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleService roleService;

	public void sendNotificaitionSync(RegisteredUserDTO registeredUserDTO) throws MailException, InterruptedException {
		System.out.println("Email sending...");

		// todo: validation
		registeredUserDTO.setEmail(registeredUserDTO.getEmail().trim().toLowerCase());

		RegisteredUser registeredUser = new RegisteredUser(registeredUserDTO);
		registeredUser.setLoyaltyProgram(loyaltyProgramService.getOne(0)); // bronze loyalty program

		// todo: longitude and latitude will be set by google maps api in the future
		Location location = new Location(registeredUserDTO.getCountry(), registeredUserDTO.getCity(), registeredUserDTO.getStreetName(), registeredUserDTO.getStreetNumber(), 0.0, 0.0);
		locationService.save(location);

		registeredUser.setLocation(location);
		registeredUser.setHospital(hospitalService.getOne(registeredUserDTO.getHospitalId())); // todo: a better solution will be implemented
		registeredUser.setPassword(passwordEncoder.encode(registeredUserDTO.getPassword()));
		registeredUser.setEnabled(false);	// todo: inace treba na false

		List<Role> roles = roleService.findByName("ROLE_REGISTERED_USER");
		registeredUser.setRoles(roles);

		registeredUserService.save(registeredUser);

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(registeredUserDTO.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Account activation");
		mail.setText("Dear " + registeredUserDTO.getFirstName() + ",\n\nPlease click on the link below to complete the account activation process." + "\n\n" + "URL: http://localhost:3000/activate?text=" + registeredUser.getActivationCode() + "\n\n" + "Best regards!");
		javaMailSender.send(mail);

		System.out.println("Email is sent!");
	}

	public void sendComplaintReplyEmailToUser(String reply, Complaint complaint){
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(complaint.getRegisteredUser().getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Administrator has replied to your complaint");

		String complaintTo = "";
		if(complaint.getCompany() == null) complaintTo = complaint.getCompanyAdmin().getFirstName();
		else complaintTo = complaint.getCompany().getName();

		mail.setText("Dear " + complaint.getRegisteredUser().getFirstName() + "\n\nYour complaint to " + complaintTo
					+ " was answered by Petar and it follows:\n" + reply);
		//TODO izmeniti ovo Luka u complaint.getSystemAdmin().getFirstName()

		javaMailSender.send(mail);
	}

}
