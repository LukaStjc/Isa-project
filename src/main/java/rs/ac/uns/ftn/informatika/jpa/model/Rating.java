package rs.ac.uns.ftn.informatika.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private RegisteredUser user;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private int score;

    @ElementCollection
    @CollectionTable(name = "rating_reasons", joinColumns = @JoinColumn(name = "rating_id"))
    @Column(name = "reason")
    private List<String> reasons;

    @Column(nullable = true, length = 500)
    private String feedback;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Rating(){
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setScore(int score) {
        this.score = score;
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }




}
