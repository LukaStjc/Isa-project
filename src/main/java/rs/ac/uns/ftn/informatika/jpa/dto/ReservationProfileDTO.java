package rs.ac.uns.ftn.informatika.jpa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import rs.ac.uns.ftn.informatika.jpa.model.ReservationItem;

import java.util.*;

@Getter @Setter @AllArgsConstructor
public class ReservationProfileDTO {

    private Integer id;
    private Double totalSum;
    private String adminName;
    private String adminLastName;
    private int durationMinutes;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Budapest")
    private Date startingDate;
    private List<ReservationItemProfileDTO> items;

    public ReservationProfileDTO(){
        items = new ArrayList<>();
    }



}
