package def.bookmicroservice.controller;

import def.bookmicroservice.entity.Book;
import def.bookmicroservice.exception.ResourceNotFoundException;
import def.bookmicroservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Récupère tous les livres
     * @return Liste de tous les livres
     */
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Ajoute un nouveau livre
     * @param book le livre à ajouter
     * @return le livre créé avec son ID généré
     */
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    /**
     * Récupère un livre par son ID
     * @param id l'ID du livre
     * @return le livre correspondant
     * @throws ResourceNotFoundException si le livre n'existe pas
     */
    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }
}

