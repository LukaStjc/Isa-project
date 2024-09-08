package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.ChangePasswordDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyAdminBasicDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyAdminRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyAdminService {

    @Autowired
    private CompanyAdminRepository companyAdminRepository;

    public CompanyAdmin save(CompanyAdmin companyAdmin){
        return companyAdminRepository.save(companyAdmin);
    }

    public CompanyAdmin findBy(Integer id) {
        return companyAdminRepository.findById(id).orElseGet(null);
    }

    //TODO izbrisati, dodato zbog 2. kt
    public Optional<CompanyAdmin> findById(Integer id){
        return companyAdminRepository.findById(id);
    }

    public CompanyAdmin findByFirstAndLastName(String firstName, String lastName) {
        return companyAdminRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public Integer findCompanyIdBy(Integer id) {
        Optional<CompanyAdmin> admin =  companyAdminRepository.findById(id);
        Company company = admin.get().getCompany();

        return company.getId();
    }

    public List<CompanyAdminBasicDTO> getCompanyAdmins(Integer id) {

        List<CompanyAdmin> admins = companyAdminRepository.findByCompanyId(id);
        List<CompanyAdminBasicDTO> dtos = new ArrayList<>();

        for (CompanyAdmin admin: admins) {
            CompanyAdminBasicDTO dto = new CompanyAdminBasicDTO(admin);
            dtos.add(dto);
        }
        return dtos;
    }

    public ResponseEntity<Boolean> changePassword(ChangePasswordDTO dto) {
        Optional<CompanyAdmin> optionalAdmin = companyAdminRepository.findById(dto.getId());

        if (optionalAdmin.isPresent()) {
            CompanyAdmin admin = optionalAdmin.get();

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(dto.getPassword());
            admin.setPassword(encodedPassword);
            admin.setPasswordChanged(true);

            companyAdminRepository.save(admin);


            return ResponseEntity.ok(Boolean.TRUE);
        } else {
            // Handle the case where the CompanyAdmin is not found
            return ResponseEntity.notFound().build();
        }
    }
}
