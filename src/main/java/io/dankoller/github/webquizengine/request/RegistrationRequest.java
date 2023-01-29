package io.dankoller.github.webquizengine.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @Email(regexp = ".+@.+\\..+", message = "Email is not valid")
    private String email;
    @Length(min = 5)
    private String password;
}
