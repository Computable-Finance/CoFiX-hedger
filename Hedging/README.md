[toc]

***

## CoFiX Hedge program

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


