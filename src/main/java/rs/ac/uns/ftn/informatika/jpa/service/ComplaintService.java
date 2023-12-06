package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.Complaint;
import rs.ac.uns.ftn.informatika.jpa.repository.ComplaintRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;


    public List<Complaint> findUnansweredComplaints() {
        return complaintRepository.findUnansweredComplaints();
    }

    public Optional<Complaint> findById(Integer id){
        return complaintRepository.findById(id);
    }

    public void save(Complaint complaint) {
        complaintRepository.save(complaint);
    }
}
