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
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private CompletedQuizRepository completedQuizRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> validateAnswer(String username, int quizId, int[] userAnswer) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (quiz.getAnswer() == null && userAnswer.length == 0 || Arrays.equals(userAnswer, quiz.getAnswer())) {
            markQuizAsCompleted(quiz, username);
            return new ResponseEntity<>(new QuizAnswerResponse(true, "Congratulations, you're right!"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new QuizAnswerResponse(false, "Wrong answer! Please, try again."), HttpStatus.OK);
        }
    }

    // Helper method to mark a quiz as completed and save it to the database
    private void markQuizAsCompleted(Quiz quiz, String username) {
        User user = userRepository.findByEmailIgnoreCase(username);
        CompletedQuiz completedQuiz = new CompletedQuiz(quiz.getId(), LocalDateTime.now(), user);
        completedQuizRepository.save(completedQuiz);
    }

    public Quiz getQuizById(int id) {
        return quizRepository.findById(id).orElse(null);
    }

    public Page<Quiz> getAllQuizzes(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));
        return quizRepository.findAll(pageable);
    }

    public Page<CompletedQuiz> getAllQuizzesByCompletedAt(String user, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "completedAt");
        User u = userRepository.findByEmailIgnoreCase(user);
        return completedQuizRepository.findAllByUser(u, pageable);
    }

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
        // Return a 204
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> patchQuiz(String user, int id, QuizRequest quizRequest) {
        // Get the quiz from the database
        Quiz quiz = quizRepository.findById(id).orElse(null);

        // If the quiz doesn't exist, return a 404
        if (quiz == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // If the user is not the author of the quiz, return a 403
        if (!quiz.getAuthor().equals(user)) {
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
            // Return the quiz to the user
            return new ResponseEntity<>(new QuizResponse(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getText(),
                    quiz.getOptions()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Helper method to validate a quiz request
    private boolean isValidQuizRequest(QuizRequest quizRequest) {
        // Check if the quiz is valid
        if (quizRequest.getTitle() == null || quizRequest.getTitle().isEmpty()) {
            return false;
        }
        if (quizRequest.getText() == null || quizRequest.getText().isEmpty()) {
            return false;
        }
        if (quizRequest.getOptions() == null || quizRequest.getOptions().length < 2) {
            return false;
        }
        // The answer can be null, so we don't need to check it
        return true;
    }

}
