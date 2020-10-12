package io.cofix.hedging.service.serviceImpl;

import com.huobi.model.trade.Order;
import io.cofix.hedging.constant.Constant;
import io.cofix.hedging.model.HedgingPool;
import io.cofix.hedging.service.HedgingJobService;
import io.cofix.hedging.service.HedgingService;
import io.cofix.hedging.service.TradeMarketService;
import io.cofix.hedging.vo.PoolAmountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
public class HedgingJobServiceImpl implements HedgingJobService {

    public final static String ORDER_DONE = "filled";
    public static boolean START = false;

    //    @Qualifier("mock")
    @Autowired
    private TradeMarketService tradeMarketService;

    @Autowired
    HedgingService hedgingService;


    private boolean isAll(BigDecimal decimalOne, BigDecimal decimalTwo, Function<BigDecimal, Boolean> compare) {
        return compare.apply(decimalOne) && compare.apply(decimalTwo);
    }

    private boolean isAllNegative(BigDecimal decimalOne, BigDecimal decimalTwo) {
        return isAll(decimalOne, decimalTwo, decimal -> BigDecimal.ZERO.compareTo(decimal) >= 0);
    }

    private boolean isAllPositive(BigDecimal decimalOne, BigDecimal decimalTwo) {
        return isAll(decimalOne, decimalTwo, decimal -> BigDecimal.ZERO.compareTo(decimal) <= 0);
    }

