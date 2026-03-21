package def.usermicroservice.controller;

import def.usermicroservice.exception.ResourceNotFoundException;
import def.usermicroservice.model.User;
import def.usermicroservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserController with mocked repository (no Spring context).
 */
@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = buildUser(1L, "Alice", "Smith", "alice@example.com", "ADMIN");
        user2 = buildUser(2L, "Bob", "Brown", "bob@example.com", "USER");
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userController.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getEmail)
                .containsExactly("alice@example.com", "bob@example.com");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userController.getAllUsers();

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addUser_shouldSaveAndReturnUser() {
        User toSave = buildUser(null, "Carol", "Clark", "carol@example.com", "USER");
        User saved = buildUser(3L, "Carol", "Clark", "carol@example.com", "USER");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userController.addUser(toSave);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getEmail()).isEqualTo("carol@example.com");
        verify(userRepository, times(1)).save(toSave);
    }

    @Test
    void getUser_shouldReturnUserWhenIdExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        User result = userController.getUser(1L);

        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getRole()).isEqualTo("ADMIN");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUser_shouldThrowResourceNotFoundWhenIdDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userController.getUser(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(99L);
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

