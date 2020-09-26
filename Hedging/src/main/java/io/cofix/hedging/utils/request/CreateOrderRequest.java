package io.cofix.hedging.utils.request;

public class CreateOrderRequest {
    public static interface OrderType {
        /**
         * Price to buy
         */
        static final String BUY_LIMIT = "buy-limit";

        /**
         * Price to sell
         */
        static final String SELL_LIMIT = "sell-limit";

        /**
         * The market price to buy
         */
        static final String BUY_MARKET = "buy-market";

        /**
         * The market price to sell
         */
        static final String SELL_MARKET = "sell-market";
    }

    /**
     * Trade pairs, mandatory, e.g. "ethcny"
     */
    public String symbol;

    /**
     * Account ID, required, e.g. "12345"
     */
    public String accountId;

    /**
     * When the order type is buy-limit and sell-limit, it represents the order quantity;
     * when the order type is buy-market, it represents the total amount of the order;
     * and when the order type is sell-market, it represents the total amount of the order
     */
    public String amount;

    /**
     * Order price, valid for limit order only, e.g. "1234.56"
     */
    public String price = "0.0";

    /**
     * Order type,sell-market,buy-limit,sell-limit
     */
    public String type;

    /**
     * Order source, for example: "API"
     */
    public String source = "com/huobi/api";
}
