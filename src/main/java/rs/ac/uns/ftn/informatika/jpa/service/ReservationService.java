package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Reservation;
import rs.ac.uns.ftn.informatika.jpa.repository.LocationRepository;
import rs.ac.uns.ftn.informatika.jpa.repository.ReservationRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public List<ReservationDTO> getAllByDate(Date date){
        List<Reservation> reservations = reservationRepository.findAll();

        int day = date.getDate();
        int month = date.getMonth();
        int year = date.getYear();

        List<ReservationDTO> reservationDTOS = new ArrayList<>();
        for(Reservation r : reservations){
            if(r.getStartingDate().getDate() == day && r.getStartingDate().getMonth() == month
                && r.getStartingDate().getYear() == year){

                reservationDTOS.add(new ReservationDTO(r));
            }
        }


        return reservationDTOS;
    }


}
