package io.cofix.hedging.service;

import com.huobi.model.trade.Order;
import io.cofix.hedging.service.HedgingJobService;
import io.cofix.hedging.service.HedgingPoolService;
import io.cofix.hedging.service.TradeMarketService;
import io.cofix.hedging.service.serviceImpl.HedgingJobServiceImpl;
import io.cofix.hedging.vo.PoolAmountVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private HedgingJobService hedgingJob = new HedgingJobServiceImpl();

    @Test
    public void not_change_no_action() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        BigDecimal erc20Threshold = new BigDecimal("1");
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

    @Test
    public void buy_eth_less_hbtc_from_market() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        BigDecimal erc20Threshold = new BigDecimal("0.0001");
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

        verify(mockTradeMarketService, times(1)).sendBuyMarketOrder(eq(mockHedgingPoolService.getSymbol()), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
    }

    @Test
    public void buy_eth_equal_hbtc_from_market() {
        BigDecimal ethThreshold = new BigDecimal("0.0001");
        BigDecimal erc20Threshold = new BigDecimal("0.0001");
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

        verify(mockTradeMarketService, times(1)).sendBuyMarketOrder(eq(mockHedgingPoolService.getSymbol()), anyString());
        verify(mockTradeMarketService, never()).sendSellMarketOrder(anyString(), anyString());
    }
}
