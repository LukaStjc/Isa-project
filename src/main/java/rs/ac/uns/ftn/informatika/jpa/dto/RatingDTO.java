package rs.ac.uns.ftn.informatika.jpa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.informatika.jpa.model.Rating;

@Getter@Setter@NoArgsConstructor
public class RatingDTO {

    private Integer id;
    private Integer userId;
    private Integer companyId;
    private int score;
    private String feedback;

    public RatingDTO(Rating rating){
        this.id = rating.getId();
        this.userId = rating.getUser().getId();
        this.companyId = rating.getCompany().getId();
        this.score = rating.getScore();
        this.feedback = rating.getFeedback();
    }



}
