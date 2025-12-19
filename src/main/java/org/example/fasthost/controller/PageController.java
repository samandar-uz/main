package org.example.fasthost.controller;

import lombok.RequiredArgsConstructor;
import org.example.fasthost.entity.Orders;
import org.example.fasthost.entity.Users;
import org.example.fasthost.entity.enums.OrderStatus;
import org.example.fasthost.entity.enums.OrderStatusUiMapper;
import org.example.fasthost.repository.OrdersRepository;
import org.example.fasthost.repository.TariffsRepository;
import org.example.fasthost.repository.UsersRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final TariffsRepository tariffsRepository;
    private final UsersRepository usersRepository;
    private final OrdersRepository ordersRepository;


    @ModelAttribute("user")
    public Users currentUser(
            @CookieValue(value = "AUTH_TOKEN", required = false) String token
    ) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return usersRepository.findByKey(token).orElse(null);
    }

    @GetMapping("/")
    public String root() {
        return "home";
    }


    @GetMapping("/sign_in")
    public String signIn(@ModelAttribute("user") Users user) {
        return user != null ? "redirect:/index" : "sign_in";
    }


    @GetMapping("/sign_up")
    public String signUp() {
        return "sign_up";
    }


    @GetMapping("/index")
    public String index(
            @ModelAttribute("user") Users user,
            Model model
    ) {
        if (user == null) {
            return "redirect:/sign_in";
        }

        Integer userId = user.getId();
        model.addAttribute("orders_count",
                ordersRepository.countByUserId(userId));

        return "index";
    }

    @GetMapping("/hosting")
    public String hosting(
            @ModelAttribute("user") Users user,
            Model model
    ) {
        if (user == null) {
            return "redirect:/sign_in";
        }

        Integer userId = user.getId();

        model.addAttribute("plans",
                tariffsRepository.findAllByActiveTrue());
        model.addAttribute("orders",
                ordersRepository.findByUserId(userId));
        model.addAttribute("statusUiMap",
                OrderStatusUiMapper.MAP);

        return "hosting";
    }


    @GetMapping("/settings")
    public String settings(@ModelAttribute("user") Users user) {
        if (user == null) {
            return "redirect:/sign_in";
        }
        return "settings";
    }

    @GetMapping("/hosting/order/{id}/renew")
    public String renewOrder(
            @PathVariable("id") Integer id,
            @CookieValue(value = "AUTH_TOKEN", required = false) String token,
            Model model
    ) {
        if (token == null) {
            return "redirect:/sign_in";
        }

        var userOpt = usersRepository.findByKey(token);
        if (userOpt.isEmpty()) {
            return "redirect:/sign_in";
        }

        var orderOpt = ordersRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/hosting?error=order_not_found";
        }

        Orders order = orderOpt.get();

        if (!order.getUser().getId().equals(userOpt.get().getId())) {
            return "redirect:/hosting?error=access_denied";
        }
        model.addAttribute("statusUiMap",
                OrderStatusUiMapper.MAP);
        model.addAttribute("order", order);
        return "renew";
    }
    @PostMapping("/hosting/order/{id}/renew/confirm")
    public String confirmRenewOrder(
            @PathVariable("id") Integer id,

            @CookieValue(value = "AUTH_TOKEN", required = false) String token
    ) {
        if (token == null) {
            return "redirect:/sign_in";
        }

        var userOpt = usersRepository.findByKey(token);
        if (userOpt.isEmpty()) {
            return "redirect:/sign_in";
        }

        var orderOpt = ordersRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/hosting?error=order_not_found";
        }

        Orders order = orderOpt.get();

        if (!order.getUser().getId().equals(userOpt.get().getId())) {
            return "redirect:/hosting?error=access_denied";
        }

        order.setEndTime(
                order.getEndTime().plusDays(30)
        );
        order.setStatus(OrderStatus.ACTIVE);
ordersRepository.save(order);

        return "redirect:/hosting?success=order_renewed";
    }
}




