package org.surro.ecomapp.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final String SHOPPING_ASSISTANT_PROMPT;

    private final ChatClient chatClient;
    private final PgVectorStore vectorStore;

    public ChatService(ChatClient chatClient,
                       PgVectorStore vectorStore,
                       @Value("classpath:prompts/chat-assistant-prompt.txt")
                       Resource promptResource) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        try (InputStreamReader reader = new InputStreamReader(promptResource.getInputStream(), StandardCharsets.UTF_8)) {
            this.SHOPPING_ASSISTANT_PROMPT = FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String message(String query) {
        String context = prepareContext(query);
        Map<String,Object> variables = new HashMap<>();
        variables.put("userQuery",query);
        variables.put("context",context);

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(SHOPPING_ASSISTANT_PROMPT)
                .variables(variables)
                .build();

        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    public String callChatclient(String prompt) {
        return chatClient
                .prompt(prompt)
                .call().content();
    }

    private String prepareContext(String query) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(5)
                .similarityThreshold(0.7)
                .build());
        String context = documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));

        System.out.println(context);
        return context;
    }

}
