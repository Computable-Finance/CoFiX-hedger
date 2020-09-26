package io.cofix.hedging.service.serviceImpl;

import com.huobi.model.trade.Order;
import io.cofix.hedging.service.TradeMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TradeMarketServiceMockImpl implements TradeMarketService {
    @Override
    public Long sendSellMarketOrder(String symbol, String amount) {
        log.info("Sell " + symbol + " : " + amount);
        return new Long(0);
    }

    @Override
    public Long sendBuyMarketOrder(String symbol, String amount) {
        log.info("Buy " + symbol + " : " + amount);
        return new Long(0);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return new Order();
    }

    @Override
    public long cancelOrder(Long orderId) {
        return 0;
    }
}
