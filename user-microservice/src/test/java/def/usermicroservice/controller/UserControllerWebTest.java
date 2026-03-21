package def.usermicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import def.usermicroservice.exception.ResourceNotFoundException;
import def.usermicroservice.model.User;
import def.usermicroservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web slice tests for UserController using MockMvc.
 */
@WebMvcTest(UserController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class UserControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = buildUser(1L, "Alice", "Smith", "alice@example.com", "ADMIN");
        user2 = buildUser(2L, "Bob", "Brown", "bob@example.com", "USER");
    }

    @Test
    void getAllUsers_shouldReturn200WithList() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("alice@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].email", is("bob@example.com")));
    }

    @Test
    void getAllUsers_shouldReturnEmptyList() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUser_shouldReturn200WhenFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        mockMvc.perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    void getUser_shouldReturn404WhenNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }

    @Test
    void addUser_shouldReturnSavedUser() throws Exception {
        User newUser = buildUser(null, "Carol", "Clark", "carol@example.com", "USER");
        User savedUser = buildUser(3L, "Carol", "Clark", "carol@example.com", "USER");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.email", is("carol@example.com")))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    void addUser_shouldReturn400WhenBodyMissing() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private User buildUser(Long id, String firstName, String lastName, String email, String role) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }
}

