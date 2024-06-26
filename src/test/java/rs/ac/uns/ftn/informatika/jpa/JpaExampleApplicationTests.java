package rs.ac.uns.ftn.informatika.jpa;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationByPremadeAppointmentDTO;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus;
import rs.ac.uns.ftn.informatika.jpa.model.Reservation;
import rs.ac.uns.ftn.informatika.jpa.repository.ReservationRepository;
import rs.ac.uns.ftn.informatika.jpa.service.ReservationService;

import javax.persistence.OptimisticLockException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JpaExampleApplicationTests {

//	@Autowired
//	private ReservationService reservationService;
//
//	@Autowired
//	private ReservationRepository reservationRepository;
//
//	@Test
//	public void testConcurrentUpdatesShouldCauseConflict() {
//
//		// Simulate first user loading the reservation
//		Reservation loadedByUser1 = reservationRepository.findById(5).get();
//
//		// Simulate second user loading the reservation
//		Reservation loadedByUser2 = reservationRepository.findById(5).get();
//
//		// First user modifies and saves the reservation
//		loadedByUser1.setStatus(ReservationStatus.Ready);
//		reservationRepository.save(loadedByUser1);
//
//		// Second user attempts to modify and save the reservation
//		loadedByUser2.setStatus(ReservationStatus.Ready);
//		assertThrows(OptimisticLockException.class, () -> {
//			reservationRepository.save(loadedByUser2);
//		});
//	}


}
