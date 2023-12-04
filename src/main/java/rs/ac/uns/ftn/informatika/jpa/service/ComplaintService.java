package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.repository.ComplaintRepository;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;


}
