package rs.ac.uns.ftn.informatika.jpa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.UserRequest;
import rs.ac.uns.ftn.informatika.jpa.model.Role;
import rs.ac.uns.ftn.informatika.jpa.model.User;
import rs.ac.uns.ftn.informatika.jpa.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleServiceImpl roleService;

//	@Override
	public User findByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username);
	}

	public User findById(Long id) throws AccessDeniedException {
		return userRepository.findById(Math.toIntExact(id)).orElseGet(null);
	}

	public List<User> findAll() throws AccessDeniedException {
		return userRepository.findAll();
	}

//	@Override
	public User save(UserRequest userRequest) {
		User u = new User();
		u.setEmail(userRequest.getEmail());
		
		// pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
		// treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
		u.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		
		u.setFirstName(userRequest.getFirstname());
		u.setLastName(userRequest.getLastname());
		u.setEnabled(true);
		u.setEmail(userRequest.getEmail());

		// u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER
		List<Role> roles = roleService.findByName("ROLE_USER");
		u.setRoles(roles);
		
		return this.userRepository.save(u);
	}

}
