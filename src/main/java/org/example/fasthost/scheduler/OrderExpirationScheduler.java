package org.example.fasthost.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fasthost.entity.Orders;
import org.example.fasthost.entity.enums.OrderStatus;
import org.example.fasthost.repository.OrdersRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExpirationScheduler {

    private final OrdersRepository ordersRepository;

    /**
     * Har kuni 00:05 da ishlaydi (Toshkent vaqti)
     */
    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Tashkent")
    @Transactional
    public void expireOrders() {

        LocalDateTime now = LocalDateTime.now();

        List<Orders> expiredOrders =
                ordersRepository.findByStatusAndEndTimeBefore(
                        OrderStatus.ACTIVE,
                        now
                );

        if (expiredOrders.isEmpty()) {
            log.info("OrderExpirationScheduler: no orders to expire");
            return;
        }

        for (Orders order : expiredOrders) {
            order.setStatus(OrderStatus.EXPIRED);

            log.info(
                    "Order expired → orderId={}, userId={}, endTime={}",
                    order.getId(),
                    order.getUser().getId(),
                    order.getEndTime()
            );
        }

        ordersRepository.saveAll(expiredOrders);
    }
}
