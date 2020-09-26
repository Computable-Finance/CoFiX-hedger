package io.cofix.hedging.config;

import io.cofix.hedging.service.TradeMarketService;
import io.cofix.hedging.service.serviceImpl.TradeMarketServiceHuobiImpl;
import io.cofix.hedging.service.serviceImpl.TradeMarketServiceMockImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TradeMarketServiceConfig {
    @Bean(name = "huobi")
    @Primary
    TradeMarketService  getHuobiMarket() {
        return new TradeMarketServiceHuobiImpl();
    }

    @Bean(name = "mock")
    TradeMarketService  getMockMarket() {
        return new TradeMarketServiceMockImpl();
    }
}
