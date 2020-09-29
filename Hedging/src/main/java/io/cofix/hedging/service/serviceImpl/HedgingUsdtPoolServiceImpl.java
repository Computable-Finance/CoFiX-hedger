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
public class HedgingUsdtPoolServiceImpl implements HedgingPoolService {
    @Autowired
    private HedgingService hedgingService;

    @Autowired
    private TransactionService transactionService;

    private PoolAmountVo poolAmountVo;

    private BigDecimal deltaEth;
    private BigDecimal deltaErc20;

    private BigDecimal ethThreshold;
    private BigDecimal erc20Threshold;

    public HedgingUsdtPoolServiceImpl() {
        this.deltaEth = BigDecimal.ZERO;
        this.deltaErc20 = BigDecimal.ZERO;

        this.ethThreshold = BigDecimal.ONE.multiply(Constant.UNIT_ETH);
        this.erc20Threshold = BigDecimal.ONE.multiply(Constant.UNIT_USDT);
    }

    /**
     * ETH/USDT Individual total share = trading pool share + lock-up share
     *
     * @return
     */
    @Override
    public BigInteger getBalance() {

        BigInteger balanceOfUSDT = hedgingService.balanceOfUSDT();
        BigInteger balanceOfLockUSDT = hedgingService.balanceOfLockUSDT();

        if (balanceOfLockUSDT == null || balanceOfUSDT == null) {
            return null;
        }
        log.info("Trading pool share=" + balanceOfUSDT);
        log.info("Lock up share=" + balanceOfLockUSDT);

        BigInteger balance = balanceOfLockUSDT.add(balanceOfUSDT);
        log.info("Total individual share=" + balance);

        return balance;
    }

    @Override
    public BigInteger getTotalSupply() {
        return hedgingService.totalSupplyOfUSDT();
    }

    @Override
    public BigInteger getEth() {
        return hedgingService.balanceOfEthOfUSDT();
    }

    @Override
    public BigInteger getErc20() {
        return hedgingService.balanceOfUsdtOfUSDT();
    }

    @Override
    public BigInteger getDecimals() {
        return hedgingService.balanceDecimalsOfUsdtOfUSDT();
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
        String symbolUrl = Constant.HUOBI_API + "ethusdt";

        log.info(symbolUrl);
        BigDecimal price = transactionService.getExchangePrice(symbolUrl);
        log.info("ETH/USDT: " + price);
        return price;
    }

    @Override
    public String getSymbol() {
        return "ethusdt";
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
