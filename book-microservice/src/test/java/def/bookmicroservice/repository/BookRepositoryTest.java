package def.bookmicroservice.repository;
import def.bookmicroservice.entity.Book;
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
 * Repository slice tests — only JPA layer is loaded with H2 in-memory DB.
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class BookRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookRepository bookRepository;
    private Book book1;
    private Book book2;
    @BeforeEach
    void setUp() {
        book1 = entityManager.persistAndFlush(
                new Book("Clean Code", "Robert C. Martin", "Technology", 2008));
        book2 = entityManager.persistAndFlush(
                new Book("The Pragmatic Programmer", "David Thomas", "Technology", 1999));
    }
    @Test
    void findAll_shouldReturnAllPersistedBooks() {
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "The Pragmatic Programmer");
    }
    @Test
    void findById_shouldReturnBookWhenIdExists() {
        Optional<Book> found = bookRepository.findById(book1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Clean Code");
        assertThat(found.get().getAuthor()).isEqualTo("Robert C. Martin");
        assertThat(found.get().getGenre()).isEqualTo("Technology");
        assertThat(found.get().getPublicationYear()).isEqualTo(2008);
    }
    @Test
    void findById_shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<Book> found = bookRepository.findById(999L);
        assertThat(found).isEmpty();
    }
    @Test
    void save_shouldPersistNewBook() {
        Book newBook = new Book("Refactoring", "Martin Fowler", "Technology", 1999);
        Book saved = bookRepository.save(newBook);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Refactoring");
        assertThat(bookRepository.count()).isEqualTo(3);
    }
    @Test
    void save_shouldUpdateExistingBook() {
        book1.setTitle("Clean Code (2nd Ed.)");
        bookRepository.save(book1);
        entityManager.flush();
        entityManager.clear();
        Optional<Book> updated = bookRepository.findById(book1.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("Clean Code (2nd Ed.)");
    }
    @Test
    void deleteById_shouldRemoveBookFromDatabase() {
        bookRepository.deleteById(book1.getId());
        assertThat(bookRepository.findById(book1.getId())).isEmpty();
        assertThat(bookRepository.count()).isEqualTo(1);
    }
    @Test
    void count_shouldReturnCorrectNumberOfBooks() {
        assertThat(bookRepository.count()).isEqualTo(2);
    }
}
