package io.cofix.hedging.config;

import io.cofix.hedging.service.HedgingPoolService;
import io.cofix.hedging.service.serviceImpl.HedgingHbtcPoolServiceImpl;
import io.cofix.hedging.service.serviceImpl.HedgingUsdtPoolServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HedgingPoolServiceConfig {
    @Bean(name = "HBTC")
    HedgingPoolService  getHedgingHbtcPoolService() {
        return new HedgingHbtcPoolServiceImpl();
    }

    @Bean(name = "USDT")
    HedgingPoolService getHedgingUsdtPoolService() {
        return new HedgingUsdtPoolServiceImpl();
    }
}
