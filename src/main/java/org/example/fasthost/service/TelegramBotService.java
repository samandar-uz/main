package org.example.fasthost.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fasthost.entity.enums.ParseMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.api-url:https://api.telegram.org}")
    private String telegramApiUrl;

    private final RestTemplate restTemplate;


    public boolean sendMessage(Long chatId, String text) {
        return sendMessage(chatId, text, ParseMode.HTML);
    }


    public boolean sendMessage(Long chatId, String text, ParseMode parseMode) {
        if (chatId == null || text == null || text.isBlank()) {
            log.warn("Chat ID yoki xabar matni bo'sh");
            return false;
        }

        try {
            String url = String.format("%s/bot%s/sendMessage", telegramApiUrl, botToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("text", text);

            if (parseMode != ParseMode.NONE) {
                body.put("parse_mode", parseMode.getValue());
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Telegram xabar muvaffaqiyatli yuborildi. Chat ID: {}", chatId);
                return true;
            } else {
                log.error("Telegram xabar yuborishda xato. Status: {}, Chat ID: {}",
                        response.getStatusCode(), chatId);
                return false;
            }

        } catch (RestClientException e) {
            log.error("Telegram API ga so'rov yuborishda xato. Chat ID: {}, Xato: {}",
                    chatId, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Telegram xabar yuborishda kutilmagan xato. Chat ID: {}, Xato: {}",
                    chatId, e.getMessage(), e);
            return false;
        }
    }



}