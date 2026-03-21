package com.example.emprunttservice.service;

import com.example.emprunttservice.dto.BookDto;
import com.example.emprunttservice.dto.EmpruntResponseDto;
import com.example.emprunttservice.dto.UserDto;
import com.example.emprunttservice.entity.Emprunt;
import com.example.emprunttservice.feign.BookServiceClient;
import com.example.emprunttservice.feign.UserServiceClient;
import com.example.emprunttservice.repository.EmpruntServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpruntServiceUnitTest {

    @Mock
    private EmpruntServiceRepository empruntRepository;
    @Mock
    private BookServiceClient bookServiceClient;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private EmpruntService empruntService;

    private BookDto book;
    private UserDto user;
    private Emprunt emprunt;

    @BeforeEach
    void setUp() {
        book = new BookDto(1L, "Clean Code", "Robert C. Martin", "Tech", 2008);
        user = new UserDto(2L, "Alice", "Smith", "alice@example.com", "ADMIN");
        emprunt = new Emprunt(1L, 2L, LocalDate.now());
        emprunt.setId(10L);
    }

    @Test
    void createEmprunt_shouldPersistAndPublishEvent() {
        when(bookServiceClient.getBookById(1L)).thenReturn(book);
        when(userServiceClient.getUserById(2L)).thenReturn(user);
        when(empruntRepository.save(any(Emprunt.class))).thenReturn(emprunt);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(mock());

        EmpruntResponseDto result = empruntService.createEmprunt(1L, 2L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getBook().getTitle()).isEqualTo("Clean Code");
        assertThat(result.getUser().getEmail()).isEqualTo("alice@example.com");
        verify(empruntRepository).save(any(Emprunt.class));
        verify(kafkaTemplate).send(eq("emprunt-created"), contains("borrowed book"));
    }

    @Test
    void getEmpruntById_shouldReturnDtoWhenFound() {
        when(empruntRepository.findById(10L)).thenReturn(Optional.of(emprunt));
        when(bookServiceClient.getBookById(1L)).thenReturn(book);
        when(userServiceClient.getUserById(2L)).thenReturn(user);

        EmpruntResponseDto result = empruntService.getEmpruntById(10L);

        assertThat(result.getBookId()).isEqualTo(1L);
        assertThat(result.getUser().getFirstName()).isEqualTo("Alice");
    }

    @Test
    void getEmpruntById_shouldThrowWhenMissing() {
        when(empruntRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empruntService.getEmpruntById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Emprunt not found");
    }

    @Test
    void getAllEmprunts_shouldMapDtos() {
        when(empruntRepository.findAll()).thenReturn(List.of(emprunt));
        when(bookServiceClient.getBookById(1L)).thenReturn(book);
        when(userServiceClient.getUserById(2L)).thenReturn(user);

        List<EmpruntResponseDto> result = empruntService.getAllEmprunts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBook().getAuthor()).isEqualTo("Robert C. Martin");
    }

    @Test
    void deleteEmprunt_shouldDeleteWhenExists() {
        when(empruntRepository.existsById(10L)).thenReturn(true);

        empruntService.deleteEmprunt(10L);

        verify(empruntRepository).deleteById(10L);
    }

    @Test
    void deleteEmprunt_shouldThrowWhenMissing() {
        when(empruntRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> empruntService.deleteEmprunt(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Emprunt not found");
    }
}

