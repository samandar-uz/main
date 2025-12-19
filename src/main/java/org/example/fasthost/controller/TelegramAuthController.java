package org.example.fasthost.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fasthost.entity.Users;
import org.example.fasthost.entity.dto.Response;
import org.example.fasthost.entity.dto.TelegramAuthDto;
import org.example.fasthost.repository.UsersRepository;
import org.example.fasthost.service.TelegramBotService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TelegramAuthController {

    private final UsersRepository usersRepository;
    private final TelegramBotService telegramBotService;

    @PostMapping("/telegram")
    @Transactional
    public Response<?> telegramLogin(
            @Valid @RequestBody TelegramAuthDto dto,
            @CookieValue(value = "AUTH_TOKEN", required = false) String token
    ) {
        if (token == null || token.isBlank()) {
            log.warn("AUTH_TOKEN cookie topilmadi");
            return Response.error("Avval tizimga kiring");
        }
        if (dto.getId() == null) {
            log.warn("Telegram ID keltirilmagan");
            return Response.error("Telegram ID kelmadi");
        }

        try {
            Users user = usersRepository.findByKey(token)
                    .orElseThrow(() -> {
                        log.error("Token bo'yicha foydalanuvchi topilmadi: {}", token);
                        return new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Foydalanuvchi topilmadi yoki sessiya tugagan"
                        );
                    });



            user.setTgLogin(true);
            user.setTgId(dto.getId());
            usersRepository.save(user);
            log.info("Foydalanuvchi Telegram bilan muvaffaqiyatli bog'landi. User ID: {}, TG ID: {}",
                    user.getId(), dto.getId());

            boolean messageSent = telegramBotService.sendMessage(
                    dto.getId(),
                    "✅ <b>UzHost</b> hisobingiz Telegram bilan muvaffaqiyatli bog'landi.\n\n" +
                            "👤 Foydalanuvchi: <b>" + user.getId() + "</b>\n" +
                            "📧 Email: <code>" + user.getEmail() + "</code>\n\n" +
                            "📌 Endi siz barcha bildirishnomalarni Telegram orqali olasiz."
            );

            if (!messageSent) {
                log.warn("Telegram xabar yuborilmadi, lekin bog'lanish saqlandi. User ID: {}",
                        user.getId());
            }

            return Response.success("Telegram muvaffaqiyatli ulandi");

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Telegram bog'lashda kutilmagan xato. Token: {}, TG ID: {}",
                    token, dto.getId(), e);
            return Response.error("Telegram bog'lashda xatolik yuz berdi");
        }
    }


}