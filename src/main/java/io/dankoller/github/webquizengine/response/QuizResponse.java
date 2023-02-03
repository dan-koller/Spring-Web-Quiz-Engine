package io.dankoller.github.webquizengine.response;

import lombok.*;

import javax.validation.constraints.NotEmpty;

/**
 * This class is used to return a response to the user when they request a quiz.
 */
@Data
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
