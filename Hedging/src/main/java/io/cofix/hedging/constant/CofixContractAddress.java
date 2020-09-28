package io.cofix.hedging.constant;

import io.cofix.hedging.model.ContractPairAddr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class CofixContractAddress {

    @Value("${cofix.weth.contract.address}")
    private String weth;

    @Value("${cofix.ethhbtc.hbtc-token.contract.address}")
    private String hbtcToken;
    @Value("${cofix.ethhbtc.trading-pool.contract.address}")
    private String hbtcPair;
    @Value("${cofix.ethhbtc.lock-pool.address}")
    private String hbtcLock;


    @Value("${cofix.ethusdt.usdt-token.contract.address}")
    private String usdtToken;
    @Value("${cofix.ethusdt.trading-pool.contract.address}")
    private String usdtPair;
    @Value("${cofix.ethusdt.lock-pool.address}")
    private String usdtLock;

    /**
     * Test network contract address
     */
    public static ContractPairAddr<String> HBTC_ADDR = new ContractPairAddr<String>(
            "",
            "",
            "",
            "",
            "");

    /**
     * Test network contract address
     */
    public static ContractPairAddr<String> USDT_ADDR = new ContractPairAddr(
            "",
            "",
            "",
            "",
            "");

    /**
     * Query trade pairs
     */
    public static String ICFIX_FACTORY = "0xB19EbE64A0ca9626824abBdbdeC4a76294D460A5";

    /**
     * Query lock up
     */
    public static String LOCK_FACTORY_ADDRESS = "0xB6ae9774D2C743B0886123A1C98d4fc92558BaBC";


    /**
     * Modify the contract address
     */
    public static void updateWethAddress(String weth) {
        HBTC_ADDR.setWeth(weth);
        USDT_ADDR.setWeth(weth);
    }

    /**
     * Modify ETH/HBTC contract address
     */
    public static void updateHbtcAddress(String token,String pair,String lock) {
        HBTC_ADDR.setToken(token);
        HBTC_ADDR.setPair(pair);
        HBTC_ADDR.setLiqidity(pair);
        HBTC_ADDR.setLock(lock);
    }

    /**
     * Modify the ETH/USDT contract address
     */
    public static void updateUsdtAddress(String token,String pair,String lock) {
        USDT_ADDR.setToken(token);
        USDT_ADDR.setPair(pair);
        USDT_ADDR.setLiqidity(pair);
        USDT_ADDR.setLock(lock);
    }

    @PostConstruct
    public void initContractAddress(){

        HBTC_ADDR = new ContractPairAddr<String>(
                hbtcToken,
                weth,
                hbtcPair,
                hbtcPair,
                hbtcLock);

        USDT_ADDR = new ContractPairAddr(
                usdtToken,
                weth,
                usdtPair,
                usdtPair,
                usdtLock);
    }
}
