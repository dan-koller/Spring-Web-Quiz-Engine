package io.dankoller.github.webquizengine.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class QuizRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String text;
    @NotEmpty
    @Size(min = 2) // should at least have 2 options
    private String[] options;
    private int[] answer; // can be null (= no correct answer)

    // Validate the json request
    public QuizRequest(@JsonProperty(required = true, value = "title") String title,
                       @JsonProperty(required = true, value = "text") String text,
                       @JsonProperty(required = true, value = "options") String[] options,
                       @JsonProperty(required = false, value = "answer") int[] answer) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
    }
}
