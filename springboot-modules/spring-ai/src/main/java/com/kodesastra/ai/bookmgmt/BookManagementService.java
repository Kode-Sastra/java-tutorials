package com.kodesastra.ai.bookmgmt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kodesastra.ai.bookmgmt.entity.Author;
import com.kodesastra.ai.bookmgmt.entity.Book;
import com.kodesastra.ai.bookmgmt.entity.Publication;

@Service
public class BookManagementService {
    private final Logger logger = LoggerFactory.getLogger(BookManagementService.class);
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    public Integer saveBook(Book book) {
        return bookRepository.save(book).getBookId();
    }

    public Integer saveBook(Book book, Publication publication, Author author) {
        book.setPublication(publication);
        book.setAuthor(author);
        return bookRepository.save(book).getBookId();
    }

    public List<Book> searchBooksByAuthor(String authorName) {
        logger.info("Invoking searchBooksByAuthor({})", authorName);
        List<Book> books = bookRepository.findByAuthorNameIgnoreCase(authorName).orElse(new ArrayList<Book>());
        return books;
    }

    public Author getAuthorDetailsByName(String name) {
        return authorRepository.findByName(name).orElse(new Author());
    }

    public Publication getPublicationDetailsByName(String name) {
        return publicationRepository.findByName(name).orElse(new Publication());
    }
}
