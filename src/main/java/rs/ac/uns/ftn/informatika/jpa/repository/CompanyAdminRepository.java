package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;

import java.util.List;

@Repository
public interface CompanyAdminRepository extends JpaRepository<CompanyAdmin, Integer> {
    CompanyAdmin findByFirstNameAndLastName(String firstName, String lastName);

    List<CompanyAdmin> findByCompanyId(Integer id);
}
