package def.usermicroservice.repository;

import def.usermicroservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository slice tests for UserRepository using H2 in-memory database.
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = persistUser("Alice", "Smith", "alice@example.com", "ADMIN");
        user2 = persistUser("Bob", "Brown", "bob@example.com", "USER");
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("alice@example.com", "bob@example.com");
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        Optional<User> found = userRepository.findById(user1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Alice");
        assertThat(found.get().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void findById_shouldReturnEmptyWhenMissing() {
        Optional<User> found = userRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldPersistNewUser() {
        User newUser = new User();
        newUser.setFirstName("Carol");
        newUser.setLastName("Clark");
        newUser.setEmail("carol@example.com");
        newUser.setRole("USER");

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.count()).isEqualTo(3);
    }

    @Test
    void save_shouldUpdateExistingUser() {
        user1.setRole("SUPER_ADMIN");
        userRepository.save(user1);
        entityManager.flush();
        entityManager.clear();

        User updated = entityManager.find(User.class, user1.getId());
        assertThat(updated.getRole()).isEqualTo("SUPER_ADMIN");
    }

    @Test
    void deleteById_shouldRemoveUser() {
        userRepository.deleteById(user1.getId());
        assertThat(userRepository.findById(user1.getId())).isEmpty();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void count_shouldReturnNumberOfUsers() {
        assertThat(userRepository.count()).isEqualTo(2);
    }

    private User persistUser(String firstName, String lastName, String email, String role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setRole(role);
        return entityManager.persistAndFlush(user);
    }
}

