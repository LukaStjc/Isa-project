package rs.ac.uns.ftn.informatika.jpa.dto;


import lombok.*;

import java.util.List;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class RateCompanyDTO {

    private Integer company;
    private int score;
    private String feedback;
    private List<String> reasons;



}
