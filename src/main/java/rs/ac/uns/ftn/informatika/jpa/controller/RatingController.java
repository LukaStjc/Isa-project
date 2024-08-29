package rs.ac.uns.ftn.informatika.jpa.controller;


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

    @PreAuthorize("hasRole('REGISTERED_USER')")
    @PostMapping("/rate")
    public ResponseEntity<RatingDTO> rateCompany(@RequestBody RateCompanyDTO rateCompanyDTO){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RegisteredUser user = (RegisteredUser) authentication.getPrincipal();

        Rating rating = ratingService.addOrUpdateRating(user, rateCompanyDTO);
        RatingDTO dto = new RatingDTO(rating);

        return new ResponseEntity<>(dto, HttpStatus.OK);


    }

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
