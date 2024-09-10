package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ContractStatus;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;
import rs.ac.uns.ftn.informatika.jpa.model.Hospital;

import java.util.Date;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    @Query("SELECT c FROM Contract c WHERE c.hospital = :hospital AND c.company = :company AND c.status = :status")
    Contract getActiveContract(@Param("hospital") Hospital hospital, @Param("company") Company company, @Param("status") ContractStatus status);


    @Query("SELECT COUNT(C) > 0  FROM Contract c WHERE c.hospital = :hospital AND c.company = :company AND c.status = :status")
    Boolean existsActiveContract(@Param("hospital") Hospital hospital, @Param("company") Company company, @Param("status") ContractStatus status);

    @Query("SELECT c FROM Contract c WHERE DAY(c.date) = :dayOfMonth AND " +
            "HOUR(c.date) = :hour AND MINUTE(c.date) = :minute")
    List<Contract> findByDayOfMonthAndTime(
            @Param("dayOfMonth") Integer dayOfMonth,
            @Param("hour") Integer hour,
            @Param("minute") Integer minute);


    @Query("SELECT c FROM Contract c WHERE " +
            "EXTRACT(DAY FROM c.date) = EXTRACT(DAY FROM :date) AND " +
            "EXTRACT(HOUR FROM c.date) = EXTRACT(HOUR FROM :date) AND " +
            "EXTRACT(MINUTE FROM c.date) = EXTRACT(MINUTE FROM :date)")
    List<Contract> findByDayAndTime(@Param("date") Date date);

    @Query("SELECT c FROM Contract c WHERE c.status = :status and (c.date BETWEEN :start AND :end)")
    List<Contract> findByTimeRange(@Param("start") Date start, @Param("end") Date end, @Param("status")ContractStatus status);

    @Query("SELECT c FROM Contract c WHERE c.status = :status and (c.company.id = :id)")
    List<Contract> findAllActiveContractsByCompany(@Param("id")Integer id,@Param("status") ContractStatus active);
}
