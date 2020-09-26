package io.cofix.hedging.utils.request;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 19:08
 */

public class IntrustOrdersDetailRequest {

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

    public static interface OrderStates {
        /**
         * pre-submitted
         */
        static final String PRE_SUBMITTED = "pre-submitted";

        /**
         * submitted
         */
        static final String SUBMITTED = "submitted";

        /**
         * partial-filled
         */
        static final String PARTIAL_FILLED = "partial-filled";

        /**
         * partial-canceled
         */
        static final String PARTIAL_CANCELED = "partial-canceled";

        /**
         * filled
         */
        static final String FILLED = "filled";

        /**
         * canceled
         */
        static final String CANCELED = "canceled";
    }

    public String symbol;       //true	string	Trading on		btcusdt, bccbtc, rcneth ...
    public String types;       //false	string	Query the order type combination, using ',' split
    public String startDate;   //false	string	查Query start date, date format yyyy-mm-dd
    public String endDate;       //false	string	Query end date, date format yyyy-mm-dd
    public String states;       //true	string	Query the order status combination, using ',' split
    public String from;           //false	string	Query start ID
    public String direct;       //false	string	The query direction is prev forward and Next backward
    public String size;           //false	string	Query record size
}
