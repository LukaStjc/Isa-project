package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationItemDTO;
import rs.ac.uns.ftn.informatika.jpa.model.*;
import rs.ac.uns.ftn.informatika.jpa.repository.HospitalRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.print.DocFlavor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

	@Autowired
	private QRCodeService qrCodeService;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void sendNotificationSync(RegisteredUserDTO registeredUserDTO) throws MailException, InterruptedException, PessimisticLockingFailureException {
		System.out.println("Email sending...");

		// todo: validation
		registeredUserDTO.setEmail(registeredUserDTO.getEmail().trim().toLowerCase());

		RegisteredUser registeredUser = new RegisteredUser(registeredUserDTO);
		registeredUser.setLoyaltyProgram(loyaltyProgramService.getOne(0)); // bronze loyalty program

		// todo: longitude and latitude will be set by google maps api in the future
		Location location = new Location(registeredUserDTO.getCountry(), registeredUserDTO.getCity(), registeredUserDTO.getStreetName(), registeredUserDTO.getStreetNumber(), 0.0, 0.0);
		locationService.save(location);

		// todo: dodati metodu koja podesavaa registered usera zbog sequence dijagrama
		registeredUser.setLocation(location);
		registeredUser.setHospital(hospitalService.getOne(registeredUserDTO.getHospitalId())); // todo: a better solution will be implemented
		registeredUser.setPassword(passwordEncoder.encode(registeredUserDTO.getPassword()));
		registeredUser.setEnabled(false);	// todo: inace treba na false

		List<Role> roles = roleService.findByName("ROLE_REGISTERED_USER");
		registeredUser.setRoles(roles);

		registeredUserService.save(registeredUser);

		sendActivationCodeEmail(registeredUser);

		System.out.println("Email is sent!");
	}

	private void sendActivationCodeEmail(RegisteredUser registeredUser) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(registeredUser.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Account activation");
		mail.setText("Dear " + registeredUser.getFirstName() + ",\n\nPlease click on the link below to complete the account activation process." + "\n\n" + "URL: http://localhost:3000/activate?text=" + registeredUser.getActivationCode() + "\n\n" + "Best regards!");
		javaMailSender.send(mail);
	}

	@Async
	public void sendReservationQRCodeASync(RegisteredUser registeredUser, Reservation reservation) throws MailException, MessagingException {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		composeEmailContent(registeredUser, helper);

		String qrCodeContent = getQRCodeContent(reservation);

		byte[] qrCode = qrCodeService.generateQRCodeImage(qrCodeContent, 200, 200);

		addQRCodeAsAttachment(helper, qrCode,"ReservationQRCode.png");
		javaMailSender.send(mimeMessage);
	}

	private void composeEmailContent(RegisteredUser registeredUser, MimeMessageHelper helper) throws MessagingException {
		helper.setTo(registeredUser.getEmail());
		helper.setFrom(env.getProperty("spring.mail.username"));
		helper.setSubject("Reservation details");
		helper.setText("Dear " + registeredUser.getFirstName() + ",\n\nWe are sending you reservation details in the following QR code." + "\n\n" + "Best regards!");
	}

	public String getQRCodeContent(Reservation reservation) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Reservation id: ").append(reservation.getId());
		stringBuilder.append("\nCompany name: " + reservation.getAdmin().getCompany().getName());
		stringBuilder.append("\nUser hospital name: " + reservation.getUser().getHospital().getName());
		stringBuilder.append("\nUser first name: " + reservation.getUser().getFirstName());
		stringBuilder.append("\nUser last name: " + reservation.getUser().getLastName());
		stringBuilder.append("\nAdmin first name: " + reservation.getAdmin().getFirstName());
		stringBuilder.append("\nAdmin last name: " + reservation.getAdmin().getLastName());
		for (ReservationItem item : reservation.getItems()) {
			stringBuilder.append("\nEquipment name: " + item.getEquipment().getName() + "\n\tQuantity: " + item.getQuantity());
		}
		stringBuilder.append("\nTotal price: " + reservation.getTotalSum());

		return stringBuilder.toString();
	}

	private void addQRCodeAsAttachment(MimeMessageHelper helper, byte[] qrCode, String fileName) throws MessagingException {
		ByteArrayResource byteArrayResource = new ByteArrayResource(qrCode);
		helper.addAttachment(fileName, byteArrayResource, "image/png");
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
