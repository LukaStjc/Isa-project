//package rs.ac.uns.ftn.informatika.jpa.configuration;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;
//import rs.ac.uns.ftn.informatika.jpa.model.User;
//import rs.ac.uns.ftn.informatika.jpa.repository.HospitalRepository;
//import rs.ac.uns.ftn.informatika.jpa.repository.LocationRepository;
//import rs.ac.uns.ftn.informatika.jpa.repository.LoyaltyProgramRepository;
//import rs.ac.uns.ftn.informatika.jpa.repository.RegisteredUserRepository;
//
//
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Configuration
//public class DatabaseConfiguration {
//    @Autowired
//    RegisteredUserRepository registeredUserRepository;
//
//    @Autowired
//    LoyaltyProgramRepository loyaltyProgramRepository;
//
//    @Autowired
//    HospitalRepository hospitalRepository;
//
//    @Autowired
//    LocationRepository locationRepository;
//
//    @Bean
//    public boolean instantiate(){
//
//        RegisteredUser registeredUser = new RegisteredUser("peraperic123@gmail.com", "Era", "Eric", "era123", "0627711549", "trgovac");
//        registeredUser.setLoyaltyProgram(loyaltyProgramRepository.getOne(0)); // bronze loyalty program
//        registeredUser.setHospital(hospitalRepository.getOne(1));
//        registeredUser.setLocation(locationRepository.getOne(1));
//        registeredUserRepository.save(registeredUser);
//
//        return true;
//    }
//}
//
