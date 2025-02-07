package ru.gavrilovegor519.hh_autoupdate_resume.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.gavrilovegor519.hh_autoupdate_resume.dto.TokenDto;

@Component
@Slf4j
public class HhApiUtils {
    @Value("${ru.gavrilovegor519.hh-autoupdate-resume.authToken}")
    private String authToken;

    @Value("${ru.gavrilovegor519.hh-autoupdate-resume.clientId}")
    private String clientId;

    @Value("${ru.gavrilovegor519.hh-autoupdate-resume.clientSecret}")
    private String clientSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://hh.ru")
            .defaultHeader("User-Agent", "hh-autoupdate-resume/1.0 (gavrilovegor519-2@yandex.ru)")
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> log.error(res.getStatusText()))
            .build();

    public TokenDto getInitialToken() {
        return restClient.post()
                .uri("/oauth/token?grant_type=authorization_code&client_id={clientId}&client_secret={clientSecret}&code={authToken}",
                        clientId, clientSecret, authToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                        return objectMapper.readValue(response.getBody(), new TypeReference<>() {
                        });
                    } else {
                        throw new UnsupportedOperationException(response.getStatusText());
                    }
                });
    }

    public TokenDto getNewToken(String refreshToken) {
        return restClient.post()
                .uri("/oauth/token?grant_type=refresh_token&refresh_token={refreshToken}", refreshToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                        return objectMapper.readValue(response.getBody(), new TypeReference<>() {
                        });
                    } else {
                        throw new UnsupportedOperationException(response.getStatusText());
                    }
                });
    }

    public void updateResume(String resumeId, String accessToken) {
        restClient.post()
                .uri("/resume/{resumeId}/publish", resumeId)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204))) {
                        return objectMapper.readValue(response.getBody(), new TypeReference<>() {
                        });
                    } else {
                        throw new UnsupportedOperationException(response.getStatusText());
                    }
                });
    }
}
