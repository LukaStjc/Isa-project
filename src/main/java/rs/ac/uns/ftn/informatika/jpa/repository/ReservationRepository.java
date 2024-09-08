package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.model.Reservation;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByAdminAndUserIsNull(CompanyAdmin admin);

    Reservation findReservationById(int id);

    List<Reservation> findAllByUserId(int id);

    List<Reservation> findAllByUserAndStatus(RegisteredUser registeredUser, ReservationStatus status, Sort sort);

    List<Reservation> findAllByUserAndStatus(RegisteredUser registeredUser, ReservationStatus status);
    @Query("SELECT r.user FROM Reservation r WHERE r.admin.id = :adminId")
    List<RegisteredUser> findUniqueUsersByAdminId(@Param("adminId") Integer adminId);

    @Query("SELECT r FROM Reservation r WHERE r.admin.id = :adminId AND r.status = :status")
    List<Reservation> findByAdminIdAndStatus(@Param("adminId") Integer adminId, @Param("status") ReservationStatus status);

//    @Lock(LockModeType.PESSIMISTIC_WRITE) DAJE NEKE GRESKE KAO MG LOCK I NATIVE SQL
//    @Query(value = "SELECT * FROM reservations r WHERE " +
//            "(:startDate BETWEEN r.starting_date AND r.starting_date + INTERVAL '1 minute' * r.duration_minutes OR " +
//            "r.starting_date BETWEEN :startDate AND :endDate)",
//            nativeQuery = true)
//    List<Reservation> findConflictingReservations(@Param("startDate") Date startDate,
//                                                  @Param("endDate") Date endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r")
    List<Reservation> lockAllReservations();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.admin.id = :adminId AND r.status = :status")
    List<Reservation> findAllReservationsByAdminId(@Param("adminId") Integer adminId, ReservationStatus status);




    List<Reservation> findAllByUser(RegisteredUser registeredUser);

    List<Reservation> findAllByAdmin(CompanyAdmin admin);

}
