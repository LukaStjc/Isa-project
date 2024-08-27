package rs.ac.uns.ftn.informatika.jpa.service;

import com.beust.jcommander.DefaultUsageFormatter;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.informatika.jpa.dto.*;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus;
import rs.ac.uns.ftn.informatika.jpa.model.*;
import rs.ac.uns.ftn.informatika.jpa.repository.ReservationRepository;

import javax.mail.MessagingException;
import javax.persistence.OptimisticLockException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.*;

import static rs.ac.uns.ftn.informatika.jpa.enumeration.LoyaltyType.*;
import static rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus.*;

@Service
@Transactional
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

    @Autowired
    private RegisteredUserService registeredUserService;

    @Autowired
    private LoyaltyProgramService loyaltyProgramService;

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


    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateReservationByPremadeAppointment(ReservationByPremadeAppointmentDTO reservationDTO)
            throws DataAccessException, ClassNotFoundException, MailException, MessagingException, OptimisticLockException {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationDTO.getReservationId());

        if (!optionalReservation.isPresent()) {
            throw new RuntimeException("Reservation with ID " + reservationDTO.getReservationId() + " not found");
        }

        Reservation reservation = optionalReservation.get();
        User user = getUserCredentinals();
        RegisteredUser registeredUser = (RegisteredUser) user;

        // Other user has already created reservation, since it has an old website version with the same available predefined reservation
        if (reservation.getItems().isEmpty() == false) throw new RuntimeException();

        // check if the user is forbidden to make a reservation this month
        if (((RegisteredUser) user).getPenaltyPoints() >= 3) {
            throw new RuntimeException("The user is forbidden from making a new reservation this month since he has canceled many reservations!");
        }

        // check if the user is forbidden to make a reservation since he has already canceled it
        boolean alreadyCancelled = checkIfUserAlreadyCanceledReservation(user.getId(), reservation.getStartingDate());
        if (alreadyCancelled)
            throw new RuntimeException("The user is forbidden from making a new reservation since he has already canceled this reservation!");

        updateReservationDetails(reservation, registeredUser);

        for (ReservationItemDTO item : reservationDTO.getReservationItems()) {
            Equipment equipment = equipmentService.findBy(item.getEquipmentId());

            if (equipment == null)
                throw new ClassNotFoundException("Equipment with ID " + item.getEquipmentId() + " not found");

            if (equipment.getAvailableQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("The chosen quantity of equipment with id " + item.getEquipmentId() + " is larger than the possible quantity");
            }

            addReservationItem(reservation, item, equipment);

            setAvailableQuantity(item, equipment);

            equipmentService.save(equipment);
        }

        for (ReservationItem reservationItem : reservation.getItems()) {
            reservationItemService.save(reservationItem);
        }

        applyDiscountAndUpdateLoyalty(reservation,registeredUser);
        registeredUserService.save(registeredUser);

        reservationRepository.save(reservation);

        emailService.sendReservationQRCodeASync(registeredUser, reservation);
    }

    private void setAvailableQuantity(ReservationItemDTO item, Equipment equipment) {
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - item.getQuantity());
    }

    private void addReservationItem(Reservation reservation, ReservationItemDTO item, Equipment equipment) {
        ReservationItem reservationItem = new ReservationItem(equipment, item.getQuantity());
        reservation.getItems().add(reservationItem);

        reservation.totalSum += equipment.getPrice() * item.getQuantity();
    }

    private void updateReservationDetails(Reservation reservation, RegisteredUser registeredUser) {
        reservation.user = registeredUser;
        reservation.hospital = registeredUser.getHospital();
        reservation.status = Ready;

        if (reservation.totalSum == null)
            reservation.totalSum = 0.0;
    }

    private boolean checkIfUserAlreadyCanceledReservation(int userId, Date reservationStartingDate) {
        List<Reservation> userReservations  = reservationRepository.findAllByUserId(userId);

        if (userReservations != null) {
            for (Reservation tempReservation : userReservations) {
                // Maybe date comparison isn't proper
                if ((tempReservation.status == Cancelled) && (tempReservation.getStartingDate().equals(reservationStartingDate))) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    // todo: exception ako nije registered user, mada on ako je ulogovan samo vidi to i to sme da radi
    private RegisteredUser getUserCredentinals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        RegisteredUser registeredUser = (RegisteredUser) user;

        return registeredUser;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void cancelReservation(int id) throws Exception {
        // Get the user from the context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        RegisteredUser loggedInUser = (RegisteredUser) user;

        // Check is a registered user creator of the reservation
        Reservation oldReservation = reservationRepository.findReservationById(id);
        if (!oldReservation.getUser().getEmail().equals(loggedInUser.getEmail()))
            throw new IllegalArgumentException("The logged-in user didn't create the reservation with number " + id);

        // Cancel old reservation, deallocate equipment and increase company equipment quantity
        for (ReservationItem tempItem : oldReservation.getItems()) {
            // Enlarge equipment quantity
            setAvailableQuantityOfEquipment(tempItem);
            reservationItemService.delete(tempItem);
        }
        updateCancelledReservationDetails(oldReservation);
        reservationRepository.save(oldReservation);

        // Penalize the user
        penalizeUser(loggedInUser, oldReservation);

        // Copy of the reservation should be created if other users want to reuse it
        Reservation newReservation = createReservationForSameAppointment(oldReservation);

        // It is used to test optimistic locking
        Thread.sleep(20000);

        reservationRepository.save(newReservation);
    }

    private Reservation createReservationForSameAppointment(Reservation oldReservation) {
        Reservation newReservation  = new Reservation();
        newReservation.status = Created;
        newReservation.admin = oldReservation.admin;
        newReservation.setDurationMinutes(oldReservation.getDurationMinutes());
        newReservation.setStartingDate(oldReservation.getStartingDate());
        newReservation.setHospital(oldReservation.getHospital());
        return newReservation;
    }

    private void updateCancelledReservationDetails(Reservation oldReservation) {
        oldReservation.status = Cancelled;
        oldReservation.setItems(null);
    }

    private void penalizeUser(RegisteredUser loggedInUser, Reservation oldReservation) {
        ZonedDateTime currentDate = ZonedDateTime.now();
        ZonedDateTime startingDate = ZonedDateTime.ofInstant(oldReservation.getStartingDate().toInstant(), currentDate.getZone());
        Duration duration = Duration.between(currentDate, startingDate);
        // Cancelling a reservation within 24 hours before the scheduled shipping time will result in a penalty of 2 points
        if (duration.toHours() <= 24) loggedInUser.setPenaltyPoints(loggedInUser.getPenaltyPoints() + 2);
        else loggedInUser.setPenaltyPoints(loggedInUser.getPenaltyPoints() + 1);
        registeredUserService.save(loggedInUser);
    }

    public boolean existsByUserAndCompany(RegisteredUser registeredUser, Company company){
        List<Reservation> reservations = reservationRepository.findAllByUserId(registeredUser.getId());
        for(Reservation reservation : reservations){
            for(ReservationItem item : reservation.getItems()){
                if(item.getEquipment().getCompany().getId()==company.getId()){
                    return true;
                }
            }
        }
        return false;
    }

    public List<ReservationProfileDTO> findAllCompleted(String sortBy, String sortDirection){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RegisteredUser registeredUser = registeredUserService.findByEmail(authentication.getName());

        Sort sort = null;

        if(!sortBy.equals("startingDate") && !sortBy.equals("totalSum") && !sortBy.equals("durationMinutes")){
            sortBy = new String("startingDate");
        }

        sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        List<ReservationProfileDTO> dtos = new ArrayList<>();
        for(Reservation r:reservationRepository.findAllByUserAndStatus(registeredUser, Completed, sort)){
            ReservationProfileDTO profile = new ReservationProfileDTO();
            profile.setId(r.getId());
            profile.setTotalSum(r.getTotalSum());
            profile.setAdminName(r.getUser().getFirstName());
            profile.setAdminLastName(r.getUser().getLastName());
            profile.setDurationMinutes(r.getDurationMinutes());
            profile.setStartingDate(r.getStartingDate());
            List<ReservationItemProfileDTO> resItems = new ArrayList<>();
            for(ReservationItem ri: r.getItems()){
                ReservationItemProfileDTO ridto = new ReservationItemProfileDTO();
                ridto.setEquipmentName(ri.getEquipment().getName());
                ridto.setQuantity(ri.getQuantity());
                resItems.add(ridto);
            }
            profile.setItems(resItems);
            dtos.add(profile);
        }

        return dtos;

    }




    public List<ReservationProfileDTO> findAllReady(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RegisteredUser registeredUser = registeredUserService.findByEmail(authentication.getName());


        List<ReservationProfileDTO> dtos = new ArrayList<>();
        for(Reservation r:reservationRepository.findAllByUserAndStatus(registeredUser, Ready)){
            ReservationProfileDTO profile = new ReservationProfileDTO();
            profile.setId(r.getId());
            profile.setTotalSum(r.getTotalSum());
            profile.setAdminName(r.getUser().getFirstName());
            profile.setAdminLastName(r.getUser().getLastName());
            profile.setDurationMinutes(r.getDurationMinutes());
            profile.setStartingDate(r.getStartingDate());
            List<ReservationItemProfileDTO> resItems = new ArrayList<>();
            for(ReservationItem ri: r.getItems()){
                ReservationItemProfileDTO ridto = new ReservationItemProfileDTO();
                ridto.setEquipmentName(ri.getEquipment().getName());
                ridto.setQuantity(ri.getQuantity());
                resItems.add(ridto);
            }
            profile.setItems(resItems);
            dtos.add(profile);
        }

        return dtos;

    }


    private void setAvailableQuantityOfEquipment(ReservationItem tempItem) {
        tempItem.getEquipment().setAvailableQuantity(
                tempItem.getEquipment().getAvailableQuantity() + tempItem.getQuantity());
    }

    private void applyDiscountAndUpdateLoyalty(Reservation reservation, RegisteredUser registeredUser) {
        reservation.setTotalSum((double) Math.round( reservation.totalSum - reservation.totalSum * ((float) registeredUser.getLoyaltyProgram().getDiscount_rate() / 100) ));
        registeredUser.setPoints(registeredUser.getPoints() + 1);

        // Upgrade loyalty program
        if (registeredUser.getLoyaltyProgram().getMaxPoints() < registeredUser.getPoints()) {
            LoyaltyProgram oldLoyaltyProgram = registeredUser.getLoyaltyProgram();

            if (oldLoyaltyProgram.getType() == Bronze) {
                LoyaltyProgram newLoyaltyProgram = loyaltyProgramService.findByType(Silver);
                registeredUser.setLoyaltyProgram(newLoyaltyProgram);
            }

            if (oldLoyaltyProgram.getType() == Silver){
                LoyaltyProgram newLoyaltyProgram = loyaltyProgramService.findByType(Gold);
                registeredUser.setLoyaltyProgram(newLoyaltyProgram);
            }
        }

    }

}
