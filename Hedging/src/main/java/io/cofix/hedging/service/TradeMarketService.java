package io.cofix.hedging.service;

import com.huobi.model.trade.Order;

public interface TradeMarketService {
    // Market selling order (e.g. trade to HTUSDT, sell HT to get USDT)
    Long sendSellMarketOrder(String symbol, String amount);

    Long sendBuyMarketOrder(String symbol, String amount);

    Order getOrderById(Long orderId);

    long cancelOrder(Long orderId);
}
