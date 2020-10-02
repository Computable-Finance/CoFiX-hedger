package io.cofix.hedging.service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;


/**
 * ClassName:TransactionService
 * Description:
 */
public interface TransactionService {

    /**
     * Sets whether the exchange has user authentication on
     */
    void updateAuthorizedUser();

    /**
     * Change the exchange API-Key and API-Secret
     * @param apiKey
     * @param apiSecret
     * @return
     */
    String updateExchangeApiKey(String apiKey, String apiSecret);

    // Market selling order (e.g. trade to HTUSDT, sell HT to get USDT)
    Long sendSellMarketOrder(String symbol, String amount);

    Long sendBuyMarketOrder(String symbol, String amount);

    BigDecimal getExchangePrice(String huobi_api, String symbols);

    String getApiKey();

    String getSecretKey();
}
