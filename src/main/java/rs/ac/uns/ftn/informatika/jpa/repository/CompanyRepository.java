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

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :text, '%')) or LOWER(c.location.country) like LOWER(CONCAT('%', :text, '%')) or LOWER(c.location.city) like LOWER(CONCAT('%', :text, '%'))")
    List<Company> findByNameContaining(@Param("text") String text);

    Company findByName(String name);

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :text, '%')) or LOWER(c.location.country) like LOWER(CONCAT('%', :text, '%')) or LOWER(c.location.city) like LOWER(CONCAT('%', :text, '%'))")
    List<Company> findByNameOrLocationContaining(@Param("text") String text);


}
