package rs.ac.uns.ftn.informatika.jpa.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private RegisteredUserService registeredUserService;

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
            throw new IllegalArgumentException("User must have at least one completed reservation to rate the company."); //ispise ovo samo handlovati bude 500eror
        }

        //proveravamo da li user ima vec ocenu za ovu kompaniju

        Optional<Rating> existingRating = ratingRepository.findByUserAndCompany(registeredUser, c);

        Rating rating;
        if(existingRating.isPresent()){
            //azuriraj
            rating = existingRating.get();
            rating.setScore(rateCompanyDTO.getScore());
            rating.setFeedback(rateCompanyDTO.getFeedback());
            rating.setReasons(rateCompanyDTO.getReasons());
        }else{
            //novi rating
            rating = new Rating();
            rating.setUser(registeredUser);
            rating.setCompany(c);
            rating.setScore(rateCompanyDTO.getScore());
            rating.setFeedback(rateCompanyDTO.getFeedback());
            rating.setReasons(rateCompanyDTO.getReasons());
        }

        save(rating);

        companyService.updateAverageScore(c);

        return rating;

    }


    public Boolean canUserRate(Integer companyId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RegisteredUser registeredUser = registeredUserService.findByEmail(authentication.getName());

        Company company = companyService.findBy(companyId);

        return reservationService.existsByUserAndCompany(registeredUser, company);
    }

    public Optional<Rating> findByCompany(Integer companyId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RegisteredUser registeredUser = registeredUserService.findByEmail(authentication.getName());

        Company company = companyService.findBy(companyId);

        Optional<Rating> existingRating = ratingRepository.findByUserAndCompany(registeredUser, company);
        return existingRating;



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
