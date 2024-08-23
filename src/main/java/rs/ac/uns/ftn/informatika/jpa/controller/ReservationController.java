package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationByPremadeAppointmentDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.ReservationPremadeDTO;
import rs.ac.uns.ftn.informatika.jpa.model.CompanyAdmin;
import rs.ac.uns.ftn.informatika.jpa.model.Reservation;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyAdminService;
import rs.ac.uns.ftn.informatika.jpa.service.ReservationService;

import javax.mail.MessagingException;
import javax.persistence.OptimisticLockException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Tag(name = "Reservation Management Controllers", description = "Manages all operations related to reservations.")
@RestController
@RequestMapping(value = "api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    @Autowired
    ReservationService reservationService;
    @Autowired
    CompanyAdminService companyAdminService;

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
    @PutMapping(value = "cancel/{id}")
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


    @PostMapping(consumes = "application/json")
    public ResponseEntity<ReservationPremadeDTO> createReservation(@RequestBody ReservationPremadeDTO dto){
        System.out.println(dto.getAdminId());
        System.out.println(dto.getSelectedDateTime());
        //ovo nece trebati kada se odradi login, za sada je ovako
        CompanyAdmin admin = companyAdminService.findBy(Integer.parseInt(dto.getAdminId()));

        String dateString = dto.getSelectedDateTime(); // Assuming dto.getStartingTime() returns "2023-12-16T03:00:00.000Z"

        // Parse the date string into a java.util.Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the timezone to UTC if your date is in UTC
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Reservation reservation = new Reservation();
        reservation.setDurationMinutes(Integer.parseInt(dto.getDurationMinutes()));
        reservation.setStartingDate(parsedDate); // Set the parsed date
        reservation.setAdmin(admin);
        try{
            reservationService.save(reservation);
        } catch(RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<ReservationPremadeDTO>(dto, HttpStatus.OK);

    }



}
