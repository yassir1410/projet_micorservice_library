package com.example.emprunttservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.emprunttservice.dto.BookDto;
import com.example.emprunttservice.dto.UserDto;
import com.example.emprunttservice.entity.Emprunt;
import com.example.emprunttservice.feign.BookServiceClient;
import com.example.emprunttservice.feign.UserServiceClient;
import com.example.emprunttservice.repository.EmpruntServiceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class EmpruntIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpruntServiceRepository repository;

    @MockBean
    private BookServiceClient bookServiceClient;

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    private BookDto bookDto;
    private UserDto userDto;
    private Emprunt emprunt1;
    private Emprunt emprunt2;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        bookDto = new BookDto(1L, "Clean Code", "Robert C. Martin", "Tech", 2008);
        userDto = new UserDto(2L, "Alice", "Smith", "alice@example.com", "ADMIN");
        when(bookServiceClient.getBookById(anyLong())).thenReturn(bookDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(mock());

        emprunt1 = repository.save(new Emprunt(1L, 2L, LocalDate.now()));
        emprunt2 = repository.save(new Emprunt(3L, 4L, LocalDate.now()));
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void getAllEmprunts_shouldReturnAll() throws Exception {
        mockMvc.perform(get("/api/emprunts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].bookId", containsInAnyOrder(emprunt1.getBookId().intValue(), emprunt2.getBookId().intValue())))
                .andExpect(jsonPath("$[0].book.title", is("Clean Code")));
    }

    @Test
    void getEmpruntById_shouldReturnDto() throws Exception {
        mockMvc.perform(get("/api/emprunts/" + emprunt1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(emprunt1.getId().intValue())))
                .andExpect(jsonPath("$.user.email", is("alice@example.com")));
    }

    @Test
    void createEmprunt_shouldPersist() throws Exception {
        mockMvc.perform(post("/api/emprunts")
                        .param("bookId", "5")
                        .param("userId", "6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId", is(5)))
                .andExpect(jsonPath("$.userId", is(6)));

        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void deleteEmprunt_shouldRemove() throws Exception {
        mockMvc.perform(delete("/api/emprunts/" + emprunt1.getId()))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(emprunt1.getId())).isEmpty();
    }
}
