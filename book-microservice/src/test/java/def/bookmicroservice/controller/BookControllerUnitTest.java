package def.bookmicroservice.controller;

import def.bookmicroservice.entity.Book;
import def.bookmicroservice.exception.ResourceNotFoundException;
import def.bookmicroservice.repository.BookRepository;
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
 * Unit tests for BookController — BookRepository is mocked, no Spring context loaded.
 */
@ExtendWith(MockitoExtension.class)
class BookControllerUnitTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookController bookController;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book("Clean Code", "Robert C. Martin", "Technology", 2008);
        book1.setId(1L);

        book2 = new Book("The Pragmatic Programmer", "David Thomas", "Technology", 1999);
        book2.setId(2L);
    }

    // ─── getAllBooks ───────────────────────────────────────────────────────────

    @Test
    void getAllBooks_shouldReturnListOfAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> result = bookController.getAllBooks();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Book::getTitle)
                .containsExactly("Clean Code", "The Pragmatic Programmer");
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getAllBooks_shouldReturnEmptyListWhenNoBooksExist() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<Book> result = bookController.getAllBooks();

        assertThat(result).isEmpty();
        verify(bookRepository, times(1)).findAll();
    }

    // ─── addBook ──────────────────────────────────────────────────────────────

    @Test
    void addBook_shouldSaveAndReturnBook() {
        Book newBook = new Book("Design Patterns", "Gang of Four", "Technology", 1994);
        Book savedBook = new Book("Design Patterns", "Gang of Four", "Technology", 1994);
        savedBook.setId(3L);

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Book result = bookController.addBook(newBook);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getTitle()).isEqualTo("Design Patterns");
        assertThat(result.getAuthor()).isEqualTo("Gang of Four");
        assertThat(result.getPublicationYear()).isEqualTo(1994);
        verify(bookRepository, times(1)).save(newBook);
    }

    // ─── getBook ──────────────────────────────────────────────────────────────

    @Test
    void getBook_shouldReturnBookWhenIdExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        Book result = bookController.getBook(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getAuthor()).isEqualTo("Robert C. Martin");
        assertThat(result.getGenre()).isEqualTo("Technology");
        assertThat(result.getPublicationYear()).isEqualTo(2008);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBook_shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookController.getBook(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book not found with id: 99");

        verify(bookRepository, times(1)).findById(99L);
    }
}

