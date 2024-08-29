package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Rating;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {


    Optional<Rating> findByUserAndCompany(RegisteredUser registeredUser, Company company);

    //boolean existsByUserAndCompany(RegisteredUser registeredUser, Company company);

}
