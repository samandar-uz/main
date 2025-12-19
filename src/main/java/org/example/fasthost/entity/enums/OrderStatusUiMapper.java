package org.example.fasthost.entity.enums;

import lombok.Getter;
import org.example.fasthost.entity.dto.StatusUi;

import java.util.EnumMap;
import java.util.Map;
@Getter
public final class OrderStatusUiMapper {

    private OrderStatusUiMapper() {}

    public static final Map<OrderStatus, StatusUi> MAP = new EnumMap<>(OrderStatus.class);

    static {
        MAP.put(OrderStatus.ACTIVE,
                new StatusUi(
                        "Faol",
                        "bg-emerald-100 dark:bg-emerald-950/30",
                        "text-emerald-700 dark:text-emerald-400",
                        "bg-emerald-500"
                ));

        MAP.put(OrderStatus.PENDING,
                new StatusUi(
                        "Kutilmoqda",
                        "bg-yellow-100 dark:bg-yellow-950/30",
                        "text-yellow-700 dark:text-yellow-400",
                        "bg-yellow-500"
                ));

        MAP.put(OrderStatus.EXPIRED,
                new StatusUi(
                        "Muddati tugagan",
                        "bg-gray-200 dark:bg-gray-800",
                        "text-gray-600 dark:text-gray-400",
                        "bg-gray-500"
                ));

        MAP.put(OrderStatus.CANCELED,
                new StatusUi(
                        "Bekor qilingan",
                        "bg-red-100 dark:bg-red-950/30",
                        "text-red-700 dark:text-red-400",
                        "bg-red-500"
                ));
    }

}
