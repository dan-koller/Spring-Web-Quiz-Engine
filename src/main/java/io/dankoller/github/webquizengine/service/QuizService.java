package io.dankoller.github.webquizengine.service;

import io.dankoller.github.webquizengine.entity.CompletedQuiz;
import io.dankoller.github.webquizengine.entity.Quiz;
import io.dankoller.github.webquizengine.entity.user.User;
import io.dankoller.github.webquizengine.persistence.CompletedQuizRepository;
import io.dankoller.github.webquizengine.persistence.QuizRepository;
import io.dankoller.github.webquizengine.persistence.UserRepository;
import io.dankoller.github.webquizengine.request.QuizRequest;
import io.dankoller.github.webquizengine.response.QuizAnswerResponse;
import io.dankoller.github.webquizengine.response.QuizResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@SuppressWarnings("unused")
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private CompletedQuizRepository completedQuizRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * This method is used to validate the answer to a quiz.
     *
     * @param username   The username of the user
     * @param quizId     The id of the quiz
     * @param userAnswer The answer that the user submitted
     * @return A ResponseEntity with the result of the answer
     */
    public ResponseEntity<?> validateAnswer(String username, int quizId, int[] userAnswer) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (quiz.getAnswer() == null && userAnswer.length == 0 || Arrays.equals(userAnswer, quiz.getAnswer())) {
            markQuizAsCompleted(quiz, username);
            return new ResponseEntity<>(new QuizAnswerResponse(true, "Congratulations, you're right!"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new QuizAnswerResponse(false, "Wrong answer! Please, try again."), HttpStatus.OK);
        }
    }

    /**
     * Helper method to save a completed quiz to the database.
     *
     * @param quiz     The quiz that was completed
     * @param username The username of the user
     */
    private void markQuizAsCompleted(Quiz quiz, String username) {
        User user = userRepository.findByEmailIgnoreCase(username);
        CompletedQuiz completedQuiz = new CompletedQuiz(quiz.getId(), LocalDateTime.now(), user);
        completedQuizRepository.save(completedQuiz);
    }

    /**
     * THis method is used to get a quiz by its id.
     *
     * @param id The id of the quiz
     * @return The quiz with the given id
     */
    public Quiz getQuizById(int id) {
        return quizRepository.findById(id).orElse(null);
    }

    /**
     * This method is used to get all quizzes from the database using pagination.
     *
     * @param page The page number
     * @return A page of quizzes
     */
    public Page<Quiz> getAllQuizzes(int page, int pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, sortBy);
        return quizRepository.findAll(pageable);
    }

    /**
     * This method gets all quizzes that have been completed by a user.
     *
     * @param username The username of the user
     * @param page     The page number
     * @return A page of completed quizzes
     */
    public Page<CompletedQuiz> getCompletedQuizzes(String username, int page, int pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, sortBy);
        User user = userRepository.findByEmailIgnoreCase(username);
        return completedQuizRepository.findAllByUser(user, pageable);
    }

    /**
     * This method allows the creation of a new quiz.
     *
     * @param author      The username of the user
     * @param quizRequest The quiz request
     * @return The quiz that was created
     */
    public QuizResponse postQuiz(String author, QuizRequest quizRequest) {
        // Validate the quiz
        if (isValidQuizRequest(quizRequest)) {
            // Create a new quiz object
            Quiz quiz = new Quiz(
                    quizRequest.getTitle(),
                    quizRequest.getText(),
                    quizRequest.getOptions(),
                    quizRequest.getAnswer(),
                    userRepository.findByEmailIgnoreCase(author));
            // Add the quiz to the database
            quizRepository.save(quiz);
            // Return the quiz to the user
            return new QuizResponse(quiz.getId(), quiz.getTitle(), quiz.getText(), quiz.getOptions());
        } else {
            return null;
        }
    }

    /**
     * This method is used to delete a quiz.
     *
     * @param user The username of the user
     * @param id   The id of the quiz
     * @return A ResponseEntity with the result of the deletion (status code)
     */
    public ResponseEntity<?> deleteQuiz(String user, int id) {
        // Get the quiz from the database
        Quiz quiz = quizRepository.findById(id).orElse(null);
        // If the quiz doesn't exist, return a 404
        if (quiz == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // If the user is not the author of the quiz, return a 403
        if (!quiz.getAuthor().getEmail().equals(user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Delete the quiz
        quizRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method is used to update a quiz.
     *
     * @param user        The username of the user
     * @param id          The id of the quiz
     * @param quizRequest The quiz request
     * @return A ResponseEntity with the result of the update (status code)
     */
    public ResponseEntity<?> patchQuiz(String user, int id, QuizRequest quizRequest) {
        // Get the quiz from the database
        Quiz quiz = quizRepository.findById(id).orElse(null);
        // If the quiz doesn't exist, return a 404
        if (quiz == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // If the user is not the author of the quiz, return a 403
        if (!quiz.getAuthor().getEmail().equals(user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Validate the quiz
        if (isValidQuizRequest(quizRequest)) {
            // Update the quiz
            quiz.setTitle(quizRequest.getTitle());
            quiz.setText(quizRequest.getText());
            quiz.setOptions(quizRequest.getOptions());
            quiz.setAnswer(quizRequest.getAnswer());
            // Save the quiz to the database
            quizRepository.save(quiz);
            // Return a quiz response to the user
            return new ResponseEntity<>(new QuizResponse(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getText(),
                    quiz.getOptions()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * A helper method to validate if a quiz request is a valid quiz.
     *
     * @param quizRequest The quiz request
     * @return True if the quiz is valid, false otherwise
     */
    private boolean isValidQuizRequest(QuizRequest quizRequest) {
        boolean isValidTitle = quizRequest.getTitle() != null && !quizRequest.getTitle().isEmpty();
        boolean isValidText = quizRequest.getText() != null && !quizRequest.getText().isEmpty();
        boolean isValidOptions = quizRequest.getOptions() != null && quizRequest.getOptions().length >= 2;
        // The answer can be null, so we don't need to check it
        return isValidTitle && isValidText && isValidOptions;
    }
}
