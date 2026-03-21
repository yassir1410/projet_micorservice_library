package def.bookmicroservice.integration;
import com.fasterxml.jackson.databind.ObjectMapper;
import def.bookmicroservice.entity.Book;
import def.bookmicroservice.repository.BookRepository;
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
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Full integration tests — Spring Boot context loaded with H2 in-memory database.
 * Tests the entire stack from HTTP layer down to the database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class BookIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private Book book1;
    private Book book2;
    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        book1 = bookRepository.save(new Book("Clean Code", "Robert C. Martin", "Technology", 2008));
        book2 = bookRepository.save(new Book("The Pragmatic Programmer", "David Thomas", "Technology", 1999));
    }
    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }
    // ─── GET /api/books ───────────────────────────────────────────────────────
    @Test
    void getAllBooks_shouldReturnAllBooksFromDatabase() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title",
                        containsInAnyOrder("Clean Code", "The Pragmatic Programmer")));
    }
    @Test
    void getAllBooks_shouldReturnEmptyListAfterClearing() throws Exception {
        bookRepository.deleteAll();
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    // ─── GET /api/books/{id} ──────────────────────────────────────────────────
    @Test
    void getBook_shouldReturnCorrectBookById() throws Exception {
        mockMvc.perform(get("/api/books/" + book1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(book1.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.author", is("Robert C. Martin")))
                .andExpect(jsonPath("$.genre", is("Technology")))
                .andExpect(jsonPath("$.publicationYear", is(2008)));
    }
    @Test
    void getBook_shouldReturn404WhenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/books/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Book not found with id: 99999")));
    }
    // ─── POST /api/books ──────────────────────────────────────────────────────
    @Test
    void addBook_shouldPersistBookAndReturnItWithGeneratedId() throws Exception {
        Book newBook = new Book("Refactoring", "Martin Fowler", "Technology", 1999);
        String responseBody = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title", is("Refactoring")))
                .andExpect(jsonPath("$.author", is("Martin Fowler")))
                .andExpect(jsonPath("$.genre", is("Technology")))
                .andExpect(jsonPath("$.publicationYear", is(1999)))
                .andReturn().getResponse().getContentAsString();
        // Verify the book is actually stored in the DB
        Book createdBook = objectMapper.readValue(responseBody, Book.class);
        assertThat(bookRepository.findById(createdBook.getId())).isPresent();
        assertThat(bookRepository.count()).isEqualTo(3);
    }
    @Test
    void addBook_shouldReturn400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    // ─── Full lifecycle ───────────────────────────────────────────────────────
    @Test
    void fullLifecycle_createAndRetrieveBook() throws Exception {
        // 1. Create
        Book newBook = new Book("Domain-Driven Design", "Eric Evans", "Architecture", 2003);
        String createResponse = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Book created = objectMapper.readValue(createResponse, Book.class);
        assertThat(created.getId()).isNotNull();
        // 2. Retrieve by ID
        mockMvc.perform(get("/api/books/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Domain-Driven Design")))
                .andExpect(jsonPath("$.author", is("Eric Evans")));
        // 3. Verify in GET all
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
