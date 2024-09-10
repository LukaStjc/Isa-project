package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyLocationDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.CompanyProfileDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Location;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LocationService locationService;

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Cacheable(value="companyList", keyGenerator = "customKeyGenerator")
    public List<Company> findAll() {
       return companyRepository.findAll();
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Cacheable(value="company", keyGenerator = "customKeyGenerator")
    public Company findOne(Integer id) {
        return companyRepository.findById(id).orElseGet(null);
    }

    public List<Company> findByNameContaining(String text) {
        return companyRepository.findByNameContainingIgnoreCase(text);
    }

    public Company findExistingByName(String name){
        return companyRepository.findByName(name);
    }

    @Cacheable(value="company", keyGenerator = "customKeyGenerator")
    public Company findBy(Integer id) throws NoSuchElementException {
        return companyRepository.findById(id).get();
    }

    double calculateDistance(RegisteredUser registeredUser, Company com){

        double lat1Rad = Math.toRadians(registeredUser.getLocation().getLatitude());
        double lon1Rad = Math.toRadians(registeredUser.getLocation().getLongitude());
        double lat2Rad = Math.toRadians(com.getLocation().getLatitude());
        double lon2Rad = Math.toRadians(com.getLocation().getLongitude());

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1Rad)*Math.cos(lat2Rad) * Math.sin(deltaLon/2)*Math.sin(deltaLon/2);

        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS_KM * c;

    }

    public List<CompanyProfileDTO> searchAndFilter(RegisteredUser registeredUser, String name, String location, Double minScore, Double maxDistance, String sortBy, String sortDirection){


        List<Company> companies = new ArrayList<>();
        List<CompanyProfileDTO> dtos = new ArrayList<>();

        Sort sort = null;

        if(!sortBy.equals("name") && !sortBy.equals("location.city") && !sortBy.equals("averageScore")){
            sortBy = new String("name");
        }

        sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);


        if ((name == null || name.isEmpty()) && (location == null || location.isEmpty())) {
            companies = companyRepository.findAll(sort);
        } else if (name != null && !name.isEmpty() && (location == null || location.isEmpty())) {
            companies = companyRepository.findByNameContainingIgnoreCase(name, sort);
        } else if ((name == null || name.isEmpty()) && location != null && !location.isEmpty()) {
            companies = companyRepository.findByLocationContainingIgnoreCase(location, sort);
        } else {
            companies = companyRepository.findByNameAndLocationContaining(name, location, sort);
        }




        /*if (minScore != null){
            companies = companies.stream()
                    .filter(company -> company.getAverageScore() >= minScore)
                    .toList();
        }*/

        for (Company c:companies){
                CompanyProfileDTO dto = new CompanyProfileDTO(c);

                if(registeredUser!=null){
                    double distance = calculateDistance(registeredUser, c);
                    dto.setDistance(distance);
                }

                dtos.add(dto);
        }

        if (minScore != null){
            dtos = dtos.stream()
                    .filter(companyProfileDTO -> companyProfileDTO.getAverageScore() >= minScore)
                    .toList();
        }

        if (maxDistance != null && registeredUser!=null){
            dtos = dtos.stream()
                    .filter(companyProfileDTO -> companyProfileDTO.getDistance() <= maxDistance)
                    .toList();
        }

        return dtos;
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

    public Company findBy(String companyName){
        return companyRepository.findCompanyByName(companyName);
    }

}
