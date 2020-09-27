[toc]

***

## CoFiX Hedge program

### CoFiX相关合约地址

```

    // ETH/HBTC交易池:测试网
    ContractPairAddr<String> HBTC_ADDR = new ContractPairAddr<String>(
            // HBTC地址
            "0xa674f71ce49ce7f298aea2f23d918d114965eb40",
            // WETH地址
            "0x59b8881812ac484ab78b8fc7c10b2543e079a6c3",
            // 流通性凭证（份额）
            "0x241b97312619fc497526521c35eba9c3443fc0cf",
            // ETH/HBTC交易池合约地址
            "0x241b97312619fc497526521c35eba9c3443fc0cf",
            // 锁仓地址
            "0x4a03f8DBa6d1FE6E5ca0b9dF0C4CA88E54Fdf49C");

    // ETH/USDT交易池：测试网
    ContractPairAddr<String> USDT_ADDR = new ContractPairAddr(
            // USDT地址
            "0x200506568c2980b4943b5eaa8713a5740eb2c98a",
            // WETH地址
            "0x59b8881812ac484ab78b8fc7c10b2543e079a6c3",
            // 流通性凭证（份额）
            "0x6555c4f6377ed3935490c81bcf780af9f66b0dc3",
            // USDT/ETH交易池合约地址
            "0x6555c4f6377ed3935490c81bcf780af9f66b0dc3",
            // 锁仓地址
            "0x48D5199B1af148A1BcA80D1e6332f43717c27849");
```


### 个人份额查询

```
    
    /**
     * ETH/HBTC 个人总份额=交易池份额+锁仓份额
     * @return
     */
    @Override
    public BigInteger getBalance() {

        BigInteger balanceOfHBTC = hedgingService.balanceOfHBTC();
        BigInteger balanceOfLockHBTC = hedgingService.balanceOfLockHBTC();
        log.info("交易池份额=" + balanceOfHBTC);
        log.info("锁仓份额=" + balanceOfLockHBTC);

        if (balanceOfHBTC == null || balanceOfLockHBTC == null) {
            return null;
        }
        BigInteger balance = balanceOfHBTC.add(balanceOfLockHBTC);
        log.info("个人总份额=" + balance);

        return balance;
    }  

    /**
     * ETH/HBTC 交易池个人份额查询
     *
     * @return
     */
    @Override
    public BigInteger balanceOfHBTC() {

        if (StringUtils.isEmpty(address)){
            log.error("Please set the market maker's address first !");
            return null;
        }

        try {
            return HBTC_ERC20.getPair().balanceOf(address).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * ETH/HBTC 锁仓个人份额查询
     *
     * @return
     */
    @Override
    public BigInteger balanceOfLockHBTC() {

        if (StringUtils.isEmpty(address)){
            log.error("Please set the market maker's address first !");
            return null;
        }

        try {
            return HBTC_ERC20.getLock().balanceOf(address).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    
    /**
     * ETH/USDT个人总份额=交易池份额+锁仓份额
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
        log.info("交易池份额=" + balanceOfUSDT);
        log.info("锁仓份额=" + balanceOfLockUSDT);

        BigInteger balance = balanceOfLockUSDT.add(balanceOfUSDT);
        log.info("个人总份额=" + balance);
        
        return balance;
    }

    
    /**
     * ETH/USDT交易池个人份额 查询
     *
     * @return
     */
    @Override
    public BigInteger balanceOfUSDT() {
        
        if (StringUtils.isEmpty(address)){
            log.error("Please set the market maker's address first !");
            return null;
        }
    
        try {
            return USDT_ERC20.getPair().balanceOf(address).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    
    /**
     * ETH/USDT交易池锁仓个人份额 查询
     *
     * @return
     */
    @Override
    public BigInteger balanceOfLockUSDT() {
        if (StringUtils.isEmpty(address)){
            log.error("Please set the market maker's address first !");
            return null;
        }

        try {
            return USDT_ERC20.getLock().balanceOf(address).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
```

### 总份额
```
    // ETH/HBTC交易池总份额 查询
    @Override
    public BigInteger totalSupplyOfHBTC() {
        try {
            return HBTC_ERC20.getLiqidity().totalSupply().send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
   // ETH/USDT交易池总份额查询
   @Override
   public BigInteger totalSupplyOfUSDT() {
       try {
           return USDT_ERC20.getLiqidity().totalSupply().send();
       } catch (Exception e) {
           e.printStackTrace();
       }

       return null;
   }

    // ETH/HBTC交易池ETH总份额查询
    @Override
    public BigInteger balanceOfEthOfHBTC() {
        try {
            return HBTC_ERC20.getWeth().balanceOf(CofixContractAddress.HBTC_ADDR.getPair()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return null;
    }
    
    // ETH/HBTC交易池 HBTC总份额查询
    @Override
    public BigInteger balanceOfHbtcOfHBTC() {
        try {
            return HBTC_ERC20.getToken().balanceOf(CofixContractAddress.HBTC_ADDR.getPair()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ETH/USDT交易池USDT总份额查询
    @Override
    public BigInteger balanceOfUsdtOfUSDT() {
        try {
            return USDT_ERC20.getToken().balanceOf(CofixContractAddress.USDT_ADDR.getPair()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    // ETH/USDT交易池ETH总份额查询
    @Override
    public BigInteger balanceOfEthOfUSDT() {
        try {
            return USDT_ERC20.getWeth().balanceOf(CofixContractAddress.USDT_ADDR.getPair()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
```


