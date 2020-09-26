package io.cofix.hedging.service;

import java.math.BigDecimal;

public interface HedgingJobService {
    void hedging();

    void hedgingPool(HedgingPoolService hedgingPoolService, TradeMarketService tradeMarketService);

/*
    void updateThreshold(BigDecimal ethThreshold, BigDecimal usdtThreshold, BigDecimal hbtcThreshold);

    BigDecimal getEthThreshold();

    BigDecimal getUsdtThreshold();

    BigDecimal getHbtcThreshold();
*/
}
