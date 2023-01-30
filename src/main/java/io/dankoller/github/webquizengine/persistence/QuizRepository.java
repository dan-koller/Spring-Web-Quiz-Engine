package io.dankoller.github.webquizengine.persistence;

import io.dankoller.github.webquizengine.entity.Quiz;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to access the quizzes in the database.
 */
@Repository
public interface QuizRepository extends PagingAndSortingRepository<Quiz, Integer> {
    Optional<Quiz> findById(int id);

    List<Quiz> findAll();
}
