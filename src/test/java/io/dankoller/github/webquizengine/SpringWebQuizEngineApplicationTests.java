package io.dankoller.github.webquizengine;

import io.dankoller.github.webquizengine.entity.CompletedQuiz;
import io.dankoller.github.webquizengine.entity.Quiz;
import io.dankoller.github.webquizengine.entity.user.User;
import io.dankoller.github.webquizengine.persistence.CompletedQuizRepository;
import io.dankoller.github.webquizengine.persistence.QuizRepository;
import io.dankoller.github.webquizengine.persistence.UserRepository;
import io.dankoller.github.webquizengine.service.QuizService;
import io.dankoller.github.webquizengine.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings({"unused", "FieldCanBeLocal"})
class SpringWebQuizEngineApplicationTests {

    // Users
    private final String validUserEmail = "testuser@gmail.com";
    private final String validUserPassword = "secret";
    private final String invalidUserEmail = "test.@.com";
    private final String invalidUserPassword = "test";

    // Quizzes
    private final String validQuizJson = "{\"title\":\"The Java Logo\"," +
            "\"text\":\"What is depicted on the Java logo?\"," +
            "\"options\":[\"Robot\",\"Tea leaf\",\"Cup of coffee\",\"Bug\"]," +
            "\"answer\":[2]}";

    private final String invalidQuizJson = "{\"title\":\"The Java Logo\"," +
            "\"text\":\"What is depicted on the Java logo?\"," +
            "\"options\":[\"Robot\"]," +
            "\"answer\":[2, 3]}";

    private final String patchedQuizJson = "{\"title\":\"Math\"," +
            "\"text\":\"Which of the following is equal to 4?\"," +
            "\"options\":[\"1+1\",\"2+2\",\"8-1\",\"5-1\"]," +
            "\"answer\":[1]}";

    // Answers
    private final String validAnswerJson = "{\"answer\":[2]}";
    private final String invalidAnswerJson = "{\"answer\":[]}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CompletedQuizRepository completedQuizRepository;

    @Autowired
    private QuizService quizService;

    // Test if the controllers are initialized
    @Test
    @Order(1)
    void contextLoads() {
        assertThat(userRepository).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(quizRepository).isNotNull();
        assertThat(completedQuizRepository).isNotNull();
        assertThat(quizService).isNotNull();
    }

    // Test if the user registration works
    @Test
    @Order(2)
    void testUserRegistration() throws Exception {
        // Test valid user registration
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + validUserEmail + "\",\"password\":\"" + validUserPassword + "\"}"))
                .andExpect(status().isOk());

        // Test invalid user registration
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + invalidUserEmail + "\",\"password\":\"" + invalidUserPassword + "\"}"))
                .andExpect(status().isBadRequest());
    }

    // Test if the user can post a new quiz
    @Test
    @Order(3)
    void testPostQuiz() throws Exception {
        // Set the user as authenticated
        setUserAsAuthenticated();
        // Test valid quiz post
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validQuizJson))
                .andExpect(status().isOk());

        // Test invalid quiz post
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidQuizJson))
                .andExpect(status().isBadRequest());
    }

    // Test if the user can get all quizzes
    @Test
    @Order(4)
    void testGetAllQuizzes() throws Exception {
        // Set the user as authenticated
        setUserAsAuthenticated();
        mockMvc.perform(get("/api/quizzes"))
                .andExpect(status().isOk());
    }

    // Test if the user can get a specific quiz
    @Test
    @Order(5)
    void testGetSpecificQuiz() throws Exception {
        // Set the user as authenticated
        setUserAsAuthenticated();
        int quizId = quizRepository.findAll().get(0).getId();
        mockMvc.perform(get("/api/quizzes/" + quizId))
                .andExpect(status().isOk());
    }

    // Test if the user can solve a quiz
    @Test
    @Order(6)
    void testSolveQuiz() throws Exception {
        // Set the user as authenticated
        setUserAsAuthenticated();
        // Test valid answer
        int quizId = quizRepository.findAll().get(0).getId();
        mockMvc.perform(post("/api/quizzes/" + quizId + "/solve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAnswerJson))
                .andExpect(status().isOk());

        // Test invalid answer
        mockMvc.perform(post("/api/quizzes/" + quizId + "/solve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAnswerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    // Test if the user can get all completed quizzes
    @Test
    @Order(7)
    @WithAnonymousUser
    void testGetCompletedQuizzes() throws Exception {
        // Set the user as authenticated
        setUserAsAuthenticated();
        mockMvc.perform(get("/api/quizzes/completed"))
                .andExpect(status().isOk());
    }

    // Test if the user can patch a quiz
    @Test
    @Order(8)
    void testPatchQuiz() throws Exception {
        // Set the user as authenticated
        setUserAsAuthenticated();
        // Test valid quiz patch
        int quizId = quizRepository.findAll().get(0).getId();
        mockMvc.perform(patch("/api/quizzes/" + quizId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchedQuizJson))
                .andExpect(status().isOk());
    }

    // Test if the user can delete a quiz
    @Test
    @Order(9)
    void testDeleteQuiz() throws Exception {
        // Set the user as authenticated
        setUserAsAuthenticated();
        // Test valid quiz delete
        int quizId = quizRepository.findAll().get(0).getId();
        mockMvc.perform(delete("/api/quizzes/" + quizId))
                .andExpect(status().isNoContent());

        // Test invalid quiz delete
        mockMvc.perform(delete("/api/quizzes/999999999"))
                .andExpect(status().isNotFound());
    }

    // Cleanups
    @Test
    @Order(10)
    void testCleanup() {
        // Get the test user, most recent quiz and most recent completed quiz
        User user = userRepository.findByEmailIgnoreCase(validUserEmail);
        Quiz quiz = quizRepository.findById(getLatestQuizId()).orElse(null);
        CompletedQuiz completedQuiz = completedQuizRepository.findById(getLatestCompletedQuiz()).orElse(null);

        // Delete the test user
        if (user != null) {
            userRepository.delete(user);
        }

        // Delete the most recent quiz
        if (quiz != null) {
            quizRepository.delete(quiz);
        }

        // Delete the most recent completed quiz
        if (completedQuiz != null) {
            completedQuizRepository.delete(completedQuiz);
        }

        assertThat(userRepository.findByEmailIgnoreCase(validUserEmail)).isNull();
        assertThat(quizRepository.findById(getLatestQuizId()).orElse(null)).isNull();
        assertThat(completedQuizRepository.findById(getLatestCompletedQuiz()).orElse(null)).isNull();
    }

    /**
     * Helper method to set the user as authenticated.
     */
    void setUserAsAuthenticated() {
        // Add AuthenticationPrincipal to the SecurityContext
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(validUserEmail, validUserPassword));
    }

    /**
     * Helper method to get the latest quiz id.
     *
     * @return The latest quiz id
     */
    private int getLatestQuizId() {
        return quizRepository.findAll().stream()
                .mapToInt(Quiz::getId)
                .max()
                .orElse(0);
    }

    /**
     * Helper method to get the latest completed quiz id.
     *
     * @return The latest completed quiz id
     */
    private int getLatestCompletedQuiz() {
        List<CompletedQuiz> completedQuizzes = (List<CompletedQuiz>) completedQuizRepository.findAll();
        if (completedQuizzes.isEmpty()) {
            return 0;
        }
        return completedQuizzes.stream()
                .mapToInt(CompletedQuiz::getId)
                .max()
                .orElse(0);
    }
}
