package io.dankoller.github.webquizengine.persistence;

import io.dankoller.github.webquizengine.entity.CompletedQuiz;
import io.dankoller.github.webquizengine.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface is used to access the completed quizzes in the database.
 */
@Repository
public interface CompletedQuizRepository extends PagingAndSortingRepository<CompletedQuiz, Integer> {
    Page<CompletedQuiz> findAllByUser(User user, Pageable pageable);
}
