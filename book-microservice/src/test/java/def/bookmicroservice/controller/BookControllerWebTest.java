package def.bookmicroservice.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import def.bookmicroservice.entity.Book;
import def.bookmicroservice.exception.ResourceNotFoundException;
import def.bookmicroservice.repository.BookRepository;
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
 * Web layer tests for BookController using @WebMvcTest.
 * Only the web slice is loaded; BookRepository is replaced by a MockBean.
 */
@WebMvcTest(BookController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class BookControllerWebTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookRepository bookRepository;
    private Book book1;
    private Book book2;
    @BeforeEach
    void setUp() {
        book1 = new Book("Clean Code", "Robert C. Martin", "Technology", 2008);
        book1.setId(1L);
        book2 = new Book("The Pragmatic Programmer", "David Thomas", "Technology", 1999);
        book2.setId(2L);
    }
    // ─── GET /api/books ───────────────────────────────────────────────────────
    @Test
    void getAllBooks_shouldReturn200WithListOfBooks() throws Exception {
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Clean Code")))
                .andExpect(jsonPath("$[0].author", is("Robert C. Martin")))
                .andExpect(jsonPath("$[0].genre", is("Technology")))
                .andExpect(jsonPath("$[0].publicationYear", is(2008)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("The Pragmatic Programmer")));
    }
    @Test
    void getAllBooks_shouldReturn200WithEmptyListWhenNoBooksExist() throws Exception {
        when(bookRepository.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    // ─── GET /api/books/{id} ──────────────────────────────────────────────────
    @Test
    void getBook_shouldReturn200WithBookWhenIdExists() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        mockMvc.perform(get("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.author", is("Robert C. Martin")))
                .andExpect(jsonPath("$.genre", is("Technology")))
                .andExpect(jsonPath("$.publicationYear", is(2008)));
    }
    @Test
    void getBook_shouldReturn404WhenBookNotFound() throws Exception {
        when(bookRepository.findById(99L))
                .thenThrow(new ResourceNotFoundException("Book not found with id: 99"));

        mockMvc.perform(get("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Book not found with id: 99")));
    }
    // ─── POST /api/books ──────────────────────────────────────────────────────
    @Test
    void addBook_shouldReturn200AndPersistBook() throws Exception {
        Book newBook = new Book("Design Patterns", "Gang of Four", "Technology", 1994);
        Book savedBook = new Book("Design Patterns", "Gang of Four", "Technology", 1994);
        savedBook.setId(3L);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("Design Patterns")))
                .andExpect(jsonPath("$.author", is("Gang of Four")))
                .andExpect(jsonPath("$.genre", is("Technology")))
                .andExpect(jsonPath("$.publicationYear", is(1994)));
    }
    @Test
    void addBook_shouldReturn400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
