package io.cofix.hedging.service;

import com.huobi.model.trade.Order;
import io.cofix.hedging.service.serviceImpl.HedgingJobServiceImpl;
import io.cofix.hedging.vo.PoolAmountVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
//@ComponentScan("io.cofix.hedging.*")
public class HedgingJobTests {
    @Mock
    private HedgingPoolService mockHedgingPoolService;

    @Mock
    private TradeMarketService mockTradeMarketService;

    @InjectMocks
    private final HedgingJobService hedgingJob = new HedgingJobServiceImpl();

    /**
     * 池子中的交易币数量没有变化（1000000000000000000），不调用市场接口进行对冲
     */
    @Test
    public void not_change_no_action() {
/*
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        BigDecimal erc20Threshold = new BigDecimal("1");
*/
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
//        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(erc20Threshold);

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");
//        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(BigDecimal.ONE);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(BigDecimal.ONE);
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                        BigInteger.valueOf(100),
                        new BigInteger("1000000000000000000"),
                        new BigInteger("1000000000000000000"),
                        BigInteger.valueOf(18)));
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
    }

    /**
     * 初始ETH为1eth，erc20为1，eth:erc20=1:3
     * 变化为1.5eth, erc20为1.5.变化量为多了0.5eth,多了0.5erc20
     * 此时应该在交易市场无交易
     */
    @Test
    public void all_up_change_no_action() {
/*
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        BigDecimal erc20Threshold = new BigDecimal("1");
*/
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
//        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(erc20Threshold);

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1500000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");
//        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(BigDecimal.ONE);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(BigDecimal.ONE);
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                        BigInteger.valueOf(100),
                        new BigInteger("1000000000000000000"),
                        new BigInteger("1000000000000000000"),
                        BigInteger.valueOf(18)));
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
    }

    /**
     * 初始ETH为1eth，erc20为1，eth:erc20=1:3
     * 变化为0.5eth, erc20为0.5.变化量为少了0.5eth,少了0.5erc20
     * 此时应该在交易市场无交易
     */
    @Test
    public void all_down_change_no_action() {
/*
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        BigDecimal erc20Threshold = new BigDecimal("1");
*/
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
//        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(erc20Threshold);

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("500000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");
//        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(BigDecimal.ONE);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(BigDecimal.ONE);
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                        BigInteger.valueOf(100),
                        new BigInteger("1000000000000000000"),
                        new BigInteger("1000000000000000000"),
                        BigInteger.valueOf(18)));
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
    }

    /**
     * 初始ETH为1eth，erc20为1，eth:erc20=1:3
     * 变化为0.5eth, erc20为2.2.变化量为少了0.5eth,多了1.2erc20
     * 此时应该在交易市场买入0.4eth,卖出1.2erc20
     */
    @Test
    public void buy_eth_more_hbtc_from_market() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
//        BigDecimal erc20Threshold = new BigDecimal("0.0001");
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(erc20Threshold);
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("500000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("2700000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getDeltaEth()).thenReturn(new BigDecimal("-500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDeltaErc20()).thenReturn(new BigDecimal("1200000000000000000"));
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                        BigInteger.valueOf(100),
                        new BigInteger("1000000000000000000"),
                        new BigInteger("1000000000000000000"),
                        BigInteger.valueOf(18)));
        Order   order   = new Order();
        order.setState("filled");
        Mockito.when(mockTradeMarketService.getOrderById(anyLong())).thenReturn(order);
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, times(1)).sendBuyMarketOrder(eq(mockHedgingPoolService.getSymbol()), eq("1.200000000000000000"));
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
    }

    /**
     * 初始ETH为1eth，erc20为1，eth:erc20=1:3
     * 变化为0.5eth, erc20为2.7.变化量为少了0.5eth,多了1.7erc20
     * 此时应该在交易市场买入0.5eth,卖出1.5erc20
     */
    @Test
    public void buy_eth_less_hbtc_from_market() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
//        BigDecimal erc20Threshold = new BigDecimal("0.0001");
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(erc20Threshold);
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("500000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("2700000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getDeltaEth()).thenReturn(new BigDecimal("-500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDeltaErc20()).thenReturn(new BigDecimal("1700000000000000000"));
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                        BigInteger.valueOf(100),
                        new BigInteger("1000000000000000000"),
                        new BigInteger("1000000000000000000"),
                        BigInteger.valueOf(18)));
        Order   order   = new Order();
        order.setState("filled");
        Mockito.when(mockTradeMarketService.getOrderById(anyLong())).thenReturn(order);
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, times(1)).sendBuyMarketOrder(eq(mockHedgingPoolService.getSymbol()), eq("1.500000000000000000"));
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
    }

    /**
     * 初始ETH为1eth，erc20为1，eth:erc20=1:3
     * 变化为0.5eth, erc20为2.5.变化量为少了0.5eth,多了1.5erc20
     * 此时应该在交易市场买入0.5eth,卖出1.5erc20
     */
    @Test
    public void buy_eth_equal_hbtc_from_market() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
