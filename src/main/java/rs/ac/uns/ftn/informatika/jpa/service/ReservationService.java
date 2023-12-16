package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationDTO;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;
import rs.ac.uns.ftn.informatika.jpa.model.Reservation;
import rs.ac.uns.ftn.informatika.jpa.repository.ReservationRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public List<ReservationDTO> getAllByDate(Date date, int showWeek){
        List<Reservation> reservations = reservationRepository.findAll();

        final int day = date.getDate();
        final int month = date.getMonth();
        final int year = date.getYear(); // getDay() 0-nedelja, 1-ponedeljak ... 6-subota

        List<ReservationDTO> reservationDTOS = new ArrayList<>();

        if(showWeek == 0){
            for(Reservation r : reservations){
                if(r.getStartingDate().getDate() == day && r.getStartingDate().getMonth() == month
                        && r.getStartingDate().getYear() == year){

                    reservationDTOS.add(new ReservationDTO(r));
                }
            }
        }
        else{   //getDay() -> ako je 3. dan, u nazad se ide 2 -> 3-1=2, u napred se ide 4 -> 7-3=4, ako je 0. dan, to je 7. dan
            int dayInWeek = date.getDay();
            if(dayInWeek == 0) dayInWeek = 7;

            int daysBefore = dayInWeek - 1;
            int daysAfter = 7 - dayInWeek;

            Calendar calendar = Calendar.getInstance();

            // Dodajem rezervacije koje su u nedelji pre izabranog dana
            for(int i=daysBefore; i>0; --i){
                calendar.setTime(date);

                calendar.add(Calendar.DAY_OF_MONTH, -i);

                Date tmpDate = calendar.getTime();
                int tmpDay = tmpDate.getDate();
                int tmpMonth = tmpDate.getMonth();
                int tmpYear = tmpDate.getYear();

                for(Reservation r : reservations){
                    if(r.getStartingDate().getDate() == tmpDay && r.getStartingDate().getMonth() == tmpMonth
                            && r.getStartingDate().getYear() == tmpYear){

                        reservationDTOS.add(new ReservationDTO(r));
                    }
                }
            }

            // Dodajem rezervacije koje su u nedelji posle izabranog dana, kao i rezervacije na izabrani dan
            for(int i=0; i<daysAfter + 1; ++i){
                calendar.setTime(date);

                calendar.add(Calendar.DAY_OF_MONTH, i);

                Date tmpDate = calendar.getTime();
                int tmpDay = tmpDate.getDate();
                int tmpMonth = tmpDate.getMonth();
                int tmpYear = tmpDate.getYear();

                for(Reservation r : reservations){
                    if(r.getStartingDate().getDate() == tmpDay && r.getStartingDate().getMonth() == tmpMonth
                            && r.getStartingDate().getYear() == tmpYear){

                        reservationDTOS.add(new ReservationDTO(r));
                    }
                }
            }
        }


        return reservationDTOS;
    }


    public List<Integer> getAllByMonthAndYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int daysToCheck = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentMonth = date.getMonth();
        int currentYear = date.getYear();

        List<Reservation> reservations = reservationRepository.findAll();
        List<Integer> daysToShow = new ArrayList<>();


        for(int i=1; i<=daysToCheck; ++i){
            for(Reservation r : reservations){

                if(r.getStartingDate().getDate() == i && r.getStartingDate().getMonth() == currentMonth
                    && r.getStartingDate().getYear() == currentYear){

                    daysToShow.add(i);
                    break;
                }
            }
        }

        return daysToShow;
    }

    public List<ReservationDTO> getAllPredefinedByCompanyAdmin(List<CompanyAdmin> companyAdmins) {
        List<ReservationDTO> reservationDTOS = new ArrayList<>();
        List<Reservation> reservations = new ArrayList<>();
        for(CompanyAdmin admin: companyAdmins){
            reservations.addAll(reservationRepository.findByAdminAndUserIsNull(admin));
        }
        for(Reservation reservation: reservations){
            reservationDTOS.add(new ReservationDTO(reservation));
        }
        return reservationDTOS;
    }
}
