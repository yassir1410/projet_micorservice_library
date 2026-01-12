package com.example.emprunttservice.repository;

import com.example.emprunttservice.entity.Emprunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmpruntServiceRepository extends JpaRepository<Emprunt, Long> {

    /**
     * Find all emprunts for a specific user
     */
    List<Emprunt> findByUserId(Long userId);

    /**
     * Find all emprunts for a specific book
     */
    List<Emprunt> findByBookId(Long bookId);
}

