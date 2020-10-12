package io.cofix.hedging.controller;

import io.cofix.hedging.service.HedgingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MainController {
    @Autowired
    HedgingService hedgingService;

    @GetMapping("/")
    public ModelAndView root() {
        return new ModelAndView("redirect:/hedging/hedgingData");
    }
}
