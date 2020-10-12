package io.cofix.hedging.service.serviceImpl;

import com.huobi.model.trade.Order;
import io.cofix.hedging.service.TradeMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TradeMarketServiceMockImpl implements TradeMarketService {
    @Override
    public long sendSellMarketOrder(String symbol, String amount) {
        log.info("Sell " + symbol + " : " + amount);
        return new Long(0);
    }

    @Override
    public long sendBuyMarketOrder(String symbol, String amount) {
        log.info("Buy " + symbol + " : " + amount);
        return new Long(0);
    }

    @Override
    public Order getOrderById(long orderId, String apiKey, String secretKey) {
        return new Order();
    }

    @Override
    public long cancelOrder(long orderId) {
        return 0;
    }
}
