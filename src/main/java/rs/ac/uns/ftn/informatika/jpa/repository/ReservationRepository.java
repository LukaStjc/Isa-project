package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.model.Reservation;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;
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


}
