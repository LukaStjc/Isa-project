package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.model.Company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    List<Company> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Company c WHERE LOWER(c.location.country) LIKE LOWER(CONCAT('%', :location, '%')) OR LOWER(c.location.city) like LOWER(CONCAT('%', :location, '%'))")
    List<Company> findByLocationContainingIgnoreCase(String location);

    Company findByName(String name);

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND (LOWER(c.location.country) like LOWER(CONCAT('%', :location, '%')) OR LOWER(c.location.city) like LOWER(CONCAT('%', :location, '%')))")
    List<Company> findByNameAndLocationContaining(String name, String location);


}
