package io.cofix.hedging.model;

import io.cofix.hedging.constant.Constant;
import io.cofix.hedging.contract.ERC20;
import io.cofix.hedging.service.TransactionService;
import io.cofix.hedging.service.HedgingService;
import io.cofix.hedging.vo.PoolAmountVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Slf4j
public class HedgingPool {

    private HedgingService hedgingService;

    private TransactionService transactionService;

    private PoolAmountVo poolAmountVo;

    private BigDecimal deltaEth;
    private BigDecimal deltaErc20;


    private BigDecimal ethThreshold;
    private BigDecimal erc20Threshold;

    private String huobiTradingPair;

    private String tokenName;

    /**
     * Address of token contract. Such as HBTC address or USDT address
     */
    private ERC20 token;

    /**
     * WETH address
     */
    private ERC20 weth;

    /**
     * Negotiable certificate (share)
     */
    private ERC20 liqidity;

    /**
     * Contract pair address. eg: HBTC:ETH
     */
    private ERC20 pair;

    /**
     * Lock up
     */
    private ERC20 lock;

    private BigInteger decimals;
    private Long orderId;

    private String HUOBI_API_KEY;
    private String HUOBI_SECRET_KEY;
    private BigDecimal pendingAccEth;
    private BigDecimal pendingAccErc20;

    public HedgingPool() {
        this.deltaEth = BigDecimal.ZERO;
        this.deltaErc20 = BigDecimal.ZERO;

        this.pendingAccEth  = BigDecimal.ZERO;
        this.pendingAccErc20 = BigDecimal.ZERO;
    }

    public BigDecimal unitDeltaEth() {
        return deltaEth.divide(Constant.UNIT_ETH, 18, RoundingMode.HALF_UP);
    }

    public BigDecimal unitDeltaErc20() {
        return deltaErc20.divide(BigDecimal.TEN.pow(decimals.intValue()), decimals.intValue(), RoundingMode.HALF_UP);
    }

    public BigDecimal unitEthThreshold() {
        return ethThreshold.divide(Constant.UNIT_ETH, 18, RoundingMode.HALF_UP);
    }

    public BigDecimal unitErc20Threshold() {
        return erc20Threshold.divide(BigDecimal.TEN.pow(decimals.intValue()), decimals.intValue(), RoundingMode.HALF_UP);
    }

