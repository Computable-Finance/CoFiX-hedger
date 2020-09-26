package io.cofix.hedging.controller;

import io.cofix.hedging.service.HedgingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @Autowired
    HedgingService hedgingService;

    @GetMapping("/")
    String root() {
        return "redirect:/auth/verify";
    }
}
