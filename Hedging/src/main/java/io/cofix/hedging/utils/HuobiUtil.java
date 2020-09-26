package io.cofix.hedging.utils;


import com.huobi.client.TradeClient;
import com.huobi.constant.HuobiOptions;
import com.huobi.model.trade.Order;

public class HuobiUtil {

    public static Order getOrder(long orderId,String accessKeyId,String screctKey) throws Exception {
        TradeClient tradeService = TradeClient.create(HuobiOptions.builder()
                .apiKey(accessKeyId)
                .secretKey(screctKey)
                .build());

        Order order = tradeService.getOrder(orderId);
        return order;
    }

    public static long cancelOrder(long orderId,String accessKeyId,String screctKey){
        TradeClient tradeService = TradeClient.create(HuobiOptions.builder()
                .apiKey(accessKeyId)
                .secretKey(screctKey)
                .build());

        long cancelResult = tradeService.cancelOrder(orderId);
        return cancelResult;
    }
}
