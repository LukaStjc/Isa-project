package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.repository.RegisteredUserRepository;
import rs.ac.uns.ftn.informatika.jpa.dto.RegisteredUserProfileDTO;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    public RegisteredUser getByActivationCode(String activationCode) {
        return registeredUserRepository.getByActivationCode(activationCode);
    }
    
    public void save(RegisteredUser registeredUser) {
        registeredUserRepository.save(registeredUser);
    }

    public RegisteredUser getByEmail(String email) {
        return registeredUserRepository.getByEmail(email);
    }

    public RegisteredUser findById(Integer id) { return  registeredUserRepository.findRegisteredUserById(id); }

    public RegisteredUser findByEmail(String email) { return  registeredUserRepository.findRegisteredUserByEmail(email); }

    public void updateUserProfile(RegisteredUser user, Map<String, Object> updates){
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(RegisteredUser.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, value);
            }
        });

        save(user);
    }

}
