package rs.ac.uns.ftn.informatika.jpa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.informatika.jpa.model.Rating;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter@NoArgsConstructor
public class RatingDTO {

    private Integer id;
    private Integer userId;
    private Integer companyId;
    private int score;
    private String feedback;
    private List<String> reasons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RatingDTO(Rating rating){
        this.id = rating.getId();
        this.userId = rating.getUser().getId();
        this.companyId = rating.getCompany().getId();
        this.score = rating.getScore();
        this.feedback = rating.getFeedback();
        this.reasons = new ArrayList<>(rating.getReasons());
        this.createdAt = rating.getCreatedAt();
        this.updatedAt = rating.getUpdatedAt();
    }



}
