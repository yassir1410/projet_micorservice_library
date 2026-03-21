package com.example.emprunttservice.service;

import com.example.emprunttservice.dto.BookDto;
import com.example.emprunttservice.dto.EmpruntResponseDto;
import com.example.emprunttservice.dto.UserDto;
import com.example.emprunttservice.entity.Emprunt;
import com.example.emprunttservice.exception.ResourceNotFoundException;
import com.example.emprunttservice.feign.BookServiceClient;
import com.example.emprunttservice.feign.UserServiceClient;
import com.example.emprunttservice.repository.EmpruntServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpruntService {

    @Autowired
    private EmpruntServiceRepository empruntRepository;

    @Autowired
    private BookServiceClient bookServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    /**
     * Create a new emprunt (loan) for a book and user
     */
    public EmpruntResponseDto createEmprunt(Long bookId, Long userId) {
        try {
            BookDto book = bookServiceClient.getBookById(bookId);
            UserDto user = userServiceClient.getUserById(userId);

            Emprunt emprunt = new Emprunt(bookId, userId, LocalDate.now());
            Emprunt savedEmprunt = empruntRepository.save(emprunt);

            // 3. ADD THIS: Send the message to Kafka
            // The topic name must match your Notification Service: "emprunt-created"
            String message = "User " + user.getFirstName() + " borrowed book: " + book.getTitle();
            kafkaTemplate.send("emprunt-created", message);

            return new EmpruntResponseDto(
                    savedEmprunt.getId(),
                    savedEmprunt.getBookId(),
                    savedEmprunt.getUserId(),
                    savedEmprunt.getEmpruntDate(),
                    book,
                    user
            );
        } catch (Exception e) {
            throw new RuntimeException("Error creating emprunt: " + e.getMessage(), e);
        }
    }

    /**
     * Get an emprunt by its ID with full details
     */
    public EmpruntResponseDto getEmpruntById(Long id) {
        Optional<Emprunt> emprunt = empruntRepository.findById(id);
        if (emprunt.isPresent()) {
            Emprunt e = emprunt.get();
            try {
                BookDto book = bookServiceClient.getBookById(e.getBookId());
                UserDto user = userServiceClient.getUserById(e.getUserId());

                return new EmpruntResponseDto(
                    e.getId(),
                    e.getBookId(),
                    e.getUserId(),
                    e.getEmpruntDate(),
                    book,
                    user
                );
            } catch (Exception ex) {
                throw new RuntimeException("Error fetching emprunt details: " + ex.getMessage(), ex);
            }
        }
        throw new ResourceNotFoundException("Emprunt not found with id: " + id);
    }

    /**
     * Get all emprunts with full details
     */
    public List<EmpruntResponseDto> getAllEmprunts() {
        List<Emprunt> emprunts = empruntRepository.findAll();
        return emprunts.stream()
            .map(e -> {
                try {
                    BookDto book = bookServiceClient.getBookById(e.getBookId());
                    UserDto user = userServiceClient.getUserById(e.getUserId());
                    return new EmpruntResponseDto(
                        e.getId(),
                        e.getBookId(),
                        e.getUserId(),
                        e.getEmpruntDate(),
                        book,
                        user
                    );
                } catch (Exception ex) {
                    throw new RuntimeException("Error fetching details for emprunt: " + ex.getMessage(), ex);
                }
            })
            .collect(Collectors.toList());
    }

    /**
     * Get all emprunts for a specific user
     */
    public List<EmpruntResponseDto> getEmpruntsByUserId(Long userId) {
        try {
            // Verify that the user exists
            userServiceClient.getUserById(userId);

            List<Emprunt> emprunts = empruntRepository.findByUserId(userId);
            return emprunts.stream()
                .map(e -> {
                    try {
                        BookDto book = bookServiceClient.getBookById(e.getBookId());
                        UserDto user = userServiceClient.getUserById(e.getUserId());
                        return new EmpruntResponseDto(
                            e.getId(),
                            e.getBookId(),
                            e.getUserId(),
                            e.getEmpruntDate(),
                            book,
                            user
                        );
                    } catch (Exception ex) {
                        throw new RuntimeException("Error fetching details: " + ex.getMessage(), ex);
                    }
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching emprunts for user: " + e.getMessage(), e);
        }
    }

    /**
     * Get all emprunts for a specific book
     */
    public List<EmpruntResponseDto> getEmpruntsByBookId(Long bookId) {
        try {
            // Verify that the book exists
            bookServiceClient.getBookById(bookId);

            List<Emprunt> emprunts = empruntRepository.findByBookId(bookId);
            return emprunts.stream()
                .map(e -> {
                    try {
                        BookDto book = bookServiceClient.getBookById(e.getBookId());
                        UserDto user = userServiceClient.getUserById(e.getUserId());
                        return new EmpruntResponseDto(
                            e.getId(),
                            e.getBookId(),
                            e.getUserId(),
                            e.getEmpruntDate(),
                            book,
                            user
                        );
                    } catch (Exception ex) {
                        throw new RuntimeException("Error fetching details: " + ex.getMessage(), ex);
                    }
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching emprunts for book: " + e.getMessage(), e);
        }
    }

    /**
     * Delete an emprunt
     */
    public void deleteEmprunt(Long id) {
        if (empruntRepository.existsById(id)) {
            empruntRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Emprunt not found with id: " + id);
        }
    }
}

