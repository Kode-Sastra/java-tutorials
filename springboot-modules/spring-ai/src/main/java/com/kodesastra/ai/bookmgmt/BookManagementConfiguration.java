package com.kodesastra.ai.bookmgmt;

import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

import com.kodesastra.ai.bookmgmt.entity.Author;
import com.kodesastra.ai.bookmgmt.entity.Book;
import com.kodesastra.ai.bookmgmt.entity.Publication;

@Configuration
@Profile("fn")
public class BookManagementConfiguration {
    @Bean
    @Description("insert a book. The book id is identified by bookId. "
        + "The book name is identified by name. "
        + "The genre is identified by genre. "
        + "The author is identified by author. "
        + "The publication is identified by publication. "
        + "The user must provide the book name, author name, genre and publication name."
    )
    Function<InsertBookRequest, Integer> insertBookFn(BookManagementService bookManagementService) {
        return insertBookRequest -> bookManagementService.saveBook(insertBookRequest.book(),
            insertBookRequest.publication(), insertBookRequest.author());
    }

    @Bean
    @Description("get the publication details. "
    + "The publication name is identified by publicationName.")
    Function<GetPublicationRequest, Publication> getPublicationFn(BookManagementService bookManagementService) {
        return getPublicationRequest -> bookManagementService.getPublicationDetailsByName(getPublicationRequest.publicationName());
    }

    @Bean
    @Description("get the author details. "
    + "The author name is identified by authorName.")
    Function<GetAuthorRequest, Author> getAuthorFn(BookManagementService bookManagementService) {
        return getAuthorRequest -> bookManagementService.getAuthorDetailsByName(getAuthorRequest.authorName());
    }

    @Bean
    @Description("Fetch all books written by an author by his name. "
        + "The author name is identified by authorName."
        + "If you don't find any books for the author just respond"
        + "- Sorry, I could not find anything related to the author")
    Function<SearchBookRequestByAuthor, List<Book>> searchBooksByAuthorFn(BookManagementService bookManagementService) {
        return searchBookRequestByAuthor -> bookManagementService.searchBooksByAuthor(searchBookRequestByAuthor.authorName());
    }
}

record SearchBookRequestByAuthor(String authorName) {}

record InsertBookRequest(Book book, Publication publication, Author author) {}

record GetAuthorRequest(String authorName) {}

record GetPublicationRequest(String publicationName) {}