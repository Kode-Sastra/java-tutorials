package com.kodesastra.ai.bookmgmt;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kodesastra.ai.bookmgmt.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Optional<Author> findByName(String name);
    //https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html
}
