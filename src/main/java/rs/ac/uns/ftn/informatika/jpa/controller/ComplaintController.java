package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.jpa.dto.ComplaintDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Complaint;
import rs.ac.uns.ftn.informatika.jpa.service.ComplaintService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    //TODO dodati id admina sistema koji je odradio reply
    @PutMapping(value = "/reply/{id}")
    public ResponseEntity<Void> updateComplaintByReply(@PathVariable Integer id, @RequestBody ComplaintDTO complaintDTO){
        Optional<Complaint> optionalComplaint = complaintService.findById(id);

        if(!optionalComplaint.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Complaint complaint = optionalComplaint.get();
        complaint.setReply(complaintDTO.getReply());

        try{
            complaintService.save(complaint);
        }
        catch (RuntimeException e){
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
