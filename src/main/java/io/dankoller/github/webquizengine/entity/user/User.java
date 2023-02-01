package io.dankoller.github.webquizengine.entity.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * This class represents a user. It is used for authentication and authorization.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotEmpty
    @Email
    private String email;
    @Column
    @NotEmpty
    private String password;

    // Default constructor for JPA
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
