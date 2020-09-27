package io.cofix.hedging.service.serviceImpl;

import io.cofix.hedging.constant.CofixContractAddress;
import io.cofix.hedging.contract.ERC20;
import io.cofix.hedging.contract.ICoFiXFactory;
import io.cofix.hedging.contract.LockFactoryContract;
import io.cofix.hedging.model.ContractPairAddr;
import io.cofix.hedging.service.HedgingService;
import io.cofix.hedging.vo.PoolAmountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@Slf4j
public class HedgingServiceImpl implements HedgingService {

    public Web3j web3j;
    public  TransactionManager transactionManager = null;

    public String address;

    public String node;
    private Integer interval;

    private ContractPairAddr<ERC20> HBTC_ERC20;
    private ContractPairAddr<ERC20> USDT_ERC20;

    private String jobKey = null;
    private PoolAmountVo oldPoolAmountVo;
    private BigDecimal deltaEth;
    private BigDecimal deltaErc20;

    HedgingServiceImpl() {
        this.interval = 20;
        this.oldPoolAmountVo = null;
        this.deltaEth = BigDecimal.ZERO;
        this.deltaErc20 = BigDecimal.ZERO;

    }


    /**
     * Check your wallet address
     */
    @Override
    public String selectUserWalletAddress() {
        return address;
    }


