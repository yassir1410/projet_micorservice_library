package com.example.emprunttservice.controller;

import com.example.emprunttservice.dto.BookDto;
import com.example.emprunttservice.dto.EmpruntResponseDto;
import com.example.emprunttservice.dto.UserDto;
import com.example.emprunttservice.service.EmpruntService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmpruntController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class EmpruntControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpruntService empruntService;

    private EmpruntResponseDto dto1;
    private EmpruntResponseDto dto2;

    @BeforeEach
    void setUp() {
        dto1 = new EmpruntResponseDto(1L, 1L, 2L, LocalDate.now(),
                new BookDto(1L, "Clean Code", "Robert C. Martin", "Tech", 2008),
                new UserDto(2L, "Alice", "Smith", "alice@example.com", "ADMIN"));
        dto2 = new EmpruntResponseDto(2L, 3L, 4L, LocalDate.now(),
                new BookDto(3L, "Pragmatic Programmer", "Andrew Hunt", "Tech", 1999),
                new UserDto(4L, "Bob", "Brown", "bob@example.com", "USER"));
    }

    @Test
    void getAllEmprunts_shouldReturnList() throws Exception {
        when(empruntService.getAllEmprunts()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/emprunts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].book.title", is("Clean Code")))
                .andExpect(jsonPath("$[1].user.email", is("bob@example.com")));
    }

    @Test
    void getEmpruntById_shouldReturnDto() throws Exception {
        when(empruntService.getEmpruntById(1L)).thenReturn(dto1);

        mockMvc.perform(get("/api/emprunts/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.book.id", is(1)))
                .andExpect(jsonPath("$.user.id", is(2)));
    }

    @Test
    void createEmprunt_shouldReturnCreated() throws Exception {
        when(empruntService.createEmprunt(anyLong(), anyLong())).thenReturn(dto1);

        mockMvc.perform(post("/api/emprunts")
                        .param("bookId", "1")
                        .param("userId", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId", is(1)))
                .andExpect(jsonPath("$.userId", is(2)));
    }

    @Test
    void deleteEmprunt_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/emprunts/1"))
                .andExpect(status().isNoContent());
    }
}

