package io.cofix.hedging.service;

import com.huobi.model.trade.Order;

public interface TradeMarketService {
    // Market selling order (e.g. trade to HTUSDT, sell HT to get USDT)
    long sendSellMarketOrder(String symbol, String amount);

    long sendBuyMarketOrder(String symbol, String amount);

    Order getOrderById(long orderId, String accessKey, String secretKey);

    long cancelOrder(long orderId);
}
