package com.example.emprunttservice.repository;

import com.example.emprunttservice.entity.Emprunt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class EmpruntServiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmpruntServiceRepository repository;

    private Emprunt emprunt1;
    private Emprunt emprunt2;

    @BeforeEach
    void setUp() {
        emprunt1 = entityManager.persistAndFlush(new Emprunt(1L, 2L, LocalDate.now()));
        emprunt2 = entityManager.persistAndFlush(new Emprunt(3L, 4L, LocalDate.now()));
    }

    @Test
    void findAll_shouldReturnAll() {
        List<Emprunt> emprunts = repository.findAll();
        assertThat(emprunts).hasSize(2);
    }

    @Test
    void findById_shouldReturnWhenExists() {
        Optional<Emprunt> found = repository.findById(emprunt1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getBookId()).isEqualTo(1L);
    }

    @Test
    void findByUserId_shouldFilter() {
        List<Emprunt> emprunts = repository.findByUserId(2L);
        assertThat(emprunts).hasSize(1);
        assertThat(emprunts.get(0).getBookId()).isEqualTo(1L);
    }

    @Test
    void findByBookId_shouldFilter() {
        List<Emprunt> emprunts = repository.findByBookId(3L);
        assertThat(emprunts).hasSize(1);
        assertThat(emprunts.get(0).getUserId()).isEqualTo(4L);
    }

    @Test
    void delete_shouldRemove() {
        repository.deleteById(emprunt1.getId());
        assertThat(repository.findById(emprunt1.getId())).isEmpty();
        assertThat(repository.count()).isEqualTo(1);
    }
}

