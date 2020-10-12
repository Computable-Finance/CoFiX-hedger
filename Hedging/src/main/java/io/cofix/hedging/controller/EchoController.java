package io.cofix.hedging.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
public class EchoController {
    @GetMapping("/echo")
    public String echo() {
        return "success";
    }
}
