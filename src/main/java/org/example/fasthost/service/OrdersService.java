package org.example.fasthost.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fasthost.entity.Orders;
import org.example.fasthost.entity.Tariffs;
import org.example.fasthost.entity.Users;
import org.example.fasthost.entity.enums.OrderStatus;
import org.example.fasthost.repository.OrdersRepository;
import org.example.fasthost.repository.TariffsRepository;
import org.example.fasthost.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersService {

    private final UsersRepository usersRepository;
    private final TariffsRepository tariffsRepository;
    private final OrdersRepository ordersRepository;
    private final HostingProvisionService hostingProvisionService;

    private static final int DURATION_DAYS = 30;

    private static final SecureRandom RND = new SecureRandom();
    private static final String ALPHABET = "abcdefghjkmnpqrstuvwxyz23456789";

    public static String shortCode(int len) {
        return RND.ints(len, 0, ALPHABET.length())
                .mapToObj(ALPHABET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
    @Transactional
    public void selectHostingPlan(Integer planId, String token) {
        Users user = usersRepository.findByKey(token).orElseThrow(AuthRequiredException::new);
        Tariffs tariff = tariffsRepository.findById(planId).orElseThrow(() -> new NotFoundException("Tarif topilmadi"));
        BigDecimal totalPrice = tariff.getPrice().multiply(BigDecimal.valueOf(DURATION_DAYS));

        if (user.getBalance().compareTo(totalPrice) < 0) {
            throw new InsufficientBalanceException();
        }
        String login = "u" +shortCode(5);
        String password = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String domain =  login + ".fasthost.uz";
        user.setBalance(user.getBalance().subtract(totalPrice));
        usersRepository.save(user);
        Orders order = Orders.builder()
                .user(user)
                .tariff(tariff)
                .duration_days(DURATION_DAYS)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(DURATION_DAYS))
                .login(login)
                .password(password)
                .cronJobsUsed(0)
                .build();

        ordersRepository.save(order);
        hostingProvisionService.provisionHosting(
                order,
                login,
                password,
                domain,
                tariff

        );

        log.info("📤 Hosting provisioning threadga topshirildi. Order ID: {}", order.getId());
    }


    public static class AuthRequiredException extends RuntimeException {}
    public static class InsufficientBalanceException extends RuntimeException {}
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }
}
