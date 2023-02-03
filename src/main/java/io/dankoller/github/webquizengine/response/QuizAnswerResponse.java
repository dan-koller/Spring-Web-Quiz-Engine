package io.dankoller.github.webquizengine.response;

import lombok.*;

/**
 * This class is used to return a response to the user when they answer a quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerResponse {
    private boolean success;
    private String feedback;
}
