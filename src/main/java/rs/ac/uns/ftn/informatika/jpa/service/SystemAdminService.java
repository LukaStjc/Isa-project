package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.SystemAdmin;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyAdminRepository;
import rs.ac.uns.ftn.informatika.jpa.repository.SystemAdminRepository;

import java.util.Optional;

@Service
public class SystemAdminService {

    @Autowired
    private SystemAdminRepository systemAdminRepository;

    public void save(SystemAdmin systemAdmin) {
        systemAdminRepository.save(systemAdmin);
    }


    public Boolean isCurrentPassword(String password, int id){
        Optional<SystemAdmin> optionalSystemAdmin =  systemAdminRepository.findById(id);
        if(optionalSystemAdmin.isEmpty()) return false;

        SystemAdmin systemAdmin = optionalSystemAdmin.get();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.matches(password, systemAdmin.getPassword());
    }

    public Optional<SystemAdmin> getById(Integer id){
        return systemAdminRepository.findById(id);
    }
}
