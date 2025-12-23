package org.example.fasthost.service;

import lombok.RequiredArgsConstructor;
import org.example.fasthost.entity.Payment;
import org.example.fasthost.entity.Users;
import org.example.fasthost.entity.dto.PaymentResponse;
import org.example.fasthost.entity.dto.TzpayResponse;
import org.example.fasthost.entity.enums.PaymentMethod;
import org.example.fasthost.entity.enums.PaymentStatus;
import org.example.fasthost.repository.PaymentRepository;
import org.example.fasthost.repository.UsersRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final UsersRepository usersRepository;
    private final PaymentRepository paymentRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public PaymentResponse createPayment(Integer amount, String token) {

        // 1️⃣ amount tekshirish
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        // 2️⃣ user tekshirish
        Users user = usersRepository.findByKey(token)
                .orElseThrow(() -> new RuntimeException("Auth required"));

        // 3️⃣ TASHQI PAYMENT (TsPay / PHP API)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("amount", amount);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<TzpayResponse> response =
                restTemplate.exchange(
                        "https://payment-chek.ru/pay.php",
                        HttpMethod.POST,
                        request,
                        new ParameterizedTypeReference<>() {}
                );

        TzpayResponse res = response.getBody();

        if (res == null || !res.isSuccess()) {
            throw new RuntimeException("Payment provider error");
        }

        // 4️⃣ DB ga saqlash
        Payment payment = Payment.builder()
                .paymentId(res.getId())
                .user(user)
                .amount(BigDecimal.valueOf(amount))
                .method(PaymentMethod.CLICK)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        // 5️⃣ Clientga javob
        return PaymentResponse.builder()
                .success(true)
                .id(res.getId())
                .redirectUrl(res.getRedirect_url())
                .build();
    }
}



