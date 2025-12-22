package org.example.fasthost.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fasthost.entity.Orders;
import org.example.fasthost.entity.Tariffs;
import org.example.fasthost.entity.enums.OrderStatus;
import org.example.fasthost.repository.OrdersRepository;
import org.example.fasthost.repository.UsersRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HostingProvisionService {

    private final FastPanelLoginService fastPanelLoginService;
    private final FastPanelMasterService fastPanelMasterService;
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;

    @Async
    @Transactional
    public void provisionHosting(Orders order,
                                 String username,
                                 String password,
                                 String domain, Tariffs tariffs) {

        log.info("🔁 Async hosting provisioning boshlandi. Order ID: {}", order.getId());

        try {
            String fastPanelToken = fastPanelLoginService.getToken();

            fastPanelMasterService.createMaster(
                    fastPanelToken,
                    username,
                    password,
                    domain,
                    tariffs

            );

            order.setStatus(OrderStatus.ACTIVE);
            ordersRepository.save(order);
            log.info("✅ Hosting ACTIVE. Order ID: {}", order.getId());
        } catch (Exception e) {
            log.error("❌ Hosting yaratilmadi. Order ID: {}", order.getId(), e);
            order.setStatus(OrderStatus.CANCELED);
            ordersRepository.save(order);
            var user = order.getUser();
            user.setBalance(user.getBalance().add(order.getTotalPrice()));
            usersRepository.save(user);

            log.warn("💰 Pul qaytarildi. User ID: {}", user.getId());
        }
    }
}
