package rs.ac.uns.ftn.informatika.jpa.service;

import com.beust.jcommander.DefaultUsageFormatter;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.hibernate.PessimisticLockException;
import org.hibernate.StaleStateException;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.informatika.jpa.dto.*;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ReservationStatus;
import rs.ac.uns.ftn.informatika.jpa.exception.CustomRetryableException;
import rs.ac.uns.ftn.informatika.jpa.exception.InsufficientEquipmentException;
import rs.ac.uns.ftn.informatika.jpa.exception.ReservationConflictException;
import rs.ac.uns.ftn.informatika.jpa.model.*;
import rs.ac.uns.ftn.informatika.jpa.repository.ReservationRepository;

import javax.mail.MessagingException;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.print.attribute.DateTimeSyntax;
import java.time.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
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

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private CompanyService companyService;

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

    
    @Cacheable(value="reservationList", keyGenerator = "customKeyGenerator")
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

    @Transactional
    public void save(Reservation reservation) {
        // Calculate the end time of the new reservation
        Date endDate = new Date(reservation.getStartingDate().getTime() + reservation.getDurationMinutes() * 60 * 1000);

        reservationRepository.lockAllReservations();
        // Fetch upcoming reservations
        List<Reservation> allReservations = reservationRepository.findAll();

        // Check for conflicts
        for (Reservation existingReservation : allReservations) {
            Date existingEndDate = new Date(existingReservation.getStartingDate().getTime() + existingReservation.getDurationMinutes() * 60 * 1000);

            if (reservation.getStartingDate().before(existingEndDate) && endDate.after(existingReservation.getStartingDate())) {
                throw new ReservationConflictException("Conflicting reservation exists for the selected time slot.");            }
        }
        // Save the new reservation
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

        //emailService.sendReservationQRCodeASync(registeredUser, reservation);
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
//        Thread.sleep(20000);

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

    public void penalizeUser(RegisteredUser loggedInUser, Reservation oldReservation) {
        try {
            RegisteredUser user = registeredUserService.findById(loggedInUser.getId());
            ZonedDateTime currentDate = ZonedDateTime.now();
            ZonedDateTime startingDate = ZonedDateTime.ofInstant(oldReservation.getStartingDate().toInstant(), currentDate.getZone());
            Duration duration = Duration.between(currentDate, startingDate);

            // Cancelling a reservation within 24 hours before the scheduled shipping time will result in a penalty of 2 points
            if (duration.toHours() <= 24) {
                user.setPenaltyPoints(user.getPenaltyPoints() + 2);
            } else {
                user.setPenaltyPoints(user.getPenaltyPoints() + 1);
            }
            
            registeredUserService.save(user);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Failed to update user penalty points: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while penalizing the user: " + e.getMessage(), e);
        }
    }

    public boolean existsByUserAndCompany(RegisteredUser registeredUser, Company company){
        List<Reservation> reservations = reservationRepository.findAllByUserIdAndStatus(registeredUser.getId(), Completed);
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

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetPenaltyPoints() {
        List<RegisteredUser> users = registeredUserService.findAll();
        users.forEach(user -> user.setPenaltyPoints(0));
        for (RegisteredUser ru : users){
            registeredUserService.save(ru);
        }
    }

    public List<Reservation> findAll(RegisteredUser registeredUser){
        return reservationRepository.findAllByUser(registeredUser);
    }

    public List<String> getQRcodes(String status) throws IllegalArgumentException, NullPointerException{

        List<String> qrCodes = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RegisteredUser registeredUser = registeredUserService.findByEmail(authentication.getName());

        //List<Reservation> reservations = findAll(registeredUser);
        List<Reservation> reservations = new ArrayList<>();

            try{
                reservations = reservationRepository.findAllByUserAndStatus(registeredUser, ReservationStatus.valueOf(status));
            }catch (NullPointerException e1){
                reservations = reservationRepository.findAllByUser(registeredUser);
            } catch (IllegalArgumentException e2){
                throw new IllegalArgumentException("Invalid status value: "+ status);

            }
            //reservations = reservationRepository.findAllByUser(registeredUser);


        for(Reservation r:reservations){
            String qrCodeContent = emailService.getQRCodeContent(r);
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(qrCodeContent, 200, 200);
            String encodedImage = Base64.getEncoder().encodeToString(qrCodeImage);
            qrCodes.add(encodedImage);
        }

        return qrCodes;
    }

    public LocalDateTime convertDateToLocalDateTime(Date date) {
        // First convert Date to Instant
        Instant instant = date.toInstant();

        // Then convert Instant to LocalDateTime
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Now extract LocalTime
        return localDateTime;
    }

    public List<DateAndAdminDTO> showAvailableAppointmentsOnDate(LocalDate date, Integer companyId){

        List<DateAndAdminDTO> availableSlots = new ArrayList<>();
        Company company = companyService.findBy(companyId);
        LocalTime start = convertDateToLocalDateTime(company.getOpeningTime()).toLocalTime();
        LocalTime end = convertDateToLocalDateTime(company.getClosingTime()).toLocalTime();


        while(start.plusHours(1).isBefore(end)){
            DateAndAdminDTO dateAndAdminDTO = new DateAndAdminDTO();
            List<Integer> admins = new ArrayList<>();
            for(CompanyAdmin companyAdmin:company.getCompanyAdmins()){
                if(isAdminFree(companyAdmin, date, start, start.plusHours(1))){
                    admins.add(companyAdmin.getId());
                    dateAndAdminDTO.setAvailableAdminId(admins);
                }
            }

            if(admins.size()!=0){
                LocalDateTime dateTime = LocalDateTime.of(date, start);
                dateAndAdminDTO.setDateSlot(dateTime);
                availableSlots.add(dateAndAdminDTO);
            }

            start = start.plusHours(1);
        }

        return availableSlots;
    }

    private boolean isAdminFree(CompanyAdmin admin, LocalDate date, LocalTime startTime, LocalTime endTime){

        List<Reservation> reservations = getReservationsByAdminAndDate(admin, date);//dodati da vraca samo statusom Created i Ready
        return reservations.stream().noneMatch(reservation ->
                convertDateToLocalDateTime(reservation.getStartingDate()).toLocalTime().isBefore(endTime) && convertDateToLocalDateTime(reservation.getEndTime()).toLocalTime().isAfter(startTime));
    }

    public List<Reservation> getReservationsByAdminAndDate(CompanyAdmin admin, LocalDate date) {
        ZoneId systemTimeZone = ZoneId.systemDefault();
        Date startOfDay = Date.from(date.atStartOfDay(systemTimeZone).toInstant());
        Date endOfDay = Date.from(date.plusDays(1).atStartOfDay(systemTimeZone).toInstant());

        return reservationRepository.findAllByAdminAndDayAndCreatedOrReady(admin, startOfDay, endOfDay);
    }

    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createReservationByExtraOrdinaryAppointment(ReservationByExtraOrdinaryAppointmentDTO dto)
            throws DataAccessException, ClassNotFoundException, MailException, MessagingException, OptimisticLockException {

        RegisteredUser registeredUser = (RegisteredUser) getUserCredentinals();

        if (registeredUser.getPenaltyPoints() >= 3) {
            throw new RuntimeException("The user is forbidden from making a new reservation this month due to many cancellations!");
        }

        Date parsedDate;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            parsedDate = formatter.parse(dto.getSelectedDateTime());
        } catch (ParseException e) {
            throw new RuntimeException("Wrong date format");
        }

        //CompanyAdmin admin = companyAdminService.findBy(dto.getAvailableAdminId());
        
        // Check if the slot is still available
        Integer adminIdentificator = null;
        for(Integer adminId: dto.getAvailableAdminId()){
            CompanyAdmin admin = companyAdminService.findBy(adminId);
            if (isSlotAvailable(admin, parsedDate)) {
                adminIdentificator = adminId;
                break;
            }
        }

        if(adminIdentificator==null){
            throw new ReservationConflictException("The selected appointment slot is no longer available.");
        }


        Reservation reservation = new Reservation();
        reservation.setStartingDate(parsedDate);
        reservation.setDurationMinutes(60);
        reservation.setAdmin(companyAdminService.findBy(adminIdentificator));

        updateReservationDetails(reservation, registeredUser);

        for (ReservationItemDTO item : dto.getReservationItems()) {
            Equipment equipment = equipmentService.findBy(item.getEquipmentId());

            if (equipment == null) {
                throw new ClassNotFoundException("Equipment with ID " + item.getEquipmentId() + " not found");
            }

            if (equipment.getAvailableQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("The chosen quantity of equipment with id " + item.getEquipmentId() + " is larger than the available quantity");
            }

            addReservationItem(reservation, item, equipment);
            setAvailableQuantity(item, equipment);
            equipmentService.save(equipment);
        }

        for (ReservationItem reservationItem : reservation.getItems()) {
            reservationItemService.save(reservationItem);
        }

        applyDiscountAndUpdateLoyalty(reservation, registeredUser);
        registeredUserService.save(registeredUser);

        try {
            reservationRepository.save(reservation);
        } catch (OptimisticLockingFailureException e) {
            throw new ReservationConflictException("The reservation could not be created due to a conflict. Please try again.");
        }

        emailService.sendReservationQRCodeASync(registeredUser, reservation);
    }

    private boolean isSlotAvailable(CompanyAdmin admin, Date startDate) {
        Date endDate = new Date(startDate.getTime() + 60 * 60 * 1000); // 60 minutes later
        List<Reservation> conflictingReservations = reservationRepository.findConflictingReservations(startDate, endDate, admin.getId());
        return conflictingReservations.isEmpty();
    }

    public List<UserDTO> getAllUsersByCompany(Integer adminId) {
//        List<Reservation> reservations = reservationRepository.findAllByCompanyAd
        List<RegisteredUser> users=  reservationRepository.findUniqueUsersByAdminId(adminId);
        List<UserDTO> dtos = new ArrayList<>();
        for (RegisteredUser user: users) {
            UserDTO dto = new UserDTO();
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dtos.add(dto);
        }

        return  dtos;
    }

    public Boolean checkIfTimeWindowPassed(Reservation reservation){
        Date currentTime = new Date();
        Date startingDate = reservation.getStartingDate(); // Assuming this returns a Date
        int durationMinutes = reservation.getDurationMinutes();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startingDate);
        calendar.add(Calendar.MINUTE, durationMinutes);

        Date endDate = calendar.getTime(); // This is the starting date plus the duration in minutes
//        System.out.println(startingDate);
//        System.out.println(durationMinutes);
//        System.out.println(endDate);
        if(currentTime.after(endDate)){
            return true;
        }
        return false;
    }
    public Boolean checkIfBeforeStartingTime(Reservation reservation){
        List<Integer> usersForPenalties = new ArrayList<>();

        // Get the current time and compare with the reservation starting time
        Date currentTime =new Date();
        Date startingDateTime = reservation.getStartingDate(); // Assuming this is a LocalDateTime

        if (currentTime.before(startingDateTime)) {
            // If current time is before the starting date, send a custom alert
            return true;
        }
        return false;

    }
    public List<ReservationDTO> getAvailableReservations(Integer id) {

        List<Reservation> reservations = reservationRepository.findByAdminIdAndStatus(id, ReservationStatus.Ready);

        List<Reservation> reservationsForUpdate = new ArrayList<>();
        List<ReservationDTO> dtos = new ArrayList<>();
        Date currentTime = new Date();
        List<Integer> usersForPenalties = new ArrayList<>();

        for (Reservation reservation: reservations) {

//            Date startingDate = reservation.getStartingDate(); // Assuming this returns a Date
//            int durationMinutes = reservation.getDurationMinutes();
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(startingDate);
//            calendar.add(Calendar.MINUTE, durationMinutes);
//
//            Date endDate = calendar.getTime(); // This is the starting date plus the duration in minutes
//            System.out.println(startingDate);
//            System.out.println(durationMinutes);
//            System.out.println(endDate);
            if(checkIfTimeWindowPassed(reservation)){
                usersForPenalties.add(reservation.getUser().getId());
                reservation.setStatus(Cancelled);
                reservationsForUpdate.add(reservation);

            }else{
                ReservationDTO dto = new ReservationDTO(reservation);
                dtos.add(dto);
            }
        }
        registeredUserService.penalizeUsers(usersForPenalties);

        reservationRepository.saveAll(reservationsForUpdate);

        return dtos;
    }
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Boolean markReservationCompleted(Integer id) {


        Reservation reservation = reservationRepository.findReservationById(id);
        List<Integer> usersForPenalties = new ArrayList<>();
        if(checkIfBeforeStartingTime(reservation)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The reservation cannot be marked as completed because it hasn't started yet.");

        }
        if(checkIfTimeWindowPassed(reservation)){
            reservation.setStatus(Cancelled);
            reservationRepository.save(reservation);
            usersForPenalties.add(reservation.getUser().getId());
            registeredUserService.penalizeUsers(usersForPenalties);

            return true;
        }
//        List<Reservation> companyReservations = reservationRepository.findAllReservationsByAdminId(id, Ready);
//        System.out.println("dobavio i zakljucao:");
//        for (Reservation r:
//                companyReservations) {
//            System.out.println("id");
//            System.out.println(reservation.id);
//        }
        reservationRepository.lockAllReservations();
        // Check and lock equipment
        List<Equipment> equipmentList;
        try {
            equipmentList = fetchAndLockEquipment(reservation);
        } catch (InsufficientEquipmentException e) {
            // Notify the front end about the insufficient equipment
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (CommandAcceptanceException e) {
            // Handle the CommandAcceptanceException
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions that may occur
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage());
        }
        try{
            reservation.setStatus(Completed);
            reservationRepository.save(reservation);
        }catch (PessimisticLockingFailureException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred 1: " + e.getMessage());
        }catch (PessimisticLockException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: 2 " + e.getMessage());
        }catch (Exception e) {
            // Handle any other exceptions that may occur
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred 3: " + e.getMessage());
        }

    equipmentService.saveAll(equipmentList);

    return true;
}
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    private List<Equipment> fetchAndLockEquipment(Reservation reservation) {
//        Set<ReservationItem> items = reservation.getItems();
//        List<Equipment> equipmentList = new ArrayList<>();
//
//        for (ReservationItem item : items) {
//            Equipment equipment = equipmentService.findByIdAndLock(item.getEquipment().getId()); // Fetch and lock
//            if (equipment != null) {
//                int updatedQuantity = equipment.getQuantity() - item.getQuantity();
//                if (updatedQuantity < 0) {
//                    // Throw an exception if there's not enough equipment
//                    throw new InsufficientEquipmentException("Not enough " + equipment.getName() + " in storage. Requested: " + item.getQuantity() + ", Available: " + equipment.getQuantity());
//                }
//                equipment.setQuantity(updatedQuantity); // Set the new quantity
//                equipmentList.add(equipment);
//            }
//        }
//        return equipmentList;
//    }
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    private List<Equipment> fetchAndLockEquipment(Reservation reservation) {
        Set<ReservationItem> items = reservation.getItems();
        List<Equipment> equipmentList = new ArrayList<>();

        try {
            for (ReservationItem item : items) {
                Equipment equipment = equipmentService.findByIdAndLock(item.getEquipment().getId()); // Fetch and lock
                if (equipment != null) {
                    int updatedQuantity = equipment.getQuantity() - item.getQuantity();
                    if (updatedQuantity < 0) {
                        // Throw an exception if there's not enough equipment
                        throw new InsufficientEquipmentException("Not enough " + equipment.getName() + " in storage. Requested: " + item.getQuantity() + ", Available: " + equipment.getQuantity());
                    }
                    equipment.setQuantity(updatedQuantity); // Set the new quantity
                    equipmentList.add(equipment);
                }
            }
            return equipmentList;

        } catch (PessimisticLockingFailureException ex) {
            // Handle the lock failure
            throw new CustomRetryableException("Unable to acquire lock on equipment. Please try again.");
        }
    }

    static void spin(long delay_in_milliseconds) {
        long delay_in_nanoseconds = delay_in_milliseconds*1000000;
        long start_time = System.nanoTime();
        while (true) {
            long now = System.nanoTime();
            long time_spent_sleeping_thus_far = now - start_time;
            if (time_spent_sleeping_thus_far >= delay_in_nanoseconds) {
                break;
            }
        }
    }




}
