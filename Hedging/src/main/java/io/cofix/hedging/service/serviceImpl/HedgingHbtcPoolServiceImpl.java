package io.cofix.hedging.service.serviceImpl;

import io.cofix.hedging.constant.Constant;
import io.cofix.hedging.service.TransactionService;
import io.cofix.hedging.service.HedgingPoolService;
import io.cofix.hedging.service.HedgingService;
import io.cofix.hedging.vo.PoolAmountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
@Slf4j
public class HedgingHbtcPoolServiceImpl implements HedgingPoolService {
    @Autowired
    private HedgingService hedgingService;

    @Autowired
    private TransactionService transactionService;

    private PoolAmountVo poolAmountVo;

    private BigDecimal deltaEth;
    private BigDecimal deltaErc20;

    private BigDecimal ethThreshold;
    private BigDecimal erc20Threshold;

    public HedgingHbtcPoolServiceImpl() {
        this.deltaEth = BigDecimal.ZERO;
        this.deltaErc20 = BigDecimal.ZERO;

        this.ethThreshold = BigDecimal.ONE.multiply(Constant.UNIT_ETH);
        this.erc20Threshold = BigDecimal.ONE.multiply(Constant.UNIT_HBTC);
    }

    /**
     * ETH/HBTC Individual total share = trading pool share + lock-up share
     *
     * @return
     */
    @Override
    public BigInteger getBalance() {

        BigInteger balanceOfHBTC = hedgingService.balanceOfHBTC();
        BigInteger balanceOfLockHBTC = hedgingService.balanceOfLockHBTC();
        log.info("Trading pool share=" + balanceOfHBTC);
        log.info("Lock up share=" + balanceOfLockHBTC);

        if (balanceOfHBTC == null || balanceOfLockHBTC == null) {
            return null;
        }
        BigInteger balance = balanceOfHBTC.add(balanceOfLockHBTC);
        log.info("Total individual share=" + balance);

        return balance;
    }

    @Override
    public BigInteger getTotalSupply() {
        return hedgingService.totalSupplyOfHBTC();
    }

    @Override
    public BigInteger getEth() {
        return hedgingService.balanceOfEthOfHBTC();
    }

    @Override
    public BigInteger getErc20() {
        return hedgingService.balanceOfHbtcOfHBTC();
    }

    @Override
    public BigInteger getDecimals() {
        return hedgingService.balanceDecimalsOfEthOfHBTC();
    }

    @Override
    public PoolAmountVo getOldPoolAmountVo() {
        return this.poolAmountVo;
    }

    @Override
    public void setOldPoolAmountVo(PoolAmountVo oldPoolAmountVo) {
        this.poolAmountVo = oldPoolAmountVo;
    }

    @Override
    public void addDeltaEth(BigDecimal deltaEth) {
        if (null == this.deltaEth) {
            this.deltaEth = deltaEth;
        } else {
            this.deltaEth = this.deltaEth.add(deltaEth);
        }
    }

    @Override
    public void addDeltaErc20(BigDecimal deltaErc20) {
        if (null == this.deltaErc20) {
            this.deltaErc20 = deltaErc20;
        } else {
            this.deltaErc20 = this.deltaErc20.add(deltaErc20);
        }
    }

    @Override
    public BigDecimal getDeltaEth() {
        return this.deltaEth;
    }

    @Override
    public BigDecimal getDeltaErc20() {
        return this.deltaErc20;
    }

    @Override
    public BigDecimal getExchangePrice() {
        String symbolUrl = Constant.HUOBI_API + "ethbtc";

        log.info(symbolUrl);
        BigDecimal price = transactionService.getExchangePrice(symbolUrl);
        log.info("ETH/HBTC: " + price);
        return price;
    }

    @Override
    public String getSymbol() {
        return "ethbtc";
    }

    @Override
    public BigDecimal getEthThreshold() {
        return this.ethThreshold;
    }

    @Override
    public BigDecimal getErc20Threshold() {
        return this.erc20Threshold;
    }

    @Override
    public void setEthThreshold(BigDecimal threshold) {
        this.ethThreshold = threshold;
    }

    @Override
    public void setErc20Threshold(BigDecimal threshold) {
        this.erc20Threshold = threshold;
    }
}
