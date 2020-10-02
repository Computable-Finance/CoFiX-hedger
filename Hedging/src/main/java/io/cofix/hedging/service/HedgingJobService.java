package io.cofix.hedging.service;

import io.cofix.hedging.model.HedgingPool;

import java.math.BigDecimal;

public interface HedgingJobService {
    void hedging();

    void hedgingPool(HedgingPool hedgingPool, TradeMarketService tradeMarketService);

/*
    void updateThreshold(BigDecimal ethThreshold, BigDecimal usdtThreshold, BigDecimal hbtcThreshold);

    BigDecimal getEthThreshold();

    BigDecimal getUsdtThreshold();

    BigDecimal getHbtcThreshold();
*/
}
