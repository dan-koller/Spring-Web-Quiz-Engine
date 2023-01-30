package io.dankoller.github.webquizengine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dankoller.github.webquizengine.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

/**
 * This class represents a completed quiz. The id field is the id of the quiz that was completed.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class CompletedQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private int completionId;

    @Column
    @NotNull
    private int id;

    @Column
    @NotNull
    @PastOrPresent
    private LocalDateTime completedAt;

    @NotNull
    @ManyToOne
    @JsonIgnore
    private User user;

    public CompletedQuiz(int id, LocalDateTime completedAt, User user) {
        this.id = id;
        this.completedAt = completedAt;
        this.user = user;
    }
}
