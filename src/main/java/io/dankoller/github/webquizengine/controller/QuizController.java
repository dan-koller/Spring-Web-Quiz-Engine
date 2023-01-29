package io.dankoller.github.webquizengine.controller;


import io.dankoller.github.webquizengine.entity.CompletedQuiz;
import io.dankoller.github.webquizengine.entity.Quiz;
import io.dankoller.github.webquizengine.entity.user.UserDetailsImpl;
import io.dankoller.github.webquizengine.request.QuizRequest;
import io.dankoller.github.webquizengine.response.QuizResponse;
import io.dankoller.github.webquizengine.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;


    // Returns a quiz with the given id
    @GetMapping("/api/quizzes/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable int id) {
        Quiz quiz = quizService.getQuizById(id);
        if (quiz == null) {
            return new ResponseEntity<>("[]", HttpStatus.NOT_FOUND);
        }
        QuizResponse response = new QuizResponse(quiz.getId(), quiz.getTitle(), quiz.getText(), quiz.getOptions());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get a list of all quizzes using a specific page like /api/quizzes?page=1
    @GetMapping("/api/quizzes")
    public ResponseEntity<?> getAllQuizzesByPage(@RequestParam(required = false, defaultValue = "0") int page) {
        Page<Quiz> quizzes = quizService.getAllQuizzes(page);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // Get a list of all quizzes with paging
    @GetMapping("/api/quizzes/completed")
    public ResponseEntity<?> getAllQuizzesByStatusCompleted(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam(required = false, defaultValue = "0") int page) {
        Page<CompletedQuiz> quizzes = quizService.getAllQuizzesByCompletedAt(user.getUsername(), page);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // Post a new quiz
    @PostMapping("/api/quizzes")
    public ResponseEntity<?> postQuiz(@AuthenticationPrincipal UserDetailsImpl author,
                                      @RequestBody @Valid QuizRequest quizRequest) {
        QuizResponse response = quizService.postQuiz(author.getUsername(), quizRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Solve a quiz and check the answer
    @PostMapping("/api/quizzes/{id}/solve")
    public ResponseEntity<?> postQuiz(@AuthenticationPrincipal UserDetailsImpl user,
                                      @RequestBody Map<String, int[]> answer,
                                      @PathVariable int id) {
        // If the map is empty, default value should be an empty array
        int[] userAnswer = answer.getOrDefault("answer", new int[0]);
        return quizService.validateAnswer(user.getUsername(), id, userAnswer);
    }

    // Delete quizzes
    @DeleteMapping("/api/quizzes/{id}")
    public ResponseEntity<?> deleteQuiz(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable int id) {
        // Check if there is a logged-in user
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return quizService.deleteQuiz(user.getUsername(), id);
    }

    @PatchMapping("/api/quizzes/{id}")
    public ResponseEntity<?> patchQuiz(@AuthenticationPrincipal UserDetailsImpl user,
                                       @RequestBody @Valid QuizRequest quizRequest,
                                       @PathVariable int id) {
        // Check if there is a logged-in user
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return quizService.patchQuiz(user.getUsername(), id, quizRequest);
    }
}
