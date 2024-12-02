package com.kodesastra.ai.bookmgmt;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kodesastra.ai.bookmgmt.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Optional<List<Book>> findByAuthorNameIgnoreCase(String name);
    //https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html#repositories.query-methods.query-property-expressions

}
