package com.example.emprunttservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "emprunts")
public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate empruntDate;

    // Constructors
    public Emprunt() {
    }

    public Emprunt(Long bookId, Long userId, LocalDate empruntDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.empruntDate = empruntDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getEmpruntDate() {
        return empruntDate;
    }

    public void setEmpruntDate(LocalDate empruntDate) {
        this.empruntDate = empruntDate;
    }

    @Override
    public String toString() {
        return "Emprunt{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", empruntDate=" + empruntDate +
                '}';
    }
}

