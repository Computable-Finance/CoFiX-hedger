package io.cofix.hedging.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContractPairAddr<T> {
    /**
     * Address of token contract. Such as HBTC address or USDT address
     */
    private T  token;

    /**
     * WETH address
     */
    private T  weth;

    /**
     * Negotiable certificate (share)
     */
    private T  liqidity;

    /**
     * Contract pair address. eg: HBTC:ETH
     */
    private T  pair;

    /**
     * Lock up
     */
    private T lock;
}
