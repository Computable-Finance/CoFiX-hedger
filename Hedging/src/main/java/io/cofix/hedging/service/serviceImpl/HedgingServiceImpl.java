package io.cofix.hedging.service.serviceImpl;

import io.cofix.hedging.constant.Constant;
import io.cofix.hedging.contract.ERC20;
import io.cofix.hedging.contract.ICoFiXFactory;
import io.cofix.hedging.contract.LockFactoryContract;
import io.cofix.hedging.model.HedgingPool;
import io.cofix.hedging.service.HedgingService;
import io.cofix.hedging.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class HedgingServiceImpl implements HedgingService {

    // Contains all the added trading pools
    private final static List<HedgingPool> HEDGING_POOL_LIST = new CopyOnWriteArrayList<>();


    public Web3j web3j;
    public TransactionManager transactionManager = null;
    public String address;

    @Value("${cofix.node}")
    private String node;
    private Integer interval;

    private String jobKey = null;

    @Value("${cofix.weth.contract.address}")
    String wethAddress;

    @Value("${cofix.icofix-factory.contract.address}")
    String icofixfactory;
    @Value("${cofix.lock-factory.contract.address}")
    String lockFactory;

    @Value("${huobi.proxy.enable}")
    private Boolean huobiProxyEnable;

    @Value("${huobi.proxy.server}")
    private String huobiProxyServer;

    @Value("${huobi.proxy.port}")
    private Integer huobiProxyPort;

    // 节点访问的用户名和密码
    private String nodeUsername;
    private String nodePassword;

    @Autowired
    private HedgingService hedgingService;

    @Autowired
    private TransactionService transactionService;

    HedgingServiceImpl() {
        this.interval = 20;
    }

    @PostConstruct
    public void postHedgingServiceImpl() {
        if (!StringUtils.isEmpty(this.node))
            this.web3j = Web3j.build(new HttpService(node));
    }

    @Override
    public List<HedgingPool> getHedgingPoolList() {
        return HEDGING_POOL_LIST;
    }

    /**
     * Add a new trading pool
     *
     * @param hedgingPool
     */
    @Override
    public void addHegingPool(HedgingPool hedgingPool) {
        HEDGING_POOL_LIST.add(hedgingPool);
    }

    private String getPairAddress(String tokenAddr) {
        ICoFiXFactory iCoFiXFactory = ICoFiXFactory.load(icofixfactory, web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);

        String pair = null;
        try {
            pair = iCoFiXFactory.getPair(tokenAddr).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pair;
    }

    private String getLockAddress(String pairAddr) {

        // Lock the warehouse address
        String lockAddress = null;
        try {
            LockFactoryContract lockFactoryContract = LockFactoryContract.load(lockFactory, web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);
            lockAddress = lockFactoryContract.stakingPoolForPair(pairAddr).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lockAddress;
    }


    @Override
    public void UpdateNode(String node, String nodeUsername, String nodePassword) {
        HttpService httpService;

        this.nodeUsername   = nodeUsername;
        this.nodePassword   = nodePassword;

        if (huobiProxyEnable) {
            Proxy proxy  = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(huobiProxyServer, huobiProxyPort));
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(proxy)
                    .build();
            httpService = new HttpService(node, client, false);
        } else {
            httpService = new HttpService(node);
        }

        if (!StringUtils.isEmpty(this.nodeUsername)) {
            httpService.addHeader("Authorization", Credentials.basic(this.nodeUsername, this.nodePassword));
        }

        web3j = Web3j.build(httpService);
        this.node = node;
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
        this.address = address;
    }

    @Override
    public String selectAddress() {
        return this.address;
    }

    /**
     * Total number of ETH trading pool
     *
     * @return
     */
    @Override
    public BigInteger balanceOfEthOfWeth(ERC20 weth, String pairAddress) {
        try {
            return weth.balanceOf(pairAddress).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Trading pool individual share query
     *
     * @return
     */
    @Override
    public BigInteger balanceOfPair(ERC20 pair) {

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
     *
     * @return
     */
    @Override
    public BigInteger balanceOfLock(ERC20 lock) {

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

    /**
     * Total trading pool share query
     *
     * @return
     */
    @Override
    public BigInteger totalSupplyOfLiqidity(ERC20 liqidity) {
        try {
            return liqidity.totalSupply().send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Trading pool HBTC total share query
     *
     * @return
     */
    @Override
    public BigInteger balanceOfErc20(ERC20 token, String pairAddress) {
        try {
            return token.balanceOf(pairAddress).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param tokenAddres
     * @param tradingPairs
     * @param ethThreshold
     * @param erc20Threshold
     */
    @Override
    public void createHegingPool(String tokenAddres, String tradingPairs, BigDecimal ethThreshold, BigDecimal erc20Threshold) {
        createHegingPoolBase(tokenAddres, tradingPairs, ethThreshold, erc20Threshold);
    }

    @Override
    public void createHegingPool(String token, String tradingPairs, String apiKey, String apiSecret, BigDecimal ethThreshold, BigDecimal erc20Threshold) {
        HedgingPool hedgingPool = createHegingPoolBase(token, tradingPairs, ethThreshold, erc20Threshold);
        if (null == hedgingPool) return;

        hedgingPool.setApiKey(apiKey);
        hedgingPool.setSecretKey(apiSecret);
    }

    public HedgingPool createHegingPoolBase(String tokenAddres, String tradingPairs, BigDecimal ethThreshold, BigDecimal erc20Threshold) {
        if (null == web3j) {
            log.info("Please set the node first");
            return null;
        }
        if (null == transactionManager) {
            log.info("Please fill in the marketmaker account address");
            return null;
        }
        log.info("***The erc20 token contract adrees is {}***",tokenAddres);

        // Create token contracts
        ERC20 token = ERC20.load(tokenAddres, web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);

        // Create weth contracts
        ERC20 weth = ERC20.load(wethAddress, web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);

        log.info("***The weth token contract adrees is {}***",wethAddress);

        // Gets the trading pool contract address
        String pairAddress = getPairAddress(tokenAddres);
        log.info("***The pair token contract adrees is {}***",pairAddress);
        ERC20 pair = ERC20.load(pairAddress, web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);


        // Gets the lock-up contract address
        String lockAddress = getLockAddress(pairAddress);
        ERC20 lock = null;
        if (!lockAddress.equals(Address.DEFAULT.getValue())) {
            log.info("***The lock token contract adrees is {}***",lockAddress);
            lock = ERC20.load(lockAddress, web3j, transactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);
        }

        // Unit conversion
        if (ethThreshold == null) {
            ethThreshold = BigDecimal.ONE.multiply(Constant.UNIT_ETH);
        }else{
            ethThreshold = ethThreshold.multiply(Constant.UNIT_ETH);
        }

        BigInteger decimals = null;
        try {
            decimals = token.decimals().send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (decimals == null) {
            log.error("Decimals failed to get");
            return null;
        }

        BigDecimal decimalsPowTen = BigDecimal.TEN.pow(decimals.intValue());
        if (erc20Threshold == null) {
            erc20Threshold = BigDecimal.ONE.multiply(decimalsPowTen);
        }else{
            erc20Threshold = erc20Threshold.multiply(decimalsPowTen);
        }

        HedgingPool hedgingPool = new HedgingPool(token, weth, pair, lock, tradingPairs, ethThreshold, erc20Threshold,hedgingService,transactionService);
        addHegingPool(hedgingPool);

        return hedgingPool;
    }

    @Override
    public void updateInterval(BigDecimal unitEthThreshold, BigDecimal unitErc20Threshold, String huobiTradingPair) {
        for (HedgingPool hedgingPool : HEDGING_POOL_LIST) {
            if (hedgingPool.getHuobiTradingPair().equalsIgnoreCase(huobiTradingPair)){
                unitEthThreshold = unitEthThreshold.multiply(Constant.UNIT_ETH);
                hedgingPool.setEthThreshold(unitEthThreshold);
                BigInteger decimals = hedgingPool.getDecimals();
                unitErc20Threshold = unitErc20Threshold.multiply(BigDecimal.TEN.pow(decimals.intValue()));
                hedgingPool.setErc20Threshold(unitErc20Threshold);
                return;
            }
        }
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
}
