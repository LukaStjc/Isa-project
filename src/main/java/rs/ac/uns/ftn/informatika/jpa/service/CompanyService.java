package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyRepository;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public List<Company> findAll() {
       return companyRepository.findAll();
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }
    
    public Company findOne(Integer id) {
        return companyRepository.findById(id).orElseGet(null);
    }

    public List<Company> findByNameContaining(String text) {
        return companyRepository.findByNameContaining(text);
    }

    public Company findExistingByName(String name){
        return companyRepository.findByName(name);
    }

    public Company findBy(Integer id) {
        return companyRepository.findById(id).orElseThrow(() -> new ResourceAccessException("Company with id:" + id + " does not exist."));
    }
}
