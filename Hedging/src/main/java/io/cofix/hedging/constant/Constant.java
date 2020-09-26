package io.cofix.hedging.constant;

import java.math.BigDecimal;

public interface Constant {

    /**
     * HUOBI API
     */
    String  HUOBI_API   = "https://api.huobi.pro/market/history/trade?size=1&symbol=";

    /**
     * The ETH unit
     */
    BigDecimal UNIT_ETH = new BigDecimal("1000000000000000000");


    /**
     * The USDT unit
     */
    BigDecimal UNIT_USDT = new BigDecimal("1000000");

    /**
     * The HBTC unit
     */
    BigDecimal UNIT_HBTC = new BigDecimal("1000000000000000000");
}
