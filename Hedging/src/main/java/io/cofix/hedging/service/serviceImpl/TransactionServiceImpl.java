package io.cofix.hedging.service.serviceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cofix.hedging.model.HedgingPool;
import io.cofix.hedging.service.HedgingService;
import io.cofix.hedging.service.TransactionService;
import io.cofix.hedging.utils.HttpClientUtil;
import io.cofix.hedging.utils.api.ApiClient;
import io.cofix.hedging.utils.api.JsonUtil;
import io.cofix.hedging.utils.request.CreateOrderRequest;
import io.cofix.hedging.utils.response.Accounts;
import io.cofix.hedging.utils.response.AccountsResponse;
import io.cofix.hedging.utils.response.BalanceResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private HedgingService hedgingService;

    // ERC20 symbol
    public static volatile String SYMBOL = null;

    // Whether the exchange has authenticated users
    private volatile String AUTHORIZED_USER = "true";

    private volatile String API_KEY = "";
    private volatile String API_SECRET = "";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


    /**
     * Sets whether the exchange has user authentication on
     */
    @Override
    public void updateAuthorizedUser() {
        if (AUTHORIZED_USER.equalsIgnoreCase("true")) {
            AUTHORIZED_USER = "false";
        } else {
            AUTHORIZED_USER = "true";
        }
    }

    /**
     * Change the exchange API-Key and API-Secret
     */
    @Override
    public String updateExchangeApiKey(String apiKey, String apiSecret) {
        API_KEY = apiKey;
        API_SECRET = apiSecret;
        return "SUCCESS";
    }


    /**
     * Get the exchange price
     */
    @Override
    public BigDecimal getExchangePrice(String huobi_api, String symbols) {
        if (huobi_api == null) {
            LOG.error("The Huobi API failed to initialize, and ERC20's Symbol failed to obtain");
            return null;
        }

        String s = HttpClientUtil.sendHttpGet(huobi_api);
        if (s == null) {
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONArray data = jsonObject.getJSONArray("data");
        if (data == null) {
            return null;
        }

        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal n = new BigDecimal("0");
        if (data.size() == 0) {
            return null;
        }

        for (int i = 0; i < data.size(); i++) {
            Object o = data.get(i);
            JSONObject jsonObject1 = JSONObject.parseObject(String.valueOf(o));
            JSONArray data1 = jsonObject1.getJSONArray("data");

            if (data1 == null) {
                continue;
            }

            if (data1.size() == 0) {
                continue;
            }

            JSONObject jsonObject2 = JSONObject.parseObject(String.valueOf(data1.get(0)));
            BigDecimal price = jsonObject2.getBigDecimal("price");
            if (price == null) {
                continue;
            }

            totalPrice = totalPrice.add(price);
            n = n.add(new BigDecimal("1"));
        }

        if (n.compareTo(new BigDecimal("0")) > 0) {
            totalPrice = totalPrice.divide(n, 18, BigDecimal.ROUND_DOWN);
            // Depending on the transaction pair, it needs to be processed differently, or converted to ethXXX if it ends in the ETH
            if (StringUtils.isNotBlank(symbols) && symbols.endsWith("eth")) {
                totalPrice = BigDecimal.ONE.divide(totalPrice, 18, BigDecimal.ROUND_DOWN);
            }
            return totalPrice;
        }
        return null;
    }

    @Override
    public String getApiKey() {
        return API_KEY;
    }

    @Override
    public String getSecretKey() {
        return API_SECRET;
    }


    // Market selling order (e.g. trade to HTUSDT, sell HT to get USDT)
    @Override
    public Long sendSellMarketOrder(String symbol, String amount) {
        HedgingPool hedgingPool = getHedgingPoolBySymbol(symbol);

        ApiClient client = new ApiClient(hedgingPool.getApiKey(), hedgingPool.getSecretKey());
        AccountsResponse accounts = client.accounts();
        Long orderId = -1L;
        List<Accounts> list = (List<Accounts>) accounts.getData();
        Accounts account = list.get(0);
        long accountId = account.getId();

        // create order:
        CreateOrderRequest createOrderReq = new CreateOrderRequest();
        createOrderReq.accountId = String.valueOf(accountId);
        createOrderReq.amount = amount;
        createOrderReq.symbol = symbol;
        createOrderReq.type = CreateOrderRequest.OrderType.SELL_MARKET; // The market price to sell
        createOrderReq.source = "api";

        //------------------------------------------------------ Create the order  -------------------------------------------------------
        try {
            orderId = client.createOrder(createOrderReq);
        } catch (Exception e) {
            LOG.info("There is an exception to the original sell order");
            return -1L;
        }

        LOG.info("Order ID: " + orderId);
        return orderId;
    }

    /**
     * Market order (e.g., trading to HTUSDT, buying TO HT and selling to USDT)
     */
    @Override
    public Long sendBuyMarketOrder(String symbol, String amount) {
        HedgingPool hedgingPool = getHedgingPoolBySymbol(symbol);

        ApiClient client = new ApiClient(hedgingPool.getApiKey(), hedgingPool.getSecretKey());
        AccountsResponse accounts = client.accounts();
        Long orderId = -1L;
        List<Accounts> list = (List<Accounts>) accounts.getData();
        Accounts account = list.get(0);
        long accountId = account.getId();

        // create order:
        CreateOrderRequest createOrderReq = new CreateOrderRequest();
        createOrderReq.accountId = String.valueOf(accountId);
        createOrderReq.amount = amount;
        createOrderReq.symbol = symbol;
        createOrderReq.type = CreateOrderRequest.OrderType.BUY_MARKET; // The market price to sell
        createOrderReq.source = "api";

        //------------------------------------------------------ Create the order  -------------------------------------------------------
        try {
            orderId = client.createOrder(createOrderReq);
        } catch (Exception e) {
            LOG.info("Buy and buy abnormal");
            return -1L;
        }

        LOG.info("Order ID: " + orderId);
        return orderId;
    }


    /**
     * Query the exchange's token balance
     *
     * @return
     */
    private HashMap<String, String> getExchangeBalance() {
        ApiClient client = new ApiClient(API_KEY, API_SECRET);
        AccountsResponse accounts = client.accounts();
        List<Accounts> listAccounts = (List<Accounts>) accounts.getData();
        if (!listAccounts.isEmpty()) {
            //------------------------------------------------------ The account balance  -------------------------------------------------------
            BalanceResponse balance = client.balance(String.valueOf(listAccounts.get(0).getId()));

            String s = "";
            try {
                s = JsonUtil.writeValue(balance);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = JSONObject.parseObject(s);
            String data = jsonObject.getString("data");
            JSONObject jsonObject1 = JSONObject.parseObject(data);
            JSONArray list = jsonObject1.getJSONArray("list");
            HashMap<String, String> hashMap = new HashMap();

            list.forEach(li -> {
                JSONObject jsonObject2 = JSONObject.parseObject(String.valueOf(li));
                String balanceStr = jsonObject2.getString("balance");
                String currency = jsonObject2.getString("currency").toLowerCase();
                if (!balanceStr.equalsIgnoreCase("0")) {
                    if (hashMap.containsKey(currency)) {
                        // The same token may appear several times, and select the one with the largest amount
                        if (new BigDecimal(hashMap.get(currency)).compareTo(new BigDecimal(balanceStr)) < 0) {
                            hashMap.replace(currency, hashMap.get(currency), balanceStr);
                        }
                    } else {
                        hashMap.put(currency, balanceStr);
                    }
                }
            });
            LOG.info("All balances in the exchange account：" + hashMap);
            return hashMap;
        }
        return null;
    }

    private HedgingPool getHedgingPoolBySymbol(String symbol) {
        if (org.springframework.util.StringUtils.isEmpty(symbol)) return null;

        List<HedgingPool> hedgingPools = hedgingService.getHedgingPoolList();
        for (HedgingPool hedgingPool : hedgingPools) {
            if (symbol.equals(hedgingPool.getSymbol()))
                return hedgingPool;
        }

        return null;
    }
}
