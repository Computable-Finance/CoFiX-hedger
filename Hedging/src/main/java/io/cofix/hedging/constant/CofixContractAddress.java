package io.cofix.hedging.constant;

import io.cofix.hedging.model.ContractPairAddr;

public interface CofixContractAddress {

    /**
     * Test network contract address
     */
    ContractPairAddr<String> HBTC_ADDR = new ContractPairAddr<String>(
            "0xa674f71ce49ce7f298aea2f23d918d114965eb40",
            "0x59b8881812ac484ab78b8fc7c10b2543e079a6c3",
            "0x241b97312619fc497526521c35eba9c3443fc0cf",
            "0x241b97312619fc497526521c35eba9c3443fc0cf",
            "0x4a03f8DBa6d1FE6E5ca0b9dF0C4CA88E54Fdf49C");

    /**
     * Test network contract address
     */
    ContractPairAddr<String> USDT_ADDR = new ContractPairAddr(
            "0x200506568c2980b4943b5eaa8713a5740eb2c98a",
            "0x59b8881812ac484ab78b8fc7c10b2543e079a6c3",
            "0x6555c4f6377ed3935490c81bcf780af9f66b0dc3",
            "0x6555c4f6377ed3935490c81bcf780af9f66b0dc3",
            "0x48D5199B1af148A1BcA80D1e6332f43717c27849");

    /**
     *  Query trade pairs
     */
    String ICFIX_FACTORY = "0xB19EbE64A0ca9626824abBdbdeC4a76294D460A5";

    /**
     *  Query lock up
     */
    String LOCK_FACTORY_ADDRESS = "0xB6ae9774D2C743B0886123A1C98d4fc92558BaBC";
}
