package com.kodesastra.ai.so;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.context.ActiveProfiles;

import com.kodesastra.ai.so.entity.Book;

@SpringBootTest
@ActiveProfiles("so")
public class StructuredOutputLiveTest {
    private final Logger logger = LoggerFactory.getLogger(StructuredOutputLiveTest.class);
    @Autowired
    private ChatModel chatModel;

    @Test
    void whenPromptToProvideJsonResponse_thenCheckResponse() {
        String query = "Can you suggest some popular works by the Author Mark Twain. "
            + "Please provide the response in the form of JSON Array. "
            + "Respond only with a valid JSON Array. "
            + "Do not include any additional text, comments or symbols."
            + "Each object in the JSON array has the following keys:"
            + "bookName, AuthorName, yearOfPublish.";
        Prompt prompt = new Prompt(query);

        ChatResponse chatResponse = chatModel.call(prompt);
        String responseContent = chatResponse.getResult().getOutput().getText();
        logger.info("The output from the LLM: {}", responseContent);
    }

    @Test
    void whenUseBeanOutputConvertor_thenConvertToPojo() {
        StructuredOutputConverter<AuthorBooks> beanOutputConverter
            = new BeanOutputConverter(AuthorBooks.class);
        String userInputTemplate = """
            Can you suggest 6 popular works of the Author {author}.
            Please provide the title, author name and publishing date.
            {format}
            """;
        PromptTemplate promptTemplate = PromptTemplate.builder()
            .template(userInputTemplate)
            .variables(Map.of("author", "Mark Twain", "format", beanOutputConverter.getFormat()))
            .build();
 //Below causing compilation issue, please use the above
 /*       PromptTemplate promptTemplate
            = new PromptTemplate(userInputTemplate,
            Map.of("author","Mark Twain", "format", beanOutputConverter.getFormat()));
*/
        Prompt prompt = new Prompt(promptTemplate.createMessage());

        ChatResponse chatResponse = chatModel.call(prompt);
        Generation generation = chatResponse.getResult();

        String responseContent = generation.getOutput().getText();
        logger.info("The output from the LLM: {}", responseContent);
        AuthorBooks authorBooks = beanOutputConverter.convert(responseContent);
        assertEquals(6, authorBooks.books().size());
    }

    @Test
    void whenUseMapOutputConverter_thenConvertToPojo() {
        StructuredOutputConverter<Map<String, Object>> mapOutputConverter = new MapOutputConverter();
        String userInputTemplate = """
            Can you suggest 2 popular books each, of any two authors from India.
            The books have to be mentioned against the author name as the key
            Please provide the title, author name and publishing date.
            {format}
            """;

        PromptTemplate promptTemplate = PromptTemplate.builder()
            .template(userInputTemplate)
            .variables(Map.of("format", mapOutputConverter.getFormat()))
            .build();

        //Below causing compilation issue, please use the above
//        PromptTemplate promptTemplate
//            = new PromptTemplate(userInputTemplate,
//            Map.of("format", mapOutputConverter.getFormat()));

        Prompt prompt = new Prompt(promptTemplate.createMessage());
        ChatResponse chatResponse = chatModel.call(prompt);
        Generation generation = chatResponse.getResult();

        String responseContent = generation.getOutput().getText();
        logger.info("The output from the LLM: {}", responseContent);
        Map<String, Object> map = mapOutputConverter.convert(responseContent);

        assertEquals(2, map.keySet().size());
    }

    @Test
    void whenUseListOutputConvertor_thenConvertToPojo() {
        DefaultConversionService defaultConversionService = new DefaultConversionService();
        ListOutputConverter listOutputConverter = new ListOutputConverter(defaultConversionService);
        String userInputTemplate = """
            Can you suggest 6 popular works of the Author {author}.
            Please provide only the title.
            {format}
            """;
        PromptTemplate promptTemplate = PromptTemplate.builder()
            .template(userInputTemplate)
            .variables(Map.of("author","Mark Twain", "format", listOutputConverter.getFormat()))
            .build();

        //Below causing compilation issue, please use the above
/*
        PromptTemplate promptTemplate
            = new PromptTemplate(userInputTemplate,
            Map.of("author","Mark Twain", "format", listOutputConverter.getFormat()));
*/

        Prompt prompt = new Prompt(promptTemplate.createMessage());

        ChatResponse chatResponse = chatModel.call(prompt);
        Generation generation = chatResponse.getResult();
        String responseContent = generation.getOutput().getText();
        logger.info("The output from the LLM: {}", responseContent);
        List<String> books = listOutputConverter.convert(responseContent);
        assertEquals(6, books.size());
    }

    @Test
    void whenUseBeanOutputConvertorWithParameterizedType_thenConvertToPojo() {
        StructuredOutputConverter<List<Book>> beanOutputConverter
            = new BeanOutputConverter(new ParameterizedTypeReference<List<Book>>() {
        });
        String userInputTemplate = """
            Can you suggest 6 popular works of the Author {author}
            Please provide only the title, author name and publishing date.
            Do not include any metadata, schema, or type information.
            {format}
            """;
        PromptTemplate promptTemplate = PromptTemplate.builder()
            .template(userInputTemplate)
            .variables(Map.of("author","Mark Twain", "format", beanOutputConverter.getFormat()))
            .build();

        //Below causing compilation issue, please use the above
/*
        PromptTemplate promptTemplate
            = new PromptTemplate(userInputTemplate,
            Map.of("author","Mark Twain", "format", beanOutputConverter.getFormat()));
*/

        Prompt prompt = new Prompt(promptTemplate.createMessage());

        ChatResponse chatResponse = chatModel.call(prompt);
        Generation generation = chatResponse.getResult();
        String responseContent = generation.getOutput().getText();
        logger.info("The output from the LLM: {}", responseContent);
        List<Book> books = beanOutputConverter.convert(responseContent);
        assertEquals(6, books.size());
    }

    record AuthorBooks (String author, List<Book> books){}

}
//additional concept: Refusal
//https://spring.io/blog/2024/08/09/spring-ai-embraces-openais-structured-outputs-enhancing-json-response