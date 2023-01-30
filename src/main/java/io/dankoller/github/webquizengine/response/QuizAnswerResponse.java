package io.dankoller.github.webquizengine.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is used to return a response to the user when they answer a quiz.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerResponse {
    private boolean success;
    private String feedback;
}
