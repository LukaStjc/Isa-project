package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.informatika.jpa.enumeration.LoyaltyType;
import rs.ac.uns.ftn.informatika.jpa.model.LoyaltyProgram;

public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Integer> {

    LoyaltyProgram findByType(LoyaltyType loyaltyType);
}