    private void updateContract() {
        if (null == web3j) {
            return;
        }

        // HBTC lock address
        //String hbtcLockAddress = getLockAddress(CofixContractAddress.HBTC_ADDR.getToken());

        HBTC_ERC20 = new ContractPairAddr<ERC20>(
                ERC20.load(CofixContractAddress.HBTC_ADDR.getToken(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT),
                ERC20.load(CofixContractAddress.HBTC_ADDR.getWeth(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT),
                ERC20.load(CofixContractAddress.HBTC_ADDR.getLiqidity(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT),
                ERC20.load(CofixContractAddress.HBTC_ADDR.getPair(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT),
                ERC20.load(CofixContractAddress.HBTC_ADDR.getLock(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT)
        );

        // HBTC lock address
        // String usdtLockAddress = getLockAddress(CofixContractAddress.USDT_ADDR.getToken());

        USDT_ERC20 = new ContractPairAddr<ERC20>(
                ERC20.load(CofixContractAddress.USDT_ADDR.getToken(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT),
                ERC20.load(CofixContractAddress.USDT_ADDR.getWeth(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT),
                ERC20.load(CofixContractAddress.USDT_ADDR.getLiqidity(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT),
                ERC20.load(CofixContractAddress.USDT_ADDR.getPair(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT),
                ERC20.load(CofixContractAddress.USDT_ADDR.getLock(), web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT)
        );

        return;
    }

    private String getLockAddress(String tokenAddr) {
        ICoFiXFactory iCoFiXFactory = ICoFiXFactory.load(CofixContractAddress.ICFIX_FACTORY, web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);

        // Lock the warehouse address
        String lockAddress = null;
        try {
            String pair = iCoFiXFactory.getPair(tokenAddr).send();
            LockFactoryContract lockFactoryContract = LockFactoryContract.load(CofixContractAddress.LOCK_FACTORY_ADDRESS, web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);
            lockAddress = lockFactoryContract.stakingPoolForPair(pair).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lockAddress;
    }


    @Override
    public void UpdateNode(String node) {
        web3j = Web3j.build(new HttpService(node));
        this.node = node;

        if (null != transactionManager) {
            updateContract();
        }
    }

    @Override
    public String getNode() {
        return this.node;
    }

    @Override
    public void updateAddress(String address) {
        if (StringUtils.isEmpty(address)) {
            log.error("The address cannot be empty");
            return;
        }
        transactionManager = new ReadonlyTransactionManager(web3j, address);
        updateContract();
        this.address = address;
    }

    @Override
    public String selectAddress() {
        return this.address;
    }

    private BigInteger decimals(ERC20 erc20) {
        try {
            return erc20.decimals().send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ETH/USDT Trading pool individual share query
     *
     * @return
     */
    @Override
    public BigInteger balanceOfUSDT() {

        if (StringUtils.isEmpty(address)) {
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
     * ETH/USDT Query of individual share in the lock of trading pool
     *
     * @return
     */
    @Override
    public BigInteger balanceOfLockUSDT() {
        if (StringUtils.isEmpty(address)) {
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

    @Override
    public BigInteger balanceDecimalsOfUSDT() {
        return decimals(USDT_ERC20.getToken());
    }

    /**
     * ETH/USDT Total trading pool share query
     *
     * @return
     */
    @Override
    public BigInteger totalSupplyOfUSDT() {
        try {
            return USDT_ERC20.getLiqidity().totalSupply().send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BigInteger totalSupplyDecimalsOfUSDT() {
        return decimals(USDT_ERC20.getLiqidity());
    }

    /**
     * ETH/USDT Total number of ETH trading pool
     *
     * @return
     */
    @Override
    public BigInteger balanceOfEthOfUSDT() {
        try {
            return USDT_ERC20.getWeth().balanceOf(CofixContractAddress.USDT_ADDR.getPair()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BigInteger balanceDecimalsOfEthOfUSDT() {
        return decimals(USDT_ERC20.getWeth());
    }

    /**
     * ETH/USDT Trading pool USDT total share query
     *
     * @return
     */
    @Override
    public BigInteger balanceOfUsdtOfUSDT() {
        try {
            return USDT_ERC20.getToken().balanceOf(CofixContractAddress.USDT_ADDR.getPair()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BigInteger balanceDecimalsOfUsdtOfUSDT() {
        return decimals(USDT_ERC20.getToken());
    }

    /**
     * ETH/HBTC Trading pool individual share query
     *
     * @return
     */
    @Override
    public BigInteger balanceOfHBTC() {

        if (StringUtils.isEmpty(address)) {
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
     * ETH/HBTC Lock individual share query
     *
     * @return
     */
    @Override
    public BigInteger balanceOfLockHBTC() {

        if (StringUtils.isEmpty(address)) {
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

    @Override
    public BigInteger balanceDecimalsOfHBTC() {
        return decimals(HBTC_ERC20.getToken());
    }

    /**
     * ETH/HBTC Total trading pool share query
     *
     * @return
     */
    @Override
    public BigInteger totalSupplyOfHBTC() {
        try {
            return HBTC_ERC20.getLiqidity().totalSupply().send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BigInteger totalSupplyDecimalsOfHBTC() {
        return decimals(HBTC_ERC20.getLiqidity());
    }

    /**
     * ETH/HBTC Trading pool ETH total share query
     *
     * @return
     */
    @Override
    public BigInteger balanceOfEthOfHBTC() {
        try {
            return HBTC_ERC20.getWeth().balanceOf(CofixContractAddress.HBTC_ADDR.getPair()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BigInteger balanceDecimalsOfEthOfHBTC() {
        return decimals(HBTC_ERC20.getWeth());
    }

    /**
     * ETH/HBTC Trading pool HBTC total share query
     *
     * @return
     */
    @Override
    public BigInteger balanceOfHbtcOfHBTC() {
        try {
            return HBTC_ERC20.getToken().balanceOf(CofixContractAddress.HBTC_ADDR.getPair()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BigInteger balanceDecimalsOfHbtcOfHBTC() {
        return decimals(HBTC_ERC20.getToken());
    }

    @Override
    public int updateInterval(Integer interval) {
        this.interval = interval;
        return 0;
    }

    @Override
    public int getInterval() {
        return this.interval;
    }

    @Override
    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    @Override
    public String getJobKey() {
        return this.jobKey;
    }

    @Override
    public PoolAmountVo getOldPoolAmountVo() {
        return this.oldPoolAmountVo;
    }

    @Override
    public void setOldPoolAmountVo(PoolAmountVo newPoolAmountVo) {
        this.oldPoolAmountVo = newPoolAmountVo;
    }

    @Override
    public void addDeltaEth(BigDecimal deltaEth) {
        this.deltaEth.add(deltaEth);
    }

    @Override
    public void addDeltaErc20(BigDecimal deltaErc20) {
        this.deltaErc20.add(deltaErc20);
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
    public void buyEth() {
        return;
    }

    @Override
    public void sellEth() {

    }

    @Override
    public void buyErc20() {

    }

    @Override
    public void sellErc20() {

    }
}
