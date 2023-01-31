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
@SuppressWarnings("unused")
public class QuizController {

    @Autowired
    private QuizService quizService;

    /**
     * This method returns a quiz by id.
     *
     * @param id The id of the quiz
     * @return A ResponseEntity with the quiz or an empty array if the quiz is not found
     */
    @GetMapping("/api/quizzes/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable int id) {
        Quiz quiz = quizService.getQuizById(id);
        if (quiz == null) {
            return new ResponseEntity<>("[]", HttpStatus.NOT_FOUND);
        }
        QuizResponse response = new QuizResponse(quiz.getId(), quiz.getTitle(), quiz.getText(), quiz.getOptions());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method returns a list of all quizzes with paging.
     *
     * @param page The page number as a query parameter
     * @return A ResponseEntity with a list of all quizzes
     */
    @GetMapping("/api/quizzes")
    public ResponseEntity<?> getAllQuizzes(@RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "10") int pageSize,
                                           @RequestParam(required = false, defaultValue = "id") String sortBy) {
        Page<Quiz> quizzes = quizService.getAllQuizzes(page, pageSize, sortBy);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    /**
     * This method returns a list of all quizzes that the user has completed with paging.
     *
     * @param user The logged-in user
     * @param page The page number as a query parameter
     * @return A ResponseEntity with a list of all quizzes that the user has completed
     */
    @GetMapping("/api/quizzes/completed")
    public ResponseEntity<?> getCompletedQuizzes(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "completedAt") String sortBy) {
        Page<CompletedQuiz> quizzes = quizService.getCompletedQuizzes(user.getUsername(), page, pageSize, sortBy);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    /**
     * This method allows the user to create a new quiz.
     *
     * @param author      The logged-in user
     * @param quizRequest The quiz request body
     * @return A ResponseEntity with the newly created quiz
     */
    @PostMapping("/api/quizzes")
    public ResponseEntity<?> postQuiz(@AuthenticationPrincipal UserDetailsImpl author,
                                      @RequestBody @Valid QuizRequest quizRequest) {
        QuizResponse response = quizService.postQuiz(author.getUsername(), quizRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method allows the user to solve a quiz.
     *
     * @param user   The logged-in user
     * @param answer The answer request body
     * @param id     The id of the quiz
     * @return A ResponseEntity with the result of the quiz
     */
    @PostMapping("/api/quizzes/{id}/solve")
    public ResponseEntity<?> solveQuiz(@AuthenticationPrincipal UserDetailsImpl user,
                                       @RequestBody Map<String, int[]> answer,
                                       @PathVariable int id) {
        // If the map is empty, default value should be an empty array
        int[] userAnswer = answer.getOrDefault("answer", new int[0]);
        return quizService.validateAnswer(user.getUsername(), id, userAnswer);
    }

    /**
     * This method allows the user to delete a quiz.
     *
     * @param user The logged-in user
     * @param id   The id of the quiz
     * @return A ResponseEntity with the result of the deletion
     */
    @DeleteMapping("/api/quizzes/{id}")
    public ResponseEntity<?> deleteQuiz(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable int id) {
        return quizService.deleteQuiz(user.getUsername(), id);
    }

    /**
     * This method allows the user to update a quiz.
     *
     * @param user        The logged-in user
     * @param quizRequest The quiz request body
     * @param id          The id of the quiz
     * @return A ResponseEntity with the result of the update
     */
    @PatchMapping("/api/quizzes/{id}")
    public ResponseEntity<?> patchQuiz(@AuthenticationPrincipal UserDetailsImpl user,
                                       @RequestBody @Valid QuizRequest quizRequest,
                                       @PathVariable int id) {
        return quizService.patchQuiz(user.getUsername(), id, quizRequest);
    }
}