//        BigDecimal erc20Threshold = new BigDecimal("0.0001");
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
//        Mockito.when(mockHedgingPoolService.getErc20Threshold()).thenReturn(erc20Threshold);
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("500000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("2500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getDeltaEth()).thenReturn(new BigDecimal("-500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDeltaErc20()).thenReturn(new BigDecimal("1500000000000000000"));
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                        BigInteger.valueOf(100),
                        new BigInteger("1000000000000000000"),
                        new BigInteger("1000000000000000000"),
                        BigInteger.valueOf(18)));
        Order   order   = new Order();
        order.setState("filled");
        Mockito.when(mockTradeMarketService.getOrderById(anyLong())).thenReturn(order);
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, times(1)).sendBuyMarketOrder(eq(mockHedgingPoolService.getSymbol()), eq("1.500000000000000000"));
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
    }

    /**
     * 初始ETH为1eth，erc20为3，eth:erc20=1:3
     * 变化为1.5eth, erc20为1.5.变化量为多了0.5eth,少了1.5erc20
     * 此时应该在交易市场卖出0.5eth,买入1.5erc20
     */
    @Test
    public void sell_eth_equal_hbtc_from_market() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("3000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1500000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getDeltaEth()).thenReturn(new BigDecimal("500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDeltaErc20()).thenReturn(new BigDecimal("-1500000000000000000"));
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                BigInteger.valueOf(100),
                new BigInteger("1000000000000000000"),
                new BigInteger("3000000000000000000"),
                BigInteger.valueOf(18)));
        Order   order   = new Order();
        order.setState("filled");
        Mockito.when(mockTradeMarketService.getOrderById(anyLong())).thenReturn(order);
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, times(1)).sendSellMarketOrder(eq(mockHedgingPoolService.getSymbol()), eq("0.500000000000000000"));
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
    }

    /**
     * 初始ETH为1eth，erc20为3，eth:erc20=1:3
     * 变化为1.5eth, erc20为1.8.变化量为多了0.5eth,少了1.2erc20
     * 此时应该在交易市场卖出0.4eth,买入1.2erc20
     */
    @Test
    public void sell_eth_more_hbtc_from_market() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("3000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1500000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1800000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getDeltaEth()).thenReturn(new BigDecimal("500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDeltaErc20()).thenReturn(new BigDecimal("-1200000000000000000"));
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                        BigInteger.valueOf(100),
                        new BigInteger("1000000000000000000"),
                        new BigInteger("3000000000000000000"),
                        BigInteger.valueOf(18)));
        Order   order   = new Order();
        order.setState("filled");
        Mockito.when(mockTradeMarketService.getOrderById(anyLong())).thenReturn(order);
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, times(1)).sendSellMarketOrder(eq(mockHedgingPoolService.getSymbol()), eq("0.400000000000000000"));
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
    }

    /**
     * 初始ETH为1eth，erc20为3，eth:erc20=1:3
     * 变化为1.5eth, erc20为1.3.变化量为多了0.5eth,少了1.7erc20
     * 此时应该在交易市场卖出0.5eth,买入1.5erc20
     */
    @Test
    public void sell_eth_less_hbtc_from_market() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        Mockito.when(mockHedgingPoolService.getExchangePrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1000000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("3000000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getEthThreshold()).thenReturn(ethThreshold);
        Mockito.when(mockHedgingPoolService.getSymbol()).thenReturn("ethbtc");

        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
        verify(mockHedgingPoolService, times(1)).setOldPoolAmountVo(any());

        Mockito.when(mockHedgingPoolService.getBalance()).thenReturn(BigInteger.valueOf(1));
        Mockito.when(mockHedgingPoolService.getTotalSupply()).thenReturn(BigInteger.valueOf(100));
        Mockito.when(mockHedgingPoolService.getEth()).thenReturn(new BigInteger("1500000000000000000"));
        Mockito.when(mockHedgingPoolService.getErc20()).thenReturn(new BigInteger("1300000000000000000"));
        Mockito.when(mockHedgingPoolService.getDecimals()).thenReturn(BigInteger.valueOf(18));
        Mockito.when(mockHedgingPoolService.getDeltaEth()).thenReturn(new BigDecimal("500000000000000000"));
        Mockito.when(mockHedgingPoolService.getDeltaErc20()).thenReturn(new BigDecimal("-1700000000000000000"));
        Mockito.when(mockHedgingPoolService.getOldPoolAmountVo()).thenReturn(
                new PoolAmountVo(BigInteger.valueOf(1),
                        BigInteger.valueOf(100),
                        new BigInteger("1000000000000000000"),
                        new BigInteger("3000000000000000000"),
                        BigInteger.valueOf(18)));
        Order   order   = new Order();
        order.setState("filled");
        Mockito.when(mockTradeMarketService.getOrderById(anyLong())).thenReturn(order);
        hedgingJob.hedgingPool(mockHedgingPoolService, mockTradeMarketService);

        verify(mockTradeMarketService, times(1)).sendSellMarketOrder(eq(mockHedgingPoolService.getSymbol()), eq("0.500000000000000000"));
        verify(mockTradeMarketService, never()).sendBuyMarketOrder(anyString(), anyString());
    }

}
