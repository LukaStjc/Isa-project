package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyLocationDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LocationService locationService;

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

    public Company findBy(Integer id) throws NoSuchElementException {
        return companyRepository.findById(id).get();
    }

    public Company updateCompany(int companyId, CompanyLocationDTO dto) throws NoSuchElementException {
        Company company = findBy(companyId);

        company.setName(dto.getName());
        company.setDescription((dto.getDescription()));

        System.out.println("Usao prvi put");

        Location location = company.getLocation();
        location.setCountry(dto.getCountry());
        location.setCity(dto.getCity());
        location.setStreet(dto.getStreetName());
        location.setStreetNumber(dto.getStreetNumber());
        System.out.println("Usao 2 put");
        locationService.save(location);
        System.out.println("Usao 3 put");
        company.setLocation(location);

        save(company);
        System.out.println("Usao 3 put");

        return company;
    }

}
