package def.usermicroservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import def.usermicroservice.model.User;
import def.usermicroservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration tests for User microservice using H2 in-memory DB.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user1 = userRepository.save(buildUser("Alice", "Smith", "alice@example.com", "ADMIN"));
        user2 = userRepository.save(buildUser("Bob", "Brown", "bob@example.com", "USER"));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(
                        "alice@example.com", "bob@example.com")));
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUser_shouldReturnCorrectUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.email", is("alice@example.com")))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    void getUser_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/users/99999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }

    @Test
    void addUser_shouldPersistAndReturnUser() throws Exception {
        User newUser = buildUser("Carol", "Clark", "carol@example.com", "USER");

        String responseBody = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email", is("carol@example.com")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User created = objectMapper.readValue(responseBody, User.class);
        assertThat(userRepository.findById(created.getId())).isPresent();
        assertThat(userRepository.count()).isEqualTo(3);
    }

    @Test
    void addUser_shouldReturn400WhenBodyMissing() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fullLifecycle_createAndRetrieveUser() throws Exception {
        User newUser = buildUser("Dave", "Dunn", "dave@example.com", "USER");
        String createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User created = objectMapper.readValue(createResponse, User.class);

        mockMvc.perform(get("/api/users/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("dave@example.com")));

        mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    private User buildUser(String firstName, String lastName, String email, String role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }
}

