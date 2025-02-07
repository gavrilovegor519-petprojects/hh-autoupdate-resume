package ru.gavrilovegor519.hh_autoupdate_resume.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class SendTelegramNotification {
    @Value("${ru.gavrilovegor519.hh-autoupdate-resume.telegram.botToken}")
    private String botToken;

    @Value("${ru.gavrilovegor519.hh-autoupdate-resume.telegram.chatId}")
    private String chatId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.telegram.org")
            .build();

    public void send(String message) {
        restClient.get()
                .uri("/{botToken}/sendMessage?chat_id={chatId}&text={message}", "bot" + botToken, chatId, message)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                        return objectMapper.readValue(response.getBody(), new TypeReference<>() {
                        });
                    } else {
                        throw new HttpClientErrorException(response.getStatusCode(), new String(response.getBody().readAllBytes()));
                    }
                });
    }
}
