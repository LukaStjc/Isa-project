package rs.ac.uns.ftn.informatika.jpa.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.RateCompanyDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.RatingDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Rating;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.service.CompanyService;
import rs.ac.uns.ftn.informatika.jpa.service.RatingService;
import rs.ac.uns.ftn.informatika.jpa.service.RegisteredUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Ratings", description = "Endpoints for company ratings")
@RestController
@RequestMapping(value = "/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;


    @Autowired
    private CompanyService companyService;

    @GetMapping
    @Transactional
    public ResponseEntity<List<RatingDTO>> getRatings() {



        List<Rating> ratings = ratingService.findAll();
        List<RatingDTO> ratingDTOs = ratings.stream().map(RatingDTO::new).collect(Collectors.toList());

        return new ResponseEntity<>(ratingDTOs, HttpStatus.OK);
    }

    @Operation(summary = "Rate a company", description = "Allows a registered user to rate a company.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RatingDTO.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized if the user is not authenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden if the user does not have sufficient rights")
    @PreAuthorize("hasRole('REGISTERED_USER')")
    @PostMapping("/rate")
    public ResponseEntity<RatingDTO> rateCompany(@RequestBody RateCompanyDTO rateCompanyDTO){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RegisteredUser user = (RegisteredUser) authentication.getPrincipal();

        Rating rating = ratingService.addOrUpdateRating(user, rateCompanyDTO);
        RatingDTO dto = new RatingDTO(rating);

        return new ResponseEntity<>(dto, HttpStatus.OK);


    }

    @Operation(summary = "Check if a user can rate a company", description = "Checks if the logged-in registered user is allowed to rate a specified company.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Returns true if the user can rate, false otherwise",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description = "Company not found or other error")
    @ApiResponse(responseCode = "401", description = "Unauthorized if the user is not authenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden if the user does not have the role REGISTERED_USER")
    @PreAuthorize("hasRole('REGISTERED_USER')")
    @GetMapping("/canUserRate/{companyId}")
    public ResponseEntity<Boolean> canUserRate(@PathVariable Integer companyId){

        Boolean canUserRate;

        try {
            canUserRate = ratingService.canUserRate(companyId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(canUserRate, HttpStatus.OK);

    }


    @Operation(summary = "Find a rating for a company", description = "Retrieves the rating for a company specified by its ID if it exists.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Successfully found the rating",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RatingDTO.class)))
    @ApiResponse(responseCode = "404", description = "Rating not found for the specified company", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized if the user is not authenticated", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden if the user does not have the role REGISTERED_USER", content = @Content)
    @PreAuthorize("hasRole('REGISTERED_USER')")
    @GetMapping("/findRating/{companyId}")
    public ResponseEntity<RatingDTO> findRating(@PathVariable Integer companyId){

        Optional<Rating> existingRating = ratingService.findByCompany(companyId);
        if(existingRating.isPresent()){
            RatingDTO dto = new RatingDTO(existingRating.get());
            return new ResponseEntity(dto, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
