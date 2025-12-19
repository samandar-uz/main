package org.example.fasthost.controller;

import lombok.RequiredArgsConstructor;
import org.example.fasthost.service.OrdersService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hosting")
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping("/select")
    public String selectHostingPlan(
            @RequestParam("planId") Integer planId,
            @CookieValue(value = "AUTH_TOKEN", required = false) String token,
            RedirectAttributes redirect
    ) {
        try {
            ordersService.selectHostingPlan(planId, token);
            redirect.addFlashAttribute("success", "Muvaffaqiyatli sotib olindi");
        } catch (OrdersService.AuthRequiredException e) {
            redirect.addFlashAttribute("error", "Avval tizimga kiring");
            return "redirect:/auth";
        } catch (OrdersService.InsufficientBalanceException e) {
            redirect.addFlashAttribute("error", "Hisobingiz yetarli emas");
        } catch (OrdersService.NotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Xatolik yuz berdi");
        }

        return "redirect:/hosting";
    }
}
