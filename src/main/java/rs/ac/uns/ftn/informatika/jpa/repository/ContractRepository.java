package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ContractStatus;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;
import rs.ac.uns.ftn.informatika.jpa.model.Hospital;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    @Query("SELECT COUNT(c) > 0 FROM Contract c WHERE c.hospital = :hospital AND c.company = :company AND c.status = :status")
    boolean existsActiveContract(@Param("hospital") Hospital hospital, @Param("company") Company company, @Param("status") ContractStatus status);

}
