package io.dankoller.github.webquizengine.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * This class represents a request to create a new quiz.
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class QuizRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String text;
    @NotEmpty
    @Size(min = 2)
    private String[] options; // should at least have 2 options
    private int[] answer; // can be null (= no correct answer)

    // Constructor for Jackson to deserialize JSON to Java object
    public QuizRequest(@JsonProperty(required = true, value = "title") String title,
                       @JsonProperty(required = true, value = "text") String text,
                       @JsonProperty(required = true, value = "options") String[] options,
                       @JsonProperty(value = "answer") int[] answer) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
    }
}
