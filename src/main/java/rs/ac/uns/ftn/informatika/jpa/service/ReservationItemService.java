package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.ReservationItem;
import rs.ac.uns.ftn.informatika.jpa.repository.ReservationItemRepository;

import java.util.List;

@Service
public class ReservationItemService {

    @Autowired
    private ReservationItemRepository reservationItemRepository;

    public void save(ReservationItem reservationItem) {
        reservationItemRepository.save(reservationItem);
    }

    public void delete(ReservationItem reservationItem) {
        reservationItemRepository.delete(reservationItem);
    }

    public List<ReservationItem> findByEquipment(Equipment equipment) {
        return reservationItemRepository.findByEquipment(equipment);
    }


}
