package io.cofix.hedging;

import io.cofix.hedging.service.HedgingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class HedgingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HedgingApplication.class, args);
    }

}
