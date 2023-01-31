package io.dankoller.github.webquizengine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dankoller.github.webquizengine.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 * This class represents a quiz. Completed quizzes are stored in the CompletedQuiz class.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    private String title;
    @Column
    private String text;
    @Column
    private String[] options;
    @Column
    @JsonIgnore
    private int[] answer;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User author; // The AUTHOR_ID is correlated to the USER_ID in the COMPLETED_QUIZ table.

    public Quiz(String title, String text, String[] options, int[] answer, User author) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
        this.author = author;
    }
}
