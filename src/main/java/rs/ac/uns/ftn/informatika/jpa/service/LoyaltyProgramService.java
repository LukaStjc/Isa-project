package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.LoyaltyProgram;
import rs.ac.uns.ftn.informatika.jpa.repository.LoyaltyProgramRepository;

@Service
public class LoyaltyProgramService {
    @Autowired
    LoyaltyProgramRepository loyaltyProgramRepository;

    LoyaltyProgram getOne(int id) {
        return loyaltyProgramRepository.getOne(id);
    }
}