    @Override
    public void hedgingPool(HedgingPool hedgingPool, TradeMarketService tradeMarketService) {
        // Huobi exchange price
        BigDecimal price = hedgingPool.getExchangePrice();

        if (null == price) {
            log.info("Get price from market failed." + hedgingPool.getSymbol());
            return;
        }

        // Total individual share
        BigInteger balance = hedgingPool.getBalance();
        // Always share
        BigInteger totalSupply = hedgingPool.getTotalSupply();
        // The eth total number
        BigInteger eth = hedgingPool.getEth();
        // The total number erc20
        BigInteger erc20 = hedgingPool.getErc20();
        // Erc20 digits
        BigInteger decimals = hedgingPool.getDecimals();

        PoolAmountVo newPoolAmountVo = new PoolAmountVo(balance, totalSupply, eth, erc20, decimals);

        log.info("PoolAmountVo：{}", newPoolAmountVo);

        if (balance == null || totalSupply == null
                || eth == null || erc20 == null
                || decimals == null) {
            return;
        }


        // It's 10 to the n
        BigInteger decimalsPowTen = BigInteger.TEN.pow(decimals.intValue());

        // Previous trading pool status
        PoolAmountVo oldPoolAmountVo = hedgingPool.getOldPoolAmountVo();

        hedgingPool.setOldPoolAmountVo(newPoolAmountVo);

        log.info(hedgingPool.getSymbol() + " " + newPoolAmountVo);

        // The first poll
        if (null == oldPoolAmountVo) {
            return;
        }

        // If there is no change, no processing is required.
        if (newPoolAmountVo.equals(oldPoolAmountVo)) return;

        BigDecimal myNewEth = newPoolAmountVo.getMyEth();
        BigDecimal myNewErc20 = newPoolAmountVo.getMyErc20();

        BigDecimal deltaEth = myNewEth.subtract(oldPoolAmountVo.getMyEth());
        BigDecimal deltaErc20 = myNewErc20.subtract(oldPoolAmountVo.getMyErc20());

        hedgingPool.addDeltaEth(deltaEth);
        hedgingPool.addDeltaErc20(deltaErc20);

        // If the assets in the period are all positive or all negative, no treatment is required
        if (isAllNegative(deltaEth, deltaErc20)) {
            log.warn("Negative delta [" + deltaEth.toPlainString() + ", " + deltaErc20.toPlainString() + "]");
            return;
        }

        log.info("Current delta [" + deltaEth.toPlainString() + ", " + deltaErc20.toPlainString() + "]");

        if (isAllPositive(deltaEth, deltaErc20)) {
            return;
        }

        // If none of the thresholds are reached, no processing is required
        if ((deltaEth.abs().compareTo(hedgingPool.getEthThreshold()) < 0) &&
                (deltaErc20.abs().compareTo(hedgingPool.getErc20Threshold()) < 0)) {
            return;
        }

        Order   orderOld = tradeMarketService.getOrderById(hedgingPool.getOrderId(), hedgingPool.getApiKey(), hedgingPool.getSecretKey());
        if ((orderOld != null) && (!ORDER_DONE.equals(orderOld.getState()))) {
            // Order is pending, no more action.
            log.warn("Order not completed.");
            return;
        } else {
            hedgingPool.clearPendingAccAmount();    // Order completed. continue hedging.
        }

        // This is new delta ACC.
        BigDecimal deltaAccEth = hedgingPool.getDeltaEth();
        BigDecimal deltaAccErc20 = hedgingPool.getDeltaErc20();

        // Now you can start hedging
/*
        if (BigDecimal.ZERO.compareTo(deltaAccEth) > 0)  hedgingService.buyEth(); else hedgingService.sellEth();
        if (BigDecimal.ZERO.compareTo(deltaAccErc20) > 0)  hedgingService.buyErc20(); else hedgingService.sellErc20();
*/

        log.info("Enter the hedge. deltaAccEth[" + deltaAccEth.toPlainString() + "] deltaAccErc20[" + deltaAccErc20.toPlainString() + "]");

        // The ERC20 at the exchange price
        BigDecimal actualErc20 = deltaAccEth.abs()
                .divide(Constant.UNIT_ETH, 18, RoundingMode.HALF_UP)
                .multiply(price)
                .multiply(new BigDecimal(decimalsPowTen));

        log.info("actualErc20={}", actualErc20.toPlainString());

        // Number of ETH to be traded at the exchange price
        BigDecimal actualEth = deltaAccErc20.abs()
                .divide(new BigDecimal(decimalsPowTen), decimals.intValue(), RoundingMode.HALF_UP)
                .divide(price, 18, BigDecimal.ROUND_HALF_UP)
                .multiply(Constant.UNIT_ETH);

        log.info("actualEth={}", actualEth.toPlainString());

        BigDecimal actualDealEth = (deltaAccErc20.abs().compareTo(actualErc20) > 0) ? deltaAccEth.abs() : actualEth;

        log.info("actualDealEth={}", actualDealEth.toPlainString());

        Long orderId;
        BigDecimal dealEth = actualDealEth.divide(Constant.UNIT_ETH, 18, BigDecimal.ROUND_DOWN);
        BigDecimal dealErc20 = dealEth.multiply(price);
        if (BigDecimal.ZERO.compareTo(deltaAccEth) > 0) { // Buy the ETH sell ERC20
            log.info("Buy the ETH sell ERC20");
            orderId = tradeMarketService.sendBuyMarketOrder(hedgingPool.getSymbol(), dealErc20.round(new MathContext(4, RoundingMode.DOWN)).toPlainString());
        } else {    // Sell ETH  Buy  ERC20
            log.info("Sell ETH  Buy  ERC20");
            orderId = tradeMarketService.sendSellMarketOrder(hedgingPool.getSymbol(), dealEth.round(new MathContext(4, RoundingMode.DOWN)).toPlainString());
        }

        hedgingPool.setOrderId(orderId);
        hedgingPool.setPendingAccAmount(deltaAccEth, deltaAccErc20);

        // Order status will be check the next polling.
/*
        // If the trade is successful, the delta is cleared, and if it is not successful, it accumulates to the next cycle
        Order order = tradeMarketService.getOrderById(orderId, hedgingPool.getApiKey(), hedgingPool.getSecretKey());

        // Completely to clinch a deal
        if (null != order && "filled".equals(order.getState())) {
            log.info("It's a complete deal. Let's clear the delta");
            hedgingPool.clearPendingAccAmount();
        }  // else we will waiting for complete.
*/
    }


    /**
     * Polling hedge
     */
    @Override
    public void hedging() {
        if (!START) {
            log.info("Did not open");
            return;
        }
        log.info(Calendar.getInstance().getTime().toString() + " ==========Polling began==========");

        List<HedgingPool> hedgingPoolList = hedgingService.getHedgingPoolList();
        if (CollectionUtils.isEmpty(hedgingPoolList)) {
            log.info("The trading pool is empty. Please add a trading pool");
        }

        for (HedgingPool hedgingPool : hedgingPoolList) {
            hedgingPool(hedgingPool, tradeMarketService);
        }
    }
}
