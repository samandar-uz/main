package org.example.fasthost.service;

import lombok.RequiredArgsConstructor;
import org.example.fasthost.entity.Orders;
import org.example.fasthost.entity.Users;
import org.example.fasthost.repository.OrdersRepository;
import org.example.fasthost.repository.TariffsRepository;
import org.example.fasthost.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final UsersRepository usersRepository;
    private final TariffsRepository tariffsRepository;
    private final OrdersRepository ordersRepository;
    private final FastPanelMasterService fastPanelService;
    private final FastPanelLoginService fastPanelLoginService;

    private static final int DURATION_DAYS = 30;

    @Transactional
    public void selectHostingPlan(Integer planId, String token) {

        if (token == null || token.isBlank()) {
            throw new AuthRequiredException();
        }

        Users user = usersRepository.findByKey(token)
                .orElseThrow(() -> new NotFoundException("User topilmadi"));

        var tariff = tariffsRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Tarif topilmadi"));

        BigDecimal totalPrice =
                safe(tariff.getPrice()).multiply(BigDecimal.valueOf(DURATION_DAYS));

        BigDecimal balance = safe(user.getBalance());

        if (balance.compareTo(totalPrice) < 0) {
            throw new InsufficientBalanceException();
        }

        // 🔐 Hosting credentials
        String login = "u_" + user.getId() + "_" +
                UUID.randomUUID().toString().substring(0, 6);

        String password = UUID.randomUUID()
                .toString().replace("-", "")
                .substring(0, 12);

        String domain = "site" + user.getId() + ".fasthost.uz";

        // 🔑 FastPanel TOKEN
        String fastPanelToken = fastPanelLoginService.getToken();

        // 🌐 1. Avval hosting yaratamiz
        try {
            String master = fastPanelService.createMaster(
                    fastPanelToken,
                    login,
                    password,
                    domain
            );
            System.out.println(master);
        } catch (Exception e) {
            // ❌ Hosting yaratilmasa — pul yechilmaydi
            throw new HostingProvisionException("Hosting yaratishda xato", e);
        }

        // 💰 2. Endi balance yechamiz
        user.setBalance(balance.subtract(totalPrice));
        usersRepository.save(user);

        // 📦 3. Order saqlash
        Orders order = Orders.builder()
                .user(user)
                .tariff(tariff)
                .duration_days(DURATION_DAYS)
                .totalPrice(totalPrice)
                .login(login)
                .password(password)
                .cronJobsUsed(0)
                .build();

        ordersRepository.save(order);
    }

    private static BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    // ===== Custom Exceptions =====
    public static class AuthRequiredException extends RuntimeException {}
    public static class InsufficientBalanceException extends RuntimeException {}
    public static class HostingProvisionException extends RuntimeException {
        public HostingProvisionException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
