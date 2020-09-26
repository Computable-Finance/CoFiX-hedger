package io.cofix.hedging.utils.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.cofix.hedging.utils.request.CreateOrderRequest;
import io.cofix.hedging.utils.request.DepthRequest;
import io.cofix.hedging.utils.request.IntrustOrdersDetailRequest;
import io.cofix.hedging.utils.response.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * API client.
 *
 * @Date 2018/1/14
 * @Time 16:02
 */

public class ApiClient {

    static final int CONN_TIMEOUT = 5;
    static final int READ_TIMEOUT = 5;
    static final int WRITE_TIMEOUT = 5;


    static final String API_URL = "https://api.huobi.pro";
    static final String API_HOST = getHost();

    static final MediaType JSON = MediaType.parse("application/json");
    static final OkHttpClient client = createOkHttpClient();

    final String accessKeyId;
    final String accessKeySecret;
    final String assetPassword;

    /**
     * Create an instance of ApiClient
     *
     * @param accessKeyId     AccessKeyId
     * @param accessKeySecret AccessKeySecret
     */
    public ApiClient(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.assetPassword = null;
    }

    /**
     * Create an instance of ApiClient
     *
     * @param accessKeyId     AccessKeyId
     * @param accessKeySecret AccessKeySecret
     * @param assetPassword   AssetPassword
     */
    public ApiClient(String accessKeyId, String accessKeySecret, String assetPassword) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.assetPassword = assetPassword;
    }
    /**
     * Check the address of charging currency
     *
     * @return String of address.
     */
    public DepositAddressResponse getDepositAddress(String currency){
        HashMap map = new HashMap();
        map.put("currency", currency);
        DepositAddressResponse resp =
                get("/v2/account/deposit/address", map, new TypeReference<DepositAddressResponse>() {
                });
        return resp;
    }

    /**
     * Check the withdrawal limit
     *
     * @return String of address.
     */
    public String queryExtractQuota(String currency){
        HashMap map = new HashMap();
        map.put("currency", currency);
        String resp = getReturnString("/v2/account/withdraw/quota", map, new TypeReference<ExtractQuotaResponse<ExtractQuotaBean<List<ExtractQuotaChains>>>>() {
        });
        return resp;
    }

    /**
     * Enquire currency withdrawal fee
     *
     * @return String of address.
     */
    public String queryExtractServiceCharge(String currency, String authorizedUser) {
        HashMap map = new HashMap();
        map.put("currency", currency);
        map.put("authorizedUser",authorizedUser);
        String resp =
                getReturnString("/v2/reference/currencies", map, new TypeReference<QueryExtractServiceChargeResponse>() {
                });
        return resp;
    }

    /**
     * Mention money (ERC20)
     *
     * @return String of address.
     */
    public ExtractERC20Response extractERC20(String address, String amount, String currency, String fee, String chain) {
        HashMap map = new HashMap();
        map.put("address",address);
        map.put("amount",amount);
        map.put("currency", currency);
        map.put("fee",fee);
        if(chain != null){
            map.put("chain",chain);
        }
        ExtractERC20Response resp =
                post("/v1/dw/withdraw/api/create", map, new TypeReference<ExtractERC20Response>() {
                });
        return resp;
    }

    /**
     * Query trade pairs
     *
     * @return List of symbols.
     */
    public List<Symbol> getSymbols() {
        ApiResponse<List<Symbol>> resp =
                get("/v1/common/symbols", null, new TypeReference<ApiResponse<List<Symbol>>>() {
                });
        return resp.checkAndReturn();
    }

    /**
     * Check all account information
     *
     * @return List of accounts.
     */
    public List<Account> getAccounts() {
        ApiResponse<List<Account>> resp =
                get("/v1/account/accounts", null, new TypeReference<ApiResponse<List<Account>>>() {
                });
        return resp.checkAndReturn();
    }

    /**
     * Create the order
     *
     * @param request CreateOrderRequest object.
     * @return Order id.
     */
    public Long createOrder(CreateOrderRequest request) {
        ApiResponse<Long> resp =
                        post("/v1/order/orders/place", request, new TypeReference<ApiResponse<Long>>() {
                        });
        return resp.checkAndReturn();
    }

    /**
     * Executive orders
     *
     * @param orderId The id of created order.
     * @return Order id.
     */
    public String placeOrder(long orderId) {
        ApiResponse<String> resp = post("/v1/order/orders/" + orderId + "/place", null,
                new TypeReference<ApiResponse<String>>() {
                });
        return resp.checkAndReturn();
    }


    // ----------------------------------------Quotation API-------------------------------------------

    /**
     * GET /market/history/kline Get the K line data
     *
     * @param symbol
     * @param period
     * @param size
     * @return
     */
    public KlineResponse kline(String symbol, String period, String size) {
        HashMap map = new HashMap();
        map.put("symbol", symbol);
        map.put("period", period);
        map.put("size", size);
        KlineResponse resp = get("/market/history/kline", map, new TypeReference<KlineResponse<List<Kline>>>() {
        });
        return resp;
    }

    /**
     * GET /market/detail/merged Get aggregate quotes (Ticker)
     *
     * @param symbol
     * @return
     */
    public MergedResponse merged(String symbol) {
        HashMap map = new HashMap();
        map.put("symbol", symbol);
        MergedResponse resp = get("/market/detail/merged", map, new TypeReference<MergedResponse<List<Merged>>>() {
        });
        return resp;
    }

    /**
     * GET /market/depth Get the Market Depth data
     *
     * @param request
     * @return
     */
    public DepthResponse depth(DepthRequest request) {
        HashMap map = new HashMap();
        map.put("symbol", request.getSymbol());
        map.put("type", request.getType());

        DepthResponse resp = get("/market/depth", map, new TypeReference<DepthResponse<List<Depth>>>() {
        });
        return resp;
    }

    /**
     * GET /market/trade Get the Trade Detail data
     *
     * @param symbol
     * @return
     */
    public TradeResponse trade(String symbol) {
        HashMap map = new HashMap();
        map.put("symbol", symbol);
        TradeResponse resp = get("/market/trade", map, new TypeReference<TradeResponse>() {
        });
        return resp;
    }

    /**
     * GET /market/history/trade Get the most recent transaction records in bulk
     *
     * @param symbol
     * @param size
     * @return
     */
    public HistoryTradeResponse historyTrade(String symbol, String size) {
        HashMap map = new HashMap();
        map.put("symbol", symbol);
        map.put("size", size);
        HistoryTradeResponse resp = get("/market/history/trade", map, new TypeReference<HistoryTradeResponse>() {
        });
        return resp;
    }

    /**
     * GET /market/detail Get 24-hour Market Detail volume data
     *
     * @param symbol
     * @return
     */
    public DetailResponse detail(String symbol) {
        HashMap map = new HashMap();
        map.put("symbol", symbol);
        DetailResponse resp = get("/market/detail", map, new TypeReference<DetailResponse<Details>>() {
        });
        return resp;
    }


    /**
     * GET /v1/common/symbols Query all trading pairs and accuracy supported by the system
     *
     * @param symbol
     * @return
     */
    public SymbolsResponse symbols(String symbol) {
        HashMap map = new HashMap();
        map.put("symbol", symbol);
        SymbolsResponse resp = get("/v1/common/symbols", map, new TypeReference<SymbolsResponse<Symbols>>() {
        });
        return resp;
    }

    /**
     * GET /v1/common/currencys Query all currencies supported by the system
     *
     * @param symbol
     * @return
     */
    public CurrencysResponse currencys(String symbol) {
        HashMap map = new HashMap();
        map.put("symbol", symbol);
        CurrencysResponse resp = get("/v1/common/currencys", map, new TypeReference<CurrencysResponse>() {
        });
        return resp;
    }

    /**
     * GET /v1/common/timestamp Query system current time
     *
     * @return
     */
    public TimestampResponse timestamp() {
        TimestampResponse resp = get("/v1/common/timestamp", null, new TypeReference<TimestampResponse>() {
        });
        return resp;
    }

    /**
     * GET /v1/account/accounts Query all accounts of the current user (that is, account-ID)
     *
     * @return
     */
    public AccountsResponse accounts() {
        AccountsResponse resp = get("/v1/account/accounts", null, new TypeReference<AccountsResponse<List<Accounts>>>() {
        });
        return resp;
    }

    /**
     * GET /v1/account/accounts/{account-id}/balance Check the balance of the specified account
     *
     * @param accountId
     * @return
     */
    public BalanceResponse balance(String accountId) {
        BalanceResponse resp = get("/v1/account/accounts/" + accountId + "/balance", null, new TypeReference<BalanceResponse<Balance>>() {
        });
        return resp;
    }

    /**
     * POST /v1/order/orders/{order-id}/submitcancel Request to cancel an order request
     *
     * @param orderId
     * @return
     */
    public SubmitcancelResponse submitcancel(String orderId) {
        SubmitcancelResponse resp = post("/v1/order/orders/" + orderId + "/submitcancel", null, new TypeReference<SubmitcancelResponse>() {
        });
        return resp;
    }

    /**
     * POST /v1/order/orders/batchcancel Bulk cancellation of order
     *
     * @param orderList
     * @return
     */
    public BatchcancelResponse submitcancels(List orderList) {
          Map<String, List> parameterMap = new HashMap();
          parameterMap.put("order-ids", orderList);
          BatchcancelResponse resp = post("/v1/order/orders/batchcancel", parameterMap, new TypeReference<BatchcancelResponse<Batchcancel<List, List<BatchcancelBean>>>>() {
          });
          return resp;
    }

    /**
     * GET /v1/order/orders/{order-id} Inquire about the details of an order
     *
     * @param orderId
     * @return
     */
    public OrdersDetailResponse ordersDetail(String orderId) {
        OrdersDetailResponse resp = get("/v1/order/orders/" + orderId, null, new TypeReference<OrdersDetailResponse>() {
        });
        return resp;
    }


    /**
     * GET /v1/order/orders/{order-id}/matchresults Query the transaction details of an order
     *
     * @param orderId
     * @return
     */
    public MatchresultsOrdersDetailResponse matchresults(String orderId) {
        MatchresultsOrdersDetailResponse resp = get("/v1/order/orders/" + orderId + "/matchresults", null, new TypeReference<MatchresultsOrdersDetailResponse>() {
        });
        return resp;
    }

    public IntrustDetailResponse intrustOrdersDetail(IntrustOrdersDetailRequest req) {
        HashMap map = new HashMap();
        map.put("symbol", req.symbol);
        map.put("states", req.states);
        if (req.startDate!=null) {
            map.put("startDate",req.startDate);
 		}
         if (req.startDate!=null) {
             map.put("start-date",req.startDate);
  		}
         if (req.endDate!=null) {
             map.put("end-date",req.endDate);
  		}
         if (req.types!=null) {
             map.put("types",req.types);
  		}
         if (req.from!=null) {
             map.put("from",req.from);
  		}
         if (req.direct!=null) {
             map.put("direct",req.direct);
  		}
         if (req.size!=null) {
             map.put("size",req.size);
  		}
        IntrustDetailResponse resp = get("/v1/order/orders/", map, new TypeReference<IntrustDetailResponse<List<IntrustDetail>>>() {
        });
        return resp;
    }

    // send a GET request.
    <T> T get(String uri, Map<String, String> params, TypeReference<T> ref) {
        if (params == null) {
            params = new HashMap<>();
        }
        return call("GET", uri, null, params, ref);
    }
    <T> String getReturnString(String uri, Map<String, String> params, TypeReference<T> ref) {
        if (params == null) {
            params = new HashMap<>();
        }
        return callReturnString("GET", uri, null, params, ref);
    }
    // send a POST request.
    <T> T post(String uri, Object object, TypeReference<T> ref) {
        return call("POST", uri, object, new HashMap<String, String>(), ref);
    }
    <T> String postReturnString(String uri, Object object, TypeReference<T> ref) {
        return callReturnString("POST", uri, object, new HashMap<String, String>(), ref);
    }

    // call api by endpoint.
    <T> T call(String method, String uri, Object object, Map<String, String> params,
                            TypeReference<T> ref) {
        ApiSignature sign = new ApiSignature();
        sign.createSignature(this.accessKeyId, this.accessKeySecret, method, API_HOST, uri, params);
        try {
            Request.Builder builder = null;
            if ("POST".equals(method)) {
                RequestBody body = RequestBody.create(JSON, JsonUtil.writeValue(object));
                builder = new Request.Builder().url(API_URL + uri + "?" + toQueryString(params)).post(body);
            } else {
                builder = new Request.Builder().url(API_URL + uri + "?" + toQueryString(params)).get();
            }
            if (this.assetPassword != null) {
                builder.addHeader("AuthData", authData());
            }
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            String s = response.body().string();
            return JsonUtil.readValue(s, ref);
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    <T> String callReturnString(String method, String uri, Object object, Map<String, String> params,
                                TypeReference<T> ref) {
        ApiSignature sign = new ApiSignature();
        sign.createSignature(this.accessKeyId, this.accessKeySecret, method, API_HOST, uri, params);
        try {
            Request.Builder builder = null;
            if ("POST".equals(method)) {
                RequestBody body = RequestBody.create(JSON, JsonUtil.writeValue(object));
                builder = new Request.Builder().url(API_URL + uri + "?" + toQueryString(params)).post(body);
            } else {
                builder = new Request.Builder().url(API_URL + uri + "?" + toQueryString(params)).get();
            }
            if (this.assetPassword != null) {
                builder.addHeader("AuthData", authData());
            }
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            String s = response.body().string();
            return s;
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    String authData() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(this.assetPassword.getBytes(StandardCharsets.UTF_8));
        md.update("hello, moto".getBytes(StandardCharsets.UTF_8));
        Map<String, String> map = new HashMap<>();
        map.put("assetPwd", DatatypeConverter.printHexBinary(md.digest()).toLowerCase());
        try {
            return ApiSignature.urlEncode(JsonUtil.writeValue(map));
        } catch (IOException e) {
            throw new RuntimeException("Get json failed: " + e.getMessage());
        }
    }

    // Encode as "a=1&b=%20&c=&d=AAA"
    String toQueryString(Map<String, String> params) {
        return String.join("&", params.entrySet().stream().map((entry) -> {
            return entry.getKey() + "=" + ApiSignature.urlEncode(entry.getValue());
        }).collect(Collectors.toList()));
    }

    // create OkHttpClient:
    static OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder().connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS).writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    static String getHost() {
        String host = null;
        try {
            host = new  URL(API_URL).getHost();
        } catch (MalformedURLException e) {
            System.err.println("parse API_URL error,system exit!,please check API_URL:" + API_URL );
            System.exit(0);
        }
        return host;
    }

}


/**
 * API signature, signature specification：
 * <p>
 * http://docs.aws.amazon.com/zh_cn/general/latest/gr/signature-version-2.html
 *
 * @Date 2018/1/14
 * @Time 16:02
 */
class ApiSignature {

    final Logger log = LoggerFactory.getLogger(getClass());

    static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    static final ZoneId ZONE_GMT = ZoneId.of("Z");

    /**
     * Create a valid signature. This method is a client call, and AccessKeyId, Timestamp, SignatureVersion, SignatureMethod, Signature parameters will be added to the incoming params.
     *
     * @param appKey       AppKeyId.
     * @param appSecretKey AppKeySecret.
     * @param method       Request method, "GET" or "POST"
     * @param host         Request domain name, for example "be.huobi.com"
     * @param uri          Request path, note no? And the parameters after that, such as "/v1/ API /info"
     * @param params       The original request parameter is stored as key-value. Note that Value should not be encoded
     */
    public void createSignature(String appKey, String appSecretKey, String method, String host,
                                String uri, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(method.toUpperCase()).append('\n') // GET
                .append(host.toLowerCase()).append('\n') // Host
                .append(uri).append('\n'); // /path
        params.remove("Signature");
        params.put("AccessKeyId", appKey);
        params.put("SignatureVersion", "2");
        params.put("SignatureMethod", "HmacSHA256");
        params.put("Timestamp", gmtNow());
        // build signature:
        SortedMap<String, String> map = new TreeMap<>(params);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append('=').append(urlEncode(value)).append('&');
        }
        // remove last '&':
        sb.deleteCharAt(sb.length() - 1);
        // sign:
        Mac hmacSha256 = null;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secKey =
                    new SecretKeySpec(appSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key: " + e.getMessage());
        }
        String payload = sb.toString();
        byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String actualSign = Base64.getEncoder().encodeToString(hash);
        params.put("Signature", actualSign);

        if (log.isDebugEnabled()) {
            log.debug("Dump parameters:");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                log.debug("  key: " + entry.getKey() + ", value: " + entry.getValue());
            }
        }
    }


    /**
     * Use the standard URL Encode encoding. Note that, unlike the JDK default, Spaces are encoded as %20 instead of +.
     *
     * @param s String
     * @return Url-encoded string
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported!");
        }
    }

    /**
     * Return epoch seconds
     */
    long epochNow() {
        return Instant.now().getEpochSecond();
    }

    String gmtNow() {
        return Instant.ofEpochSecond(epochNow()).atZone(ZONE_GMT).format(DT_FORMAT);
    }
}

/*class JsonUtil {

    public static String writeValue(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T readValue(String s, TypeReference<T> ref) throws IOException {
        return objectMapper.readValue(s, ref);
    }

    static final ObjectMapper objectMapper = createObjectMapper();

    static ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // disabled features:
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

}*/
