package io.cofix.hedging.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContractPairAddr<T> {
    /**
     * 代币合约地址。比如HBTC地址或者USDT地址
     */
    private T  token;

    /**
     * WETH地址
     */
    private T  weth;

    /**
     * 流通性凭证（份额）
     */
    private T  liqidity;

    /**
     * Contract pair address. eg: HBTC:ETH
     */
    private T  pair;

    /**
     * 锁仓
     */
    private T lock;
}
