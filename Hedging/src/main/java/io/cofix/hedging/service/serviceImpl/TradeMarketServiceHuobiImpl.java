package io.cofix.hedging.service.serviceImpl;

import com.huobi.model.trade.Order;
import io.cofix.hedging.service.HedgingService;
import io.cofix.hedging.service.TransactionService;
import io.cofix.hedging.service.TradeMarketService;
import io.cofix.hedging.utils.HuobiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeMarketServiceHuobiImpl implements TradeMarketService {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private HedgingService hedgingService;

    @Override
    public long sendSellMarketOrder(String symbol, String amount) {
        return transactionService.sendSellMarketOrder(symbol, amount);
    }

    @Override
    public long sendBuyMarketOrder(String symbol, String amount) {
        return transactionService.sendBuyMarketOrder(symbol, amount);
    }

    @Override
    public Order getOrderById(Long orderId, String apiKey, String secretKey) {
/*
        String apiKey = transactionService.getApiKey();
        String secretKey = transactionService.getSecretKey();
*/
        if (null == orderId) return null;

        Order order = null;
        try {
            order = HuobiUtil.getOrder(orderId, apiKey, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public long cancelOrder(long orderId) {
        String apiKey = transactionService.getApiKey();
        String secretKey = transactionService.getSecretKey();
        long cancelResult = HuobiUtil.cancelOrder(orderId, apiKey, secretKey);
        return cancelResult;
    }
}
