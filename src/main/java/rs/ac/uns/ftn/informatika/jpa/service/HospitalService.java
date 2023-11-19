package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.Hospital;
import rs.ac.uns.ftn.informatika.jpa.repository.HospitalRepository;

@Service
public class HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    Hospital getOne(int id) {
        return hospitalRepository.getOne(id);
    }
}
