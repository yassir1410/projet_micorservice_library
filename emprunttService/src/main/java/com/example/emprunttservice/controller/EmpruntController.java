package com.example.emprunttservice.controller;

import com.example.emprunttservice.dto.EmpruntResponseDto;
import com.example.emprunttservice.service.EmpruntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    private static final Logger logger = LoggerFactory.getLogger(EmpruntController.class);

    @Autowired
    private EmpruntService empruntService;

    /**
     * Create a new emprunt
     */
    @PostMapping
    public ResponseEntity<EmpruntResponseDto> createEmprunt(
        @RequestParam Long bookId,
        @RequestParam Long userId
    ) {
        try {
            EmpruntResponseDto emprunt = empruntService.createEmprunt(bookId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(emprunt);
        } catch (Exception e) {
            logger.error("Error creating emprunt for bookId: {} and userId: {}", bookId, userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get an emprunt by its ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmpruntResponseDto> getEmpruntById(@PathVariable Long id) {
        try {
            EmpruntResponseDto emprunt = empruntService.getEmpruntById(id);
            return ResponseEntity.ok(emprunt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get all emprunts
     */
    @GetMapping
    public ResponseEntity<List<EmpruntResponseDto>> getAllEmprunts() {
        try {
            List<EmpruntResponseDto> emprunts = empruntService.getAllEmprunts();
            return ResponseEntity.ok(emprunts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all emprunts for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmpruntResponseDto>> getEmpruntsByUserId(@PathVariable Long userId) {
        try {
            List<EmpruntResponseDto> emprunts = empruntService.getEmpruntsByUserId(userId);
            return ResponseEntity.ok(emprunts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all emprunts for a specific book
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<EmpruntResponseDto>> getEmpruntsByBookId(@PathVariable Long bookId) {
        try {
            List<EmpruntResponseDto> emprunts = empruntService.getEmpruntsByBookId(bookId);
            return ResponseEntity.ok(emprunts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete an emprunt
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmprunt(@PathVariable Long id) {
        try {
            empruntService.deleteEmprunt(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

