package com.example.emprunttservice.dto;

import java.time.LocalDate;

public class EmpruntResponseDto {
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDate empruntDate;
    private BookDto book;
    private UserDto user;

    // Constructors
    public EmpruntResponseDto() {
    }

    public EmpruntResponseDto(Long id, Long bookId, Long userId, LocalDate empruntDate, BookDto book, UserDto user) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.empruntDate = empruntDate;
        this.book = book;
        this.user = user;
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

    public BookDto getBook() {
        return book;
    }

    public void setBook(BookDto book) {
        this.book = book;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}

