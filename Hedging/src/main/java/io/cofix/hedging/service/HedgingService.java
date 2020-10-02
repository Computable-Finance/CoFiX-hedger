package io.cofix.hedging.service;

import io.cofix.hedging.contract.ERC20;
import io.cofix.hedging.model.HedgingPool;
import io.cofix.hedging.vo.PoolAmountVo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;


public interface HedgingService {

    List<HedgingPool> getHedgingPoolList();

    void updateAddress(String address);

    String selectAddress();

    void addHegingPool(HedgingPool hedgingPool);

    BigInteger balanceOfPair(ERC20 pair);

    void UpdateNode(String node);

    String getNode();

    /**
     * Update Interval in seconds.
     * @param interval
     * @return
     */
    int updateInterval(Integer interval);

    /**
     * Get scheduler interval.
     * @return
     */
    int getInterval();

    /**
     * Set quartz scheduler job key.
     * @param jobKey
     */
    void setJobKey(String jobKey);

    /**
     * Get quartz scheduler job key.
     * @return
     */
    String getJobKey();

    BigInteger balanceOfLock(ERC20 lock);

    BigInteger totalSupplyOfLiqidity(ERC20 liqidity);

    BigInteger balanceOfEthOfWeth(ERC20 weth, String pairAddress);

    BigInteger balanceOfErc20(ERC20 token, String pairAddress);

    void createHegingPool(String token, String tradingPairs, BigDecimal ethThreshold, BigDecimal erc20Threshold);

    void updateInterval(BigDecimal unitEthThreshold, BigDecimal unitErc20Threshold, String huobiTradingPair);
}