    public HedgingPool(ERC20 token, ERC20 weth, ERC20 pair, ERC20 lock,
                       String tradingPairs, BigDecimal ethThreshold, BigDecimal erc20Threshold,
                       HedgingService hedgingService, TransactionService transactionService) {
        this.hedgingService = hedgingService;
        this.transactionService = transactionService;

        this.token = token;
        this.weth = weth;
        this.pair = pair;
        this.lock = lock;
        this.liqidity = pair;
        this.huobiTradingPair = tradingPairs;

        this.ethThreshold = ethThreshold;
        this.erc20Threshold = erc20Threshold;

        this.deltaEth = BigDecimal.ZERO;
        this.deltaErc20 = BigDecimal.ZERO;

        this.pendingAccEth  = BigDecimal.ZERO;
        this.pendingAccErc20 = BigDecimal.ZERO;

        try {
            String name = token.name().send();
            this.tokenName = name;
        } catch (Exception e) {
            log.info("获取名称失败");
        }

        try {
            this.decimals = token.decimals().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Individual total share = trading pool share + lock-up share
     *
     * @return
     */
    public BigInteger getBalance() {
        String address = hedgingService.selectAddress();

        BigInteger balanceOfPair = balanceOfPair(address);
        BigInteger balanceOfLock = balanceOfLock(address);
        log.info("The {} Trading pool share :{}", huobiTradingPair.toUpperCase(), balanceOfPair);
        log.info("The {} Lock up share :{}", huobiTradingPair.toUpperCase(), balanceOfLock);

        if (balanceOfPair == null || balanceOfLock == null) {
            return null;
        }
        BigInteger balance = balanceOfPair.add(balanceOfLock);
        log.info("The {} Total individual share :{}", huobiTradingPair.toUpperCase(), balance);

        return balance;
    }

    /**
     * Lock individual share query
     * @param address
     * @return
     */
    public BigInteger balanceOfLock(String address) {

        if (StringUtils.isEmpty(address)) {
            log.error("Please set the market maker's address first !");
            return null;
        }

        if (lock == null) {
            return BigInteger.ZERO;
        }

        try {
            return lock.balanceOf(address).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Individual share query in trading pool
     * @param address
     * @return
     */
    public BigInteger balanceOfPair(String address) {

        if (StringUtils.isEmpty(address)) {
            log.error("Please set the market maker's address first !");
            return null;
        }

        try {
            return pair.balanceOf(address).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Total trading pool share query
     * @return
     */
    public BigInteger getTotalSupply() {
        try {
            return liqidity.totalSupply().send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Trading pool ETH share query
     * @return
     */
    public BigInteger getEth() {
        try {
            return weth.balanceOf(pair.getContractAddress()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Trading pool ERC20 share query
     * @return
     */
    public BigInteger getErc20() {
        try {
            return token.balanceOf(pair.getContractAddress()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public BigInteger getDecimals() {
        return this.decimals;
    }


    public PoolAmountVo getOldPoolAmountVo() {
        return this.poolAmountVo;
    }


    public void setOldPoolAmountVo(PoolAmountVo oldPoolAmountVo) {
        this.poolAmountVo = oldPoolAmountVo;
    }


    public void addDeltaEth(BigDecimal deltaEth) {
        if (null == this.deltaEth) {
            this.deltaEth = deltaEth;
        } else {
            this.deltaEth = this.deltaEth.add(deltaEth);
        }
    }


    public void addDeltaErc20(BigDecimal deltaErc20) {
        if (null == this.deltaErc20) {
            this.deltaErc20 = deltaErc20;
        } else {
            this.deltaErc20 = this.deltaErc20.add(deltaErc20);
        }
    }

    public BigDecimal getDeltaEth() {
        return this.deltaEth;
    }


    public BigDecimal getDeltaErc20() {
        return this.deltaErc20;
    }


    public BigDecimal getExchangePrice() {
        String huobiUrl = Constant.HUOBI_API + huobiTradingPair;

        log.info(huobiUrl);
        BigDecimal price = transactionService.getExchangePrice(huobiUrl, huobiTradingPair);
        log.info("The {} price is :{}", huobiTradingPair.toUpperCase(), price);
        return price;
    }


    public String getSymbol() {
        return huobiTradingPair;
    }


    public BigDecimal getEthThreshold() {
        return this.ethThreshold;
    }


    public BigDecimal getErc20Threshold() {
        return this.erc20Threshold;
    }


    public void setEthThreshold(BigDecimal threshold) {
        this.ethThreshold = threshold;
    }


    public void setErc20Threshold(BigDecimal threshold) {
        this.erc20Threshold = threshold;
    }

    public HedgingService getHedgingService() {
        return hedgingService;
    }

    public void setHedgingService(HedgingService hedgingService) {
        this.hedgingService = hedgingService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public String getHuobiTradingPair() {
        return huobiTradingPair;
    }

    public void setHuobiTradingPair(String huobiTradingPair) {
        this.huobiTradingPair = huobiTradingPair;
    }

    public ERC20 getToken() {
        return token;
    }

    public void setToken(ERC20 token) {
        this.token = token;
    }

    public ERC20 getWeth() {
        return weth;
    }

    public void setWeth(ERC20 weth) {
        this.weth = weth;
    }

    public ERC20 getLiqidity() {
        return liqidity;
    }

    public void setLiqidity(ERC20 liqidity) {
        this.liqidity = liqidity;
    }

    public ERC20 getPair() {
        return pair;
    }

    public void setPair(ERC20 pair) {
        this.pair = pair;
    }

    public ERC20 getLock() {
        return lock;
    }

    public void setLock(ERC20 lock) {
        this.lock = lock;
    }

    public static Logger getLog() {
        return log;
    }

    public PoolAmountVo getPoolAmountVo() {
        return poolAmountVo;
    }

    public void setPoolAmountVo(PoolAmountVo poolAmountVo) {
        this.poolAmountVo = poolAmountVo;
    }

    public void setDeltaEth(BigDecimal deltaEth) {
        this.deltaEth = deltaEth;
    }

    public void setDeltaErc20(BigDecimal deltaErc20) {
        this.deltaErc20 = deltaErc20;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setApiKey(String apiKey) {
        this.HUOBI_API_KEY  = apiKey;
    }

    public String getApiKey() {
        return this.HUOBI_API_KEY;
    }

    public void setSecretKey(String secretKey) {
        this.HUOBI_SECRET_KEY = secretKey;
    }

    public String getSecretKey() {
        return this.HUOBI_SECRET_KEY;
    }

    public BigDecimal getPendingAccEth() {
        return this.pendingAccEth;
    }

    public void setPendingAccEth(BigDecimal deltaAccEth) {
        this.pendingAccEth = deltaAccEth;
    }

    public BigDecimal getPendingAccErc20() {
        return this.pendingAccErc20;
    }

    public void setPendingAccErc20(BigDecimal deltaAccErc20) {
        this.pendingAccErc20 = deltaAccErc20;
    }

    public void setPendingAccAmount(BigDecimal deltaAccEth, BigDecimal deltaAccErc20) {
        log.info("Set pending [{}, {}]", deltaAccEth, deltaAccErc20);

        setPendingAccEth(deltaAccEth);
        setPendingAccErc20(deltaAccErc20);
    }
    public void clearPendingAccAmount() {
        log.info("Clear pending [{}, {}]", this.pendingAccEth, this.pendingAccErc20);

        this.addDeltaEth(this.pendingAccEth.negate());
        this.addDeltaErc20(this.pendingAccErc20.negate());

        this.pendingAccEth  = BigDecimal.ZERO;
        this.pendingAccErc20    = BigDecimal.ZERO;
    }
}
