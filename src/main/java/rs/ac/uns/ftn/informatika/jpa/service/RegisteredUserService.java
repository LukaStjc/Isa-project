package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.repository.RegisteredUserRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

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

}
