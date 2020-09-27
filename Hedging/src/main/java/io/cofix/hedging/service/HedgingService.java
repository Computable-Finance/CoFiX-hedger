package io.cofix.hedging.service;

import io.cofix.hedging.vo.PoolAmountVo;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ClassName:MiningService
 * Description:
 */
public interface HedgingService {

    void updateAddress(String address);

    String selectAddress();

    /**
     * Check your wallet address
     *
     * @return
     */
    String selectUserWalletAddress();


    BigInteger balanceOfUSDT();

    BigInteger balanceOfLockUSDT();

    BigInteger balanceDecimalsOfUSDT();

    BigInteger totalSupplyOfUSDT();

    BigInteger totalSupplyDecimalsOfUSDT();

    BigInteger balanceOfEthOfUSDT();

    BigInteger balanceDecimalsOfEthOfUSDT();

    BigInteger balanceOfUsdtOfUSDT();

    BigInteger balanceDecimalsOfUsdtOfUSDT();

    /**
     *
     */
    BigInteger balanceOfHBTC();

    BigInteger balanceOfLockHBTC();

    BigInteger balanceDecimalsOfHBTC();

    BigInteger totalSupplyOfHBTC();

    /**
     * 初始化节点URL
     * @param node
     */
    void UpdateNode(String node);

    String getNode();

    BigInteger totalSupplyDecimalsOfHBTC();

    BigInteger balanceOfEthOfHBTC();

    BigInteger balanceDecimalsOfEthOfHBTC();

    BigInteger balanceOfHbtcOfHBTC();

    BigInteger balanceDecimalsOfHbtcOfHBTC();

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

    /**
     * Get OLD pool amount
     * @return
     */
    PoolAmountVo getOldPoolAmountVo();

    /**
     * Set NEW pool amount
     * @param newPoolAmountVo
     */
    void setOldPoolAmountVo(PoolAmountVo newPoolAmountVo);

    /**
     * Add Delta Eth
     * @param deltaEth
     */
    void addDeltaEth(BigDecimal deltaEth);

    /**
     * Add Delta ERC20
     * @param deltaErc20
     */
    void addDeltaErc20(BigDecimal deltaErc20);

    /**
     * get Accumulate ETH
     * @return
     */
    BigDecimal getDeltaEth();

    /**
     * Get Accumulate ERC20
     * @return
     */
    BigDecimal getDeltaErc20();

    /**
     * Buy ETH from houbi
     */
    void buyEth();

    /**
     * Sell ETH from houbi
     */
    void sellEth();

    /**
     * Buy ERC20 from houbi.
     */
    void buyErc20();

    /**
     * Sell ERC20 from houbi.
     */
    void sellErc20();
}
