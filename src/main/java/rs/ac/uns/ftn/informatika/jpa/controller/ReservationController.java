package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.*;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationByPremadeAppointmentDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationPremadeDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.UserDTO;
import rs.ac.uns.ftn.informatika.jpa.exception.CustomRetryableException;
import rs.ac.uns.ftn.informatika.jpa.exception.ReservationConflictException;
import rs.ac.uns.ftn.informatika.jpa.exception.ReservationLockedException;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.model.Reservation;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyAdminService;
import rs.ac.uns.ftn.informatika.jpa.service.RegisteredUserService;
import rs.ac.uns.ftn.informatika.jpa.service.ReservationService;

import javax.mail.MessagingException;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Tag(name = "Reservation Management Controllers", description = "Manages all operations related to reservations.")
@RestController
@RequestMapping(value = "api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    @Autowired
    ReservationService reservationService;
    @Autowired
    CompanyAdminService companyAdminService;
    @Autowired
    private RegisteredUserService registeredUserService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<List<ReservationDTO>> getAllByDate
            (@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date date,
             @RequestParam("week") int showWeek,
             @PathVariable Integer id) {
        //TODO dodati da se prikazuju samo one rezervacije koje su vezane za tog admina kompanije

        List<ReservationDTO> reservationDTOS = reservationService.getAllByDate(date, showWeek, id);

        return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/month-overview/{id}")
    public ResponseEntity<List<Integer>> getReservedDaysInMonth
            (@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd") Date date,
             @PathVariable Integer id) {

        List<Integer> days = reservationService.getAllByMonthAndYear(date, id);

        return new ResponseEntity<>(days, HttpStatus.OK);
    }


    @Operation(summary = "Create a reservation based on a premade appointment!",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation created successfully!",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "You made bad request, check data!",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!",
                    content = @Content)
    })
    @PreAuthorize("hasRole('REGISTERED_USER')")
    @PostMapping(consumes = "application/json", value = "/create-by-premade-appointment")
    public ResponseEntity createReservationByPremadeAppointment(@RequestBody ReservationByPremadeAppointmentDTO reservation) throws Exception
    {
        try {
            reservationService.updateReservationByPremadeAppointment(reservation);
        } catch (OptimisticLockException | OptimisticLockingFailureException | StaleStateException e) {
            return new ResponseEntity<>("Sorry, we're unable to confirm the availability of the selected appointment or equipment at the moment. Please try again.", HttpStatus.CONFLICT);
        } catch (RuntimeException | MessagingException | ClassNotFoundException  e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "Cancel a reservation!",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully!",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!",
                    content = @Content)
    })
    @PreAuthorize("hasRole('REGISTERED_USER')")
    @PostMapping(value = "/cancel/{id}")
    public ResponseEntity cancelReservation(@PathVariable Integer id) throws Exception {

        try {
            reservationService.cancelReservation(id);
        } catch (OptimisticLockException | OptimisticLockingFailureException | StaleStateException e) {
            return new ResponseEntity<>("Sorry, we're unable to confirm the availability of the equipment after cancellation. Please try again later.", HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "Create a new reservation", description = "Creates a new reservation for a company admin based on the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation created successfully", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "409", description = "Reservation conflict", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createReservation(@RequestBody ReservationPremadeDTO dto) {
        System.out.println(dto.getAdminId());
        System.out.println(dto.getSelectedDateTime());

        CompanyAdmin admin = companyAdminService.findBy(Integer.parseInt(dto.getAdminId()));
        Company company = admin.getCompany(); // Assuming admin is linked to the company

        String dateString = dto.getSelectedDateTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Date openingTime = company.getOpeningTime();
        Date closingTime = company.getClosingTime();

        // Convert Date to LocalTime
        LocalTime openingLocalTime = openingTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime closingLocalTime = closingTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime selectedLocalTime = parsedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        LocalTime endTime = selectedLocalTime.plusMinutes(Integer.parseInt(dto.getDurationMinutes()));
        String errorMessage = null;

        // Check if the selected date is within the company's working hours
        if (selectedLocalTime.isBefore(openingLocalTime) || endTime.isAfter(closingLocalTime)) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String formattedOpeningTime = timeFormat.format(openingTime);
            String formattedClosingTime = timeFormat.format(closingTime);


            if (selectedLocalTime.isBefore(openingLocalTime) || selectedLocalTime.isAfter(closingLocalTime)) {
                // Selected time is outside working hours
                errorMessage = String.format(
                        "Selected time is outside company working hours. Working hours are from %s to %s.",
                        formattedOpeningTime, formattedClosingTime
                );
            } else if (endTime.isAfter(closingLocalTime)) {
                // Duration exceeds working hours
                errorMessage = String.format(
                        "The selected time plus duration exceeds company working hours. Please select an earlier time. Working hours are from %s to %s.",
                        formattedOpeningTime, formattedClosingTime
                );
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        Reservation reservation = new Reservation();
        reservation.setDurationMinutes(Integer.parseInt(dto.getDurationMinutes()));
        reservation.setStartingDate(parsedDate);
        reservation.setAdmin(admin);

        try {
            reservationService.save(reservation);
            return ResponseEntity.ok("Reservation created successfully.");
        } catch (ReservationConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the reservation.");
        }
    }

    @Operation(summary = "Retrieve completed reservations from your history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of your history completed reservations!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationProfileDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reservations not found!", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!", content = @Content)
    })
    @PreAuthorize("hasRole('REGISTERED_USER')")
    @GetMapping("/history-completed")
    public ResponseEntity<List<ReservationProfileDTO>> findAllCompleted(
            @RequestParam(required = false, defaultValue = "startingDate") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection){

        List<ReservationProfileDTO> dtos = new ArrayList<>();

        try {
            dtos = reservationService.findAllCompleted(sortBy, sortDirection);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(dtos, HttpStatus.OK);

    }


    @Operation(summary = "Retrieve reservations that are ready to be collected")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of your upcoming reservations!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationProfileDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reservations not found!", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Login with appropriate privileges is required!", content = @Content)
    })
    @PreAuthorize("hasRole('REGISTERED_USER')")
    @GetMapping("/ready")
    public ResponseEntity<List<ReservationProfileDTO>> findAllReady(){

        List<ReservationProfileDTO> dtos = new ArrayList<>();

        try {
            dtos = reservationService.findAllReady();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(dtos, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('REGISTERED_USER')")
    @GetMapping("/showAvailableAppointmentsOnDate")
    public ResponseEntity showAvailableAppointmentsOnDate(
            @RequestParam String dateString,
            @RequestParam Integer companyId
            ){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(dateString);
            List<DateAndAdminDTO> datesAndAdmin = new ArrayList<>();
            datesAndAdmin = reservationService.showAvailableAppointmentsOnDate(date, companyId);

            return ResponseEntity.ok(datesAndAdmin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("GRESKA!");
        }



    }

    @PreAuthorize("hasRole('REGISTERED_USER')")
    @PostMapping(consumes = "application/json", value = "/create-by-extraordinary-appointment")
    public ResponseEntity createReservationByExtraOrdinaryAppointment(@RequestBody ReservationByExtraOrdinaryAppointmentDTO dto) throws Exception
    {
        try {
            reservationService.createReservationByExtraOrdinaryAppointment(dto);
        } catch (OptimisticLockException | OptimisticLockingFailureException | StaleStateException e) {
            return new ResponseEntity<>("This slot has just been booked by another user. Please try another slot.", HttpStatus.CONFLICT);
        } catch (RuntimeException | MessagingException | ClassNotFoundException  e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }







    @GetMapping("/get-users/{id}")
    public ResponseEntity<List<UserDTO>> getAllUsersByCompanyAdmin(@PathVariable Integer id){

        return new ResponseEntity<>(reservationService.getAllUsersByCompany(id), HttpStatus.OK);
    }

    @GetMapping("/available/{id}")
    public ResponseEntity<List<ReservationDTO>> getAvailableReservations(@PathVariable Integer id){

        return new ResponseEntity<>(reservationService.getAvailableReservations(id), HttpStatus.OK);
    }
    @PutMapping("/mark-completed/{id}")
    public ResponseEntity<String> markReservationCompleted(@PathVariable Integer id) {
        try {
            reservationService.markReservationCompleted(id);
            return new ResponseEntity<>("Reservation marked as completed.", HttpStatus.OK);
        } catch (PessimisticLockingFailureException e) {
            return new ResponseEntity<>("Reservation is locked by another transaction. Please try again.", HttpStatus.CONFLICT);
        } catch (CustomRetryableException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (ReservationLockedException e) {
            return new ResponseEntity<>("Reservation is locked. Please try again.", HttpStatus.CONFLICT);
        }
    }


}
