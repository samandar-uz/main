package org.example.fasthost.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FastPanelLoginService {

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String getToken() {

        String url = "https://panel.fasthost.uz:8888/login";

        Map<String, String> body = Map.of(
                "username", "fastuser",
                "password", "IwE8CvCFvleNc072"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
        );

        // data.token ni olib qaytaradi
        assert response.getBody() != null;
        return ((Map<String, Object>) response.getBody()
                .get("data"))
                .get("token")
                .toString();
    }
}
