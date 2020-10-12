package io.cofix.hedging.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HedgingPoolTests {
    @InjectMocks
    private final HedgingPool hedgingPool  = new HedgingPool();

    @Test
    public void clearAccAmountTest() {
        hedgingPool.setPendingAccEth(BigDecimal.ONE);
        hedgingPool.setPendingAccErc20(BigDecimal.TEN);
        assertEquals(BigDecimal.ONE, hedgingPool.getPendingAccEth());
        assertEquals(BigDecimal.TEN, hedgingPool.getPendingAccErc20());

        hedgingPool.clearPendingAccAmount();
        assertEquals(BigDecimal.ZERO, hedgingPool.getPendingAccEth());
        assertEquals(BigDecimal.ZERO, hedgingPool.getPendingAccErc20());
    }
}
