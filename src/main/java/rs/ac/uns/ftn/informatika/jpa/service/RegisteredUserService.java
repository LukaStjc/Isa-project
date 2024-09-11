package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.repository.RegisteredUserRepository;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserProfileDTO;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private LocationService locationService;

    public RegisteredUser getByActivationCode(String activationCode) {
        return registeredUserRepository.getByActivationCode(activationCode);
    }
    
    public void save(RegisteredUser registeredUser) {
        registeredUserRepository.save(registeredUser);
    }

    public RegisteredUser getByEmail(String email) {
        return registeredUserRepository.getByEmail(email);
    }

    @Transactional(readOnly = false)
    public RegisteredUser findById(Integer id) {
        return registeredUserRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("RegisteredUser not found with id: " + id));
    }

    public RegisteredUser findByEmail(String email) { return  registeredUserRepository.findRegisteredUserByEmail(email); }

    public RegisteredUser updateUserProfile(RegisteredUser user, RegisteredUserProfileDTO dto){

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getTelephoneNumber() != null) user.setTelephoneNumber(dto.getTelephoneNumber());


        if (dto.getOccupation() != null) user.setOccupation(dto.getOccupation());
        Location loc = locationService.findById(user.getLocation().getId());
        if(dto.getCountry() != null) {
            loc.setCountry(dto.getCountry());
            user.setLocation(loc);
        }

        if(dto.getCity() != null) {
            loc.setCity(dto.getCity());
            user.setLocation(loc);
        }

        if(dto.getStreetName() != null) {
            loc.setStreet(dto.getStreetName());
            user.setLocation(loc);
        }

        if(dto.getStreetNumber() != null) {
            loc.setStreetNumber(dto.getStreetNumber());
            user.setLocation(loc);
        }

        locationService.save(loc);
        save(user);
        return user;
    }

    public List<RegisteredUser> findAll(){
        return registeredUserRepository.findAll();
    }




    public void penalizeUsers(List<Integer> usersForPenalties) {

        List<RegisteredUser> users = registeredUserRepository.findAllById(usersForPenalties);

        for (RegisteredUser user : users) {
            if (user != null) {
                int newPenaltyPoints = user.getPenaltyPoints() + 2;
                user.setPenaltyPoints(newPenaltyPoints);
            }
        }

        registeredUserRepository.saveAll(users);

    }
}
