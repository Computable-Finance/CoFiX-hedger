package io.cofix.hedging.service.serviceImpl;

import com.huobi.model.trade.Order;
import io.cofix.hedging.constant.Constant;
import io.cofix.hedging.service.HedgingJobService;
import io.cofix.hedging.service.HedgingPoolService;
import io.cofix.hedging.service.HedgingService;
import io.cofix.hedging.service.TradeMarketService;
import io.cofix.hedging.vo.PoolAmountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.function.Function;

@Component
@Slf4j
public class HedgingJobServiceImpl implements HedgingJobService {


    public static boolean START = false;

    @Autowired
    private HedgingService hedgingService;

//    @Qualifier("mock")
    @Autowired
    private TradeMarketService tradeMarketService;

    @Qualifier("HBTC")
    @Autowired
    private HedgingPoolService hedgingHbtcPoolService;

    @Qualifier("USDT")
    @Autowired
    private HedgingPoolService hedgingUsdtPoolService;

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
    public void hedgingPool(HedgingPoolService hedgingPoolService, TradeMarketService tradeMarketService) {
        BigDecimal price = hedgingPoolService.getExchangePrice();

        if (null == price) {
            log.info("Get price from market failed." + hedgingPoolService.getSymbol());
            return;
        }

        // 自己份额
        BigInteger balance = hedgingPoolService.getBalance();
        // 总份额
        BigInteger totalSupply = hedgingPoolService.getTotalSupply();
        // eth总份额
        BigInteger eth = hedgingPoolService.getEth();
        // erc20总份额
        BigInteger erc20 = hedgingPoolService.getErc20();
        // erc20位数
        BigInteger decimals = hedgingPoolService.getDecimals();

        PoolAmountVo newPoolAmountVo = new PoolAmountVo(balance, totalSupply, eth, erc20, decimals);

        if (balance == null || totalSupply == null
                || eth == null || erc20 == null
                || decimals == null) {
            log.info("未能查询到数据：{}", newPoolAmountVo);
            return;
        }

        // 转换为10的n次方
        BigInteger decimalsPowTen = BigInteger.TEN.pow(decimals.intValue());

        // 之前的交易池状态
        PoolAmountVo oldPoolAmountVo = hedgingPoolService.getOldPoolAmountVo();

        hedgingPoolService.setOldPoolAmountVo(newPoolAmountVo);

        log.info(hedgingPoolService.getSymbol() + " " + newPoolAmountVo);

        // 第一次轮询
        if (null == oldPoolAmountVo) {
            return;
        }

        // 如果没有变化，无需任何处理。
        if (newPoolAmountVo.equals(oldPoolAmountVo)) return;

        BigDecimal myNewEth = newPoolAmountVo.getMyEth();
        BigDecimal myNewErc20 = newPoolAmountVo.getMyErc20();

        BigDecimal deltaEth = myNewEth.subtract(oldPoolAmountVo.getMyEth());
        BigDecimal deltaErc20 = myNewErc20.subtract(oldPoolAmountVo.getMyErc20());

        log.info("Current delta [" + deltaEth.toPlainString() + ", " + deltaErc20.toPlainString() + "]");

        hedgingPoolService.addDeltaEth(deltaEth);
        hedgingPoolService.addDeltaErc20(deltaErc20);

        // 如果周期内资产全部为正或者全部为负，均不需要处理
        if (isAllNegative(deltaEth, deltaErc20) || isAllPositive(deltaEth, deltaErc20)) {
            return;
        }

        // 如果都没有达到阈值，无需处理
        if ((deltaEth.abs().compareTo(hedgingPoolService.getEthThreshold()) < 0) &&
                (deltaErc20.abs().compareTo(hedgingPoolService.getErc20Threshold()) < 0)) {
            return;
        }

        BigDecimal deltaAccEth = hedgingPoolService.getDeltaEth();
        BigDecimal deltaAccErc20 = hedgingPoolService.getDeltaErc20();

        // 现在可以开始对冲了
/*
        if (BigDecimal.ZERO.compareTo(deltaAccEth) > 0)  hedgingService.buyEth(); else hedgingService.sellEth();
        if (BigDecimal.ZERO.compareTo(deltaAccErc20) > 0)  hedgingService.buyErc20(); else hedgingService.sellErc20();
*/

        log.info("进入对冲. deltaAccEth["+deltaAccEth.toPlainString()+"] deltaAccErc20["+deltaAccErc20.toPlainString()+"]");

        // 按交易所的价格折算后的ERC20
        BigDecimal actualErc20 = deltaAccEth.abs()
                .divide(Constant.UNIT_ETH, 18, RoundingMode.HALF_UP)
                .multiply(price)
                .multiply(new BigDecimal(decimalsPowTen));

        log.info("actualErc20={}",actualErc20.toPlainString());

        // 按交易所的价格需要交易的ETH数量
        BigDecimal actualEth   = deltaAccErc20.abs()
                .divide(new BigDecimal(decimalsPowTen), 18, RoundingMode.HALF_UP)
                .divide(price, 18, BigDecimal.ROUND_HALF_UP)
                .multiply(Constant.UNIT_ETH);

        log.info("actualEth={}",actualEth.toPlainString());

        BigDecimal actualDealEth = (deltaAccErc20.abs().compareTo(actualErc20) > 0) ? deltaAccEth.abs() : actualEth;

        log.info("actualDealEth={}",actualDealEth.toPlainString());

        Long orderId;
        String dealEth = actualDealEth.divide(Constant.UNIT_ETH, 18, BigDecimal.ROUND_DOWN).toPlainString();
        if (BigDecimal.ZERO.compareTo(deltaAccEth) > 0) { // 买ETH卖ERC20
            log.info("买ETH卖ERC20");
            orderId = tradeMarketService.sendBuyMarketOrder(hedgingPoolService.getSymbol(), dealEth);
        } else {    // 卖ETH买ERC20
            log.info("卖ETH买ERC20");
            orderId = tradeMarketService.sendSellMarketOrder(hedgingPoolService.getSymbol(), dealEth);
        }

        // 如果交易成功，将delta清掉，没有成功，累计到下一轮周期买卖
        Order order = tradeMarketService.getOrderById(orderId);
        // 完全成交
        if ("filled".equals(order.getState())) {
            log.info("完全成交，将delta清掉");
            hedgingPoolService.addDeltaEth(deltaAccEth.negate());
            hedgingPoolService.addDeltaErc20(actualErc20.negate());
        } else if ("partial-filled".equals(order.getState())) { // 部分成交
            // 没有完全交易成功，可能卖掉了一部分，先撤单，然后delta减掉已经买了的数量
            // 撤单 实际取消结果还是需要查询订单状态
            log.info("进行撤单");
            tradeMarketService.cancelOrder(orderId);
            // 再次查询订单是否取消成功
            Order order2 = tradeMarketService.getOrderById(orderId);
            // 撤单完成后
            if ("canceled".equals(order2.getState())
                    || "partial-canceled".equals(order2.getState())
                    || "filled".equals(order2.getState())) {

                log.info("撤单完成");
                // 订单数量
                BigDecimal amount = order2.getAmount();
                // 已成交数量
                BigDecimal filledAmount = order2.getFilledAmount();
                // 已卖的数量
                if (filledAmount != null && amount != null) {
                    // 已经交易的数量
                    BigDecimal sellAmount = amount.subtract(filledAmount);
                    // 防止接口返回0
                    if (order2.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                        price = order2.getPrice();
                    }

                    BigDecimal selledErc20 = sellAmount.multiply(price);

                    if (BigDecimal.ZERO.compareTo(deltaAccEth) > 0) {
                        selledErc20 = selledErc20.negate();
                    } else {
                        sellAmount = sellAmount.negate();
                    }

                    sellAmount = sellAmount.multiply(Constant.UNIT_ETH);
                    selledErc20 = selledErc20.multiply(new BigDecimal(decimalsPowTen));
                    log.info("完成交易的ETH数量={},ERC20数量={}", sellAmount.toPlainString(), selledErc20.toPlainString());
                    hedgingPoolService.addDeltaEth(sellAmount);
                    hedgingPoolService.addDeltaErc20(selledErc20);

                }
            }
        }
    }

/*
    public void updateThreshold(BigDecimal ethThreshold, BigDecimal usdtThreshold, BigDecimal hbtcThreshold) {
        this.ethThreshold = ethThreshold.multiply(Constant.UNIT_ETH);
        this.usdtThreshold = usdtThreshold.multiply(Constant.UNIT_USDT);
        this.hbtcThreshold = hbtcThreshold.multiply(Constant.UNIT_HBTC);
    }

    public BigDecimal getUsdtThreshold() {
        return usdtThreshold;
    }

    public BigDecimal getEthThreshold() {
        return ethThreshold;
    }

    public BigDecimal getHbtcThreshold() {
        return hbtcThreshold;
    }

*/

    /**
     * 轮询对冲
     */
    @Override
    public void hedging() {
        if (!START) {
            log.info("未开启");
            return;
        }
        log.info(Calendar.getInstance().getTime().toString() + " ==========轮询开始==========");

        hedgingPool(hedgingHbtcPoolService, tradeMarketService);
        hedgingPool(hedgingUsdtPoolService, tradeMarketService);
    }
}
