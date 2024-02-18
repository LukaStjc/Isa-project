package rs.ac.uns.ftn.informatika.jpa.service;

import org.aspectj.apache.bcel.ExceptionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationByPremadeAppointmentDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationItemDTO;
import rs.ac.uns.ftn.informatika.jpa.model.*;
import rs.ac.uns.ftn.informatika.jpa.repository.ReservationRepository;

import javax.mail.MessagingException;
import java.util.*;

import static rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus.Ready;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired CompanyAdminService companyAdminService;

    @Autowired
    private UserService userService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private ReservationItemService reservationItemService;

    @Autowired
    private EmailService emailService;

    public List<ReservationDTO> getAllByDate(Date date, int showWeek, Integer id){
        List<Reservation> reservations = reservationRepository.findAll();

        final int day = date.getDate();
        final int month = date.getMonth();
        final int year = date.getYear(); // getDay() 0-nedelja, 1-ponedeljak ... 6-subota

        List<ReservationDTO> reservationDTOS = new ArrayList<>();

        //TODO i ovo izbaciti
        Optional<CompanyAdmin> optionalCompanyAdmin = companyAdminService.findById(id);//
        if(!optionalCompanyAdmin.isPresent()){//
            return null;//
        }//
        CompanyAdmin companyAdmin = optionalCompanyAdmin.get();//

        //TODO i u ovim ifovima
        if(showWeek == 0){
            for(Reservation r : reservations){
                if(companyAdmin.getCompany().getId() == r.getAdmin().getCompany().getId() && r.getStartingDate().getDate() == day && r.getStartingDate().getMonth() == month
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
                    if(companyAdmin.getCompany().getId() == r.getAdmin().getCompany().getId() && r.getStartingDate().getDate() == tmpDay && r.getStartingDate().getMonth() == tmpMonth
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
                    if(companyAdmin.getCompany().getId() == r.getAdmin().getCompany().getId() && r.getStartingDate().getDate() == tmpDay && r.getStartingDate().getMonth() == tmpMonth
                            && r.getStartingDate().getYear() == tmpYear){

                        reservationDTOS.add(new ReservationDTO(r));
                    }
                }
            }
        }


        return reservationDTOS;
    }


    public List<Integer> getAllByMonthAndYear(Date date, Integer id) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int daysToCheck = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentMonth = date.getMonth();
        int currentYear = date.getYear();

        List<Reservation> reservations = reservationRepository.findAll();
        List<Integer> daysToShow = new ArrayList<>();

        //TODO i ovo izbaciti
        Optional<CompanyAdmin> optionalCompanyAdmin = companyAdminService.findById(id);//
        if(!optionalCompanyAdmin.isPresent()){//
            return null;//
        }//
        CompanyAdmin companyAdmin = optionalCompanyAdmin.get();//

        for(int i=1; i<=daysToCheck; ++i){
            for(Reservation r : reservations){

                if(companyAdmin.getCompany().getId() == r.getAdmin().getCompany().getId() && r.getStartingDate().getDate() == i && r.getStartingDate().getMonth() == currentMonth
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

    public void save(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    // todo: transakcija, konfliktna situacija
    public void updateReservationByPremadeAppointment(ReservationByPremadeAppointmentDTO reservationDTO) throws DataAccessException, ClassNotFoundException, MailException, MessagingException {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationDTO.getReservationId());

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();

            User user = getUserCredentinals();
            RegisteredUser registeredUser = (RegisteredUser) user;

            reservation.user = registeredUser;
            reservation.hospital = registeredUser.getHospital();
            reservation.status = Ready;

            if (reservation.totalSum == null)
                reservation.totalSum = 0.0;

            for (ReservationItemDTO item : reservationDTO.getReservationItems()) {
                Equipment equipment = equipmentService.findBy(item.getEquipmentId());

                if (equipment == null)
                    throw new ClassNotFoundException("Equipment with ID " + item.getEquipmentId() + " not found");

                if (equipment.getQuantity() < item.getQuantity())
                    throw new IllegalArgumentException("The chosen quantity of equipment with id " + item.getEquipmentId() + " is larger than the possible quantity");

                ReservationItem reservationItem = new ReservationItem(equipment, item.getQuantity());
                reservation.getItems().add(reservationItem);

                reservation.totalSum += equipment.getPrice() * item.getQuantity();
            }

            // transakcijski pristup - radim kad sam siguran da je prethodno prosao
            for (ReservationItem reservationItem : reservation.getItems()) {
                reservationItemService.save(reservationItem);
                Integer updatedQuantity = reservationItem.equipment.getQuantity() - reservationItem.getQuantity();
                equipmentService.updateQuantity(reservationItem.equipment.getId(), updatedQuantity);
            }

            reservationRepository.save(reservation);

            emailService.sendReservationQRCodeSync(registeredUser, reservation);

        } else {
            throw new RuntimeException("Reservation with ID " + reservationDTO.getReservationId() + " not found");
        }
    }

    // todo: exception ako nije registered user, mada on ako je ulogovan samo vidi to i to sme da radi
    private RegisteredUser getUserCredentinals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        RegisteredUser registeredUser = (RegisteredUser) user;

        return registeredUser;
    }


}
