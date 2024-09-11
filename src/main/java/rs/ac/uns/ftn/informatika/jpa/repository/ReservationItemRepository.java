package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.ReservationItem;

import java.util.List;

@Repository
public interface ReservationItemRepository extends JpaRepository<ReservationItem, Integer> {
    List<ReservationItem> findByEquipment(Equipment equipment);

}
