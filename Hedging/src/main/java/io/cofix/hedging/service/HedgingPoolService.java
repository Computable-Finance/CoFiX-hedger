package io.cofix.hedging.service;

import io.cofix.hedging.vo.PoolAmountVo;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface HedgingPoolService {
    BigInteger  getBalance();
    BigInteger  getTotalSupply();
    BigInteger  getEth();
    BigInteger  getErc20();
    BigInteger  getDecimals();

    PoolAmountVo    getOldPoolAmountVo();
    void setOldPoolAmountVo(PoolAmountVo oldPoolAmountVo);

    void addDeltaEth(BigDecimal deltaEth);
    void addDeltaErc20(BigDecimal deltaErc20);

    BigDecimal  getDeltaEth();
    BigDecimal  getDeltaErc20();

    BigDecimal  getExchangePrice();

    /**
     * Get trade pair. eg: ethusdt
     * @return
     */
    String  getSymbol();

    BigDecimal getEthThreshold();
    BigDecimal getErc20Threshold();

    void setEthThreshold(BigDecimal threshold);
    void setErc20Threshold(BigDecimal threshold);
}
