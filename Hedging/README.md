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
            "0x241b97312619fc497526521c35eba9c3443fc0cf");

    // ETH/USDT交易池：测试网
    ContractPairAddr<String> USDT_ADDR = new ContractPairAddr(
            // USDT地址
            "0x200506568c2980b4943b5eaa8713a5740eb2c98a",
            // WETH地址
            "0x59b8881812ac484ab78b8fc7c10b2543e079a6c3",
            // 流通性凭证（份额）
            "0x6555c4f6377ed3935490c81bcf780af9f66b0dc3",
            // USDT/ETH交易池合约地址
            "0x6555c4f6377ed3935490c81bcf780af9f66b0dc3");
```


### 个人份额查询
```
    // ETH/HBTC交易池个人份额 查询
    @Override
    public BigInteger balanceOfHBTC() {
        try {
            return HBTC_ERC20.getToken().balanceOf(credentials.getAddress()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    // ETH/USDT交易池个人份额 查询
    @Override
    public BigInteger balanceOfUSDT() {
        try {
            return USDT_ERC20.getToken().balanceOf(credentials.getAddress()).send();
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


