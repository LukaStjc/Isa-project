package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.informatika.jpa.dto.ComplaintDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Complaint;
import rs.ac.uns.ftn.informatika.jpa.service.ComplaintService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/complaints")
@CrossOrigin(origins = "http://localhost:3000")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;


    @GetMapping
    public ResponseEntity<List<ComplaintDTO>> getAll(){
        List<Complaint> complaints = complaintService.findUnansweredComplaints();

        List<ComplaintDTO> complaintDTOS = new ArrayList<>();
        for(Complaint c : complaints){
            complaintDTOS.add(new ComplaintDTO(c));
        }

        return new ResponseEntity<>(complaintDTOS, HttpStatus.OK);
    }
}
