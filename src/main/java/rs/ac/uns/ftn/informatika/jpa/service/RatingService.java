package rs.ac.uns.ftn.informatika.jpa.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.RateCompanyDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Rating;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
import rs.ac.uns.ftn.informatika.jpa.repository.RatingRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CompanyService companyService;

    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    public Rating findById(Integer id) { return  ratingRepository.findById(id).orElseGet(null); }

    public Rating save(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Rating addOrUpdateRating(RegisteredUser registeredUser, RateCompanyDTO rateCompanyDTO){
        Company c = companyService.findBy(rateCompanyDTO.getCompany());

        //proveravamo da li user ima rezervaciju
        boolean hasReservations = reservationService.existsByUserAndCompany(registeredUser, c);
        if (!hasReservations) {
            throw new IllegalArgumentException("User must have at least one reservation to rate the company."); //ispise ovo samo kako handlovati bude 500eror
        }

        //proveravamo da li user ima vec ocenu za ovu kompaniju

        Optional<Rating> existingRating = ratingRepository.findByUserAndCompany(registeredUser, c);

        Rating rating;
        if(existingRating.isPresent()){
            //azuriraj
            rating = existingRating.get();
            rating.setScore(rateCompanyDTO.getScore());
            rating.setFeedback(rateCompanyDTO.getFeedback());
        }else{
            //novi rating
            rating = new Rating();
            rating.setUser(registeredUser);
            rating.setCompany(c);
            rating.setScore(rateCompanyDTO.getScore());
            rating.setFeedback(rateCompanyDTO.getFeedback());
        }

        return save(rating);

    }


}
