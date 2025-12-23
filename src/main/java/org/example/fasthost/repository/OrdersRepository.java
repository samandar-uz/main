package org.example.fasthost.repository;

import org.example.fasthost.entity.Orders;
import org.example.fasthost.entity.Users;
import org.example.fasthost.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {


    List<Orders> findByUserIdOrderByCreateTimeDesc(Integer userId);

    long countByUserId(Integer userId);
    List<Orders> findByStatusAndEndTimeBefore(
            OrderStatus status,
            LocalDateTime time
    );

}