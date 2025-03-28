package com.kodesastra.ai.bookmgmt;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.kodesastra.ai.bookmgmt.entity.Author;
import com.kodesastra.ai.bookmgmt.entity.Book;
import com.kodesastra.ai.bookmgmt.entity.Publication;

@SpringBootTest
@ActiveProfiles("fn")
@Sql(scripts = "classpath:/scripts/books.sql", executionPhase = BEFORE_TEST_CLASS)
public class BookManagementLiveTest {

    private final Logger logger = LoggerFactory.getLogger(BookManagementLiveTest.class);

    @Autowired
    private BookManagementService bookManagementService;

    @Autowired
    private ChatModel chatModel;

    @Test
    public void whenServiceDirectly_thenExecute() {
        Author author = bookManagementService.getAuthorDetailsByName("Stephen King");
        logger.info("The author id: {}", author.getAuthorId());

        Publication publication = bookManagementService.getPublicationDetailsByName("HarperCollins");
        logger.info("The publication id: {}", publication.getPublicationId());

        Integer bookId = bookManagementService.saveBook(new Book(author, publication, "The Stand", "Fantasy"));
        logger.info("Book saved with book id: {}", bookId);

        List<Book> books = bookManagementService.searchBooksByAuthor("Mark Twain");
        books.forEach(e -> logger.info(e.getName()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"I would like to know at most two books "
        + "by the authors Mark Twain, Stephen King, and Leo Tolstoy.",
          "I would like to know the names of the publications who have published Mark Twain's work. ",
          "Insert the book Sense and Sensibility by the author Jane Austen, "
        + "published by Penguin Books in the genre Romance.",
        "Insert the book The ABC Murders by the author Agatha Christie, "
        + "published by Penguin Books in the genre Mystery."})
    public void whenUserGivesInstructions_thenRespond(String userInstruction) {
        String systemInstruction = "While answering, please stick to the context provided by the function."
            + "If the book with the same title and author already exists, please abort the insert and inform the same.";
        //FUnction Calling is deprecated by Spring AI, instead they are using Tools
/*        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
            .withFunctions(Set.of("searchBooksByAuthorFn", "insertBookFn", "getPublicationFn", "getAuthorFn"))
            .build();*/
        ToolCallingChatOptions toolCallingChatOptions = ToolCallingChatOptions.builder()
            .toolNames(Set.of("searchBooksByAuthorFn", "insertBookFn", "getPublicationFn", "getAuthorFn"))
            .build();
        Prompt prompt = new Prompt(userInstruction + systemInstruction, toolCallingChatOptions);

        ChatResponse chatResponse = chatModel.call(prompt);
        String response = chatResponse.getResult()
            .getOutput()
            .getText();
        logger.info("Response from OpenAI LLM: {}", response);
    }
}
