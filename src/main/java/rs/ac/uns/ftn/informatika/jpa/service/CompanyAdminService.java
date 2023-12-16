package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyAdminRepository;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyRepository;

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

    public CompanyAdmin findByFirstAndLastName(String firstName, String lastName) {
        return companyAdminRepository.findByFirstNameAndLastName(firstName, lastName);
    }
}
