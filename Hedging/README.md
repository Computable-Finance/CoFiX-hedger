[toc]

***

## CoFiX Hedge program
### 配置
#### application.properties

- Mainnet
 
| 配置参数 | 合约名称 | 合约地址 |
|--------|---------|--------|
| cofix.weth.contract.address | WETH | 0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2 |
| cofix.icofix-factory.contract.address | CoFiXFactory | 0x66C64ecC3A6014733325a8f2EBEE46B4CA3ED550 |
| cofix.lock-factory.contract.address | CoFiXVaultForLP | 0x6903b1C17A5A0A9484c7346E5c0956027A713fCF |

- Ropsten
 
| 配置参数 | 合约名称 | 合约地址 |
|--------|---------|--------|
| cofix.weth.contract.address | WETH | 0x59b8881812ac484ab78b8fc7c10b2543e079a6c3 |
| cofix.icofix-factory.contract.address | CoFiXFactory | 0xC85987c73300CFd1838da40F0A4b29bB64EAed8e |
| cofix.lock-factory.contract.address | CoFiXVaultForLP | 0x7e6dCD3581d596fe5F628B77fd6784F10D09b43d |

### 页面配置
- Mainnet

| trading pairs | erc20 token contract address |
| ----- | ------ |
|ethusdt|0xdAC17F958D2ee523a2206206994597C13D831ec7|
|ethbtc|0x0316EB71485b0Ab14103307bf65a021042c6d380|

- Ropsten

| trading pairs | erc20 token contract address |
| ----- | ------ |
|ethusdt|0x200506568C2980B4943B5EaA8713A5740eb2c98A|
|ethbtc|0xA674f71ce49CE7F298aea2F23D918d114965eb40|

### 个人份额查询

```
    
    /**
     * Individual total share = trading pool share + lock-up share
     *
     * @return
     */
    public BigInteger getBalance() {
        String address = hedgingService.selectAddress();

        BigInteger balanceOfPair = balanceOfPair(address);
        BigInteger balanceOfLock = balanceOfLock(address);
        log.info("The {} Trading pool share :{}", huobiTradingPair.toUpperCase(), balanceOfPair);
        log.info("The {} Lock up share :{}", huobiTradingPair.toUpperCase(), balanceOfLock);

        if (balanceOfPair == null || balanceOfLock == null) {
            return null;
        }
        BigInteger balance = balanceOfPair.add(balanceOfLock);
        log.info("The {} Total individual share :{}", huobiTradingPair.toUpperCase(), balance);

        return balance;
    }

    /**
     * Individual share query in trading pool
     * @param address
     * @return
     */
    public BigInteger balanceOfPair(String address) {

        if (StringUtils.isEmpty(address)) {
            log.error("Please set the market maker's address first !");
            return null;
        }

        try {
            return pair.balanceOf(address).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    
    /**
     * Lock individual share query
     * @param address
     * @return
     */
    public BigInteger balanceOfLock(String address) {

    if (StringUtils.isEmpty(address)) {
        log.error("Please set the market maker's address first !");
        return null;
    }

    if (lock == null) {
        return BigInteger.ZERO;
    }

    try {
        return lock.balanceOf(address).send();
    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
    }
```

### 总份额
```
    /**
     * Total trading pool share query
     * @return
     */
    public BigInteger getTotalSupply() {
        try {
            return liqidity.totalSupply().send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
   /**
    * Trading pool ETH share query
    * @return
    */
   public BigInteger getEth() {
       try {
           return weth.balanceOf(pair.getContractAddress()).send();
       } catch (Exception e) {
           e.printStackTrace();
       }

       return null;
   }

   /**
    * Trading pool ERC20 share query
    * @return
    */
   public BigInteger getErc20() {
       try {
           return token.balanceOf(pair.getContractAddress()).send();
       } catch (Exception e) {
           e.printStackTrace();
       }

       return null;
   }
```


