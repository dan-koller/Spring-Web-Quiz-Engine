package io.dankoller.github.webquizengine.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

// Object to return to the user (without the correct answer from the quiz object)
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
