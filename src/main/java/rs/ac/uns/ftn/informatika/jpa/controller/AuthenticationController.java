package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.informatika.jpa.dto.JwtAuthenticationRequestDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.JwtResponseDTO;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.util.TokenUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

//Kontroler zaduzen za autentifikaciju korisnika

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	// Prvi endpoint koji pogadja korisnik kada se loguje.
	// Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
	@PostMapping("/login")
	public ResponseEntity<JwtResponseDTO> createAuthenticationToken(
			@RequestBody JwtAuthenticationRequestDTO authenticationRequest, HttpServletResponse response) {
		// Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
		// AuthenticationException

		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				authenticationRequest.getEmail(), authenticationRequest.getPassword()));

		// Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
		// kontekst
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Kreiraj token za tog korisnika
		User user = (User) authentication.getPrincipal();
		String jwt = tokenUtils.generateToken(user.getEmail());
		int expiresIn = tokenUtils.getExpiredIn();

		List<String> roles = user.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		// Vrati token kao odgovor na uspesnu autentifikaciju
//		return ResponseEntity.ok(new UserTokenStateDTO(jwt, expiresIn));
		return ResponseEntity
				.ok(new JwtResponseDTO(jwt, user.getId(), user.getEmail(), roles));
	}


}