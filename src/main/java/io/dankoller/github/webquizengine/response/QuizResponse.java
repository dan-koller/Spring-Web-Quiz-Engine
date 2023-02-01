package io.dankoller.github.webquizengine.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * This class is used to return a response to the user when they request a quiz.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    @NotEmpty
    private int id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;
    @NotEmpty
    private String[] options;
}
