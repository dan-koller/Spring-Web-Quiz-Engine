package io.dankoller.github.webquizengine.persistence;

import io.dankoller.github.webquizengine.entity.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmailIgnoreCase(String email);

    Optional<User> findUserByEmail(String email);

    List<User> findAll();
}
