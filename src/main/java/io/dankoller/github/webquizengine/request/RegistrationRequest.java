package io.dankoller.github.webquizengine.request;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

/**
 * This class represents a request to register a new user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @Email(regexp = ".+@.+\\..+", message = "Email is not valid")
    private String email;
    @Length(min = 5)
    private String password;
}
