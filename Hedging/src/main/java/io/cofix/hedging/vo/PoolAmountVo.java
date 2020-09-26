package io.cofix.hedging.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
public class PoolAmountVo {
    private BigInteger balance;
    private BigInteger totalSupply;
    private BigInteger eth;
    private BigInteger erc20;
    private BigInteger decimals;

    @Override
    public String toString() {
        return "Balance[" + balance + "] total supply[" + totalSupply + "] ETH[" + eth
                + "] ERC20[" + erc20 + "] decimals[" + decimals + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;

        PoolAmountVo poolAmountVo = (PoolAmountVo) obj;
        if ((!poolAmountVo.getBalance().equals(this.balance)) ||
                (!poolAmountVo.getTotalSupply().equals(this.totalSupply)) ||
                (!poolAmountVo.getEth().equals(this.eth)) ||
                (!poolAmountVo.getErc20().equals(this.erc20))) {
            return false;
        }

        return true;
    }

    public BigDecimal getMyEth() {
        return new BigDecimal(eth).multiply(new BigDecimal(balance)).divide(new BigDecimal(totalSupply), 18, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getMyErc20() {
        return new BigDecimal(erc20).multiply(new BigDecimal(balance)).divide(new BigDecimal(totalSupply), 18, BigDecimal.ROUND_HALF_UP);
    }
}
