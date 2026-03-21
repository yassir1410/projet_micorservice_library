package com.example.emprunttservice.controller;

import com.example.emprunttservice.dto.BookDto;
import com.example.emprunttservice.dto.EmpruntResponseDto;
import com.example.emprunttservice.dto.UserDto;
import com.example.emprunttservice.service.EmpruntService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpruntControllerUnitTest {

    @Mock
    private EmpruntService empruntService;

    @InjectMocks
    private EmpruntController empruntController;

    private EmpruntResponseDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleDto = new EmpruntResponseDto(1L, 1L, 2L, LocalDate.now(),
                new BookDto(1L, "Clean Code", "Robert C. Martin", "Tech", 2008),
                new UserDto(2L, "Alice", "Smith", "alice@example.com", "ADMIN"));
    }

    @Test
    void createEmprunt_shouldReturnCreated() {
        when(empruntService.createEmprunt(1L, 2L)).thenReturn(sampleDto);

        ResponseEntity<EmpruntResponseDto> response = empruntController.createEmprunt(1L, 2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBookId()).isEqualTo(1L);
        verify(empruntService).createEmprunt(1L, 2L);
    }

    @Test
    void getEmpruntById_shouldReturnOk() {
        when(empruntService.getEmpruntById(1L)).thenReturn(sampleDto);

        ResponseEntity<EmpruntResponseDto> response = empruntController.getEmpruntById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUserId()).isEqualTo(2L);
    }

    @Test
    void getAllEmprunts_shouldReturnList() {
        when(empruntService.getAllEmprunts()).thenReturn(List.of(sampleDto));

        ResponseEntity<List<EmpruntResponseDto>> response = empruntController.getAllEmprunts();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void deleteEmprunt_shouldReturnNoContent() {
        doNothing().when(empruntService).deleteEmprunt(anyLong());

        ResponseEntity<Void> response = empruntController.deleteEmprunt(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(empruntService).deleteEmprunt(1L);
    }
}

