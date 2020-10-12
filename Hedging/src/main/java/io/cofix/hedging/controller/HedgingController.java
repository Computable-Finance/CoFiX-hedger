package io.cofix.hedging.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cofix.hedging.annotation.TokenRequired;
import io.cofix.hedging.constant.Constant;
import io.cofix.hedging.model.HedgingPool;
import io.cofix.hedging.service.HedgingJobService;
import io.cofix.hedging.service.HedgingService;

import io.cofix.hedging.service.TransactionService;
import io.cofix.hedging.service.serviceImpl.HedgingJobServiceImpl;
import io.cofix.hedging.service.serviceImpl.TransactionServiceImpl;
import io.cofix.hedging.utils.HttpClientUtil;
import io.cofix.hedging.utils.api.ApiClient;
import io.cofix.hedging.utils.api.JsonUtil;
import io.cofix.hedging.utils.response.Accounts;
import io.cofix.hedging.utils.response.AccountsResponse;
import io.cofix.hedging.utils.response.BalanceResponse;
import io.cofix.hedging.vo.HedgingQuartzJob;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * ClassName:HedgingController
 * Description:
 */
@RestController
@RequestMapping("/hedging")
@Slf4j
public class HedgingController {

    final private HedgingService hedgingService;

    final private TransactionServiceImpl transactionServiceIml;

    final private Scheduler scheduler;

    final static String JOB_GROUP = "hedging-jobs";

    @Autowired
    HedgingJobService hedgingJob;

    @Autowired
    TransactionService transactionService;

    HedgingController(final HedgingService hedgingService, final TransactionServiceImpl transactionServiceIml, final Scheduler scheduler) {
        this.hedgingService = hedgingService;
        this.transactionServiceIml = transactionServiceIml;
        this.scheduler = scheduler;
    }

    @TokenRequired
    @PostMapping("/updateInterval")
    public void updateInterval(@RequestParam Integer interval) {
        hedgingService.updateInterval(interval);
    }

    /**
     * Set up a marketmaker account address
     */
    @TokenRequired
    @PostMapping("/updateAddress")
    public String updateAddress(@RequestParam(name = "address") String address) {
        hedgingService.updateAddress(address);
        return hedgingService.selectAddress();
    }

    /**
     * Set the API-Key and API-Secret of huocoin exchange
     *
     * @return
     */
    @TokenRequired
    @PostMapping("/updateExchangeApiKey")
    public List updateExchangeApiKey(@RequestParam(name = "apiKey") String apiKey, @RequestParam(name = "apiSecret") String apiSecret) {
        ApiClient client = new ApiClient(apiKey, apiSecret);
        AccountsResponse accounts = client.accounts();
        List<Accounts> listAccounts = (List<Accounts>) accounts.getData();
        if (!listAccounts.isEmpty()) {
            //------------------------------------------------------ The account balance  -------------------------------------------------------
            BalanceResponse balance = client.balance(String.valueOf(listAccounts.get(0).getId()));
            String s = "";
            try {
                s = JsonUtil.writeValue(balance);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = JSONObject.parseObject(s);
            String data = jsonObject.getString("data");
            JSONObject jsonObject1 = JSONObject.parseObject(data);
            JSONArray list = jsonObject1.getJSONArray("list");
            List balanceList = new ArrayList<>();

            list.forEach(li -> {
                JSONObject jsonObject2 = JSONObject.parseObject(String.valueOf(li));
                String balanceStr = jsonObject2.getString("balance");
                String currency = jsonObject2.getString("currency");

                // Find the balance of ETH,USDT
                if (currency.equalsIgnoreCase(TransactionServiceImpl.SYMBOL)
                        || currency.equalsIgnoreCase("ETH")) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("balance", balanceStr);
                    hashMap.put("currency", currency);
                    balanceList.add(hashMap);
                }
            });

            String result = transactionServiceIml.updateExchangeApiKey(apiKey, apiSecret);
            if (result.equalsIgnoreCase("SUCCESS")) {
                return balanceList;
            }
        }
        return null;
    }

    /**
     * Set the node
     */
    @TokenRequired
    @PostMapping("/updateNode")
    public String updateNode(@RequestParam(name = "node") String node) {
        hedgingService.UpdateNode(node);
        return node;
    }

    @TokenRequired
    @PostMapping("/updateThreshold")
    public String updateThreshold(@RequestParam(name = "unitEthThreshold") BigDecimal unitEthThreshold,
                                  @RequestParam(name = "unitErc20Threshold") BigDecimal unitErc20Threshold,
                                  @RequestParam(name = "huobiTradingPair") String huobiTradingPair) {
        hedgingService.updateInterval(unitEthThreshold,unitErc20Threshold,huobiTradingPair);

        return "ok";
    }


    @TokenRequired
    @PostMapping("/addTransactionPool")
    public String addTransactionPool(@RequestParam(name = "token") String token,
                                     @RequestParam(name = "tradingPairs") String tradingPairs,
                                     @RequestParam(name = "apiKey") String apiKey,
                                     @RequestParam(name = "apiSecret") String apiSecret,
                                     @RequestParam(name = "ethThreshold", required = false) BigDecimal ethThreshold,
                                     @RequestParam(name = "erc20Threshold", required = false) BigDecimal erc20Threshold) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(tradingPairs)) {
            throw new RuntimeException("The contract address or tradingPairs cannot be empty");
        }

        // Check if the trade pairs are correct
        String huobiUrl = Constant.HUOBI_API + tradingPairs;

        try {
            BigDecimal exchangePrice = transactionService.getExchangePrice(huobiUrl, tradingPairs);
            log.info("***The added huobi trading pair [{}] gets a price of [{}]***", tradingPairs, exchangePrice);
        } catch (Exception e) {
            log.error("***Added huobi trading pair [{}], failed to get price: {}***", tradingPairs, e.getMessage());
            return "error";
        }

        hedgingService.createHegingPool(token, tradingPairs, apiKey, apiSecret, ethThreshold, erc20Threshold);

        return "ok";
    }


    /**
     * Set the network agent address and port
     *
     * @param proxyIp
     * @param proxyPort
     * @return
     */
    @TokenRequired
    @PostMapping("/updateProxy")
    public String updateProxy(@RequestParam(name = "proxyIp") String proxyIp, @RequestParam(name = "proxyPort") int proxyPort) {
//        ApiClient.setProxy(proxyIp, proxyPort);

        HttpClientUtil.updateProxy(proxyIp, proxyPort);
        return "ok";
    }

    /**
     * View miner details
     */
//    @TokenRequired
    @GetMapping("/hedgingData")
    public ModelAndView miningData() {
        String address = hedgingService.selectAddress();
        if (address == null) address = "Please fill in the marketmaker account address";

        ModelAndView mav = new ModelAndView("hedgingData");
        mav.addObject("address", address);

        mav.addObject("node", hedgingService.getNode());
        mav.addObject("interval", hedgingService.getInterval());
        mav.addObject("proxyIp", HttpClientUtil.getProxyIp());
        mav.addObject("proxyPort", HttpClientUtil.getProxyPort());
        mav.addObject("start", HedgingJobServiceImpl.START ? "OPEN" : "CLOSE");
        // 展示各池数据
        List<HedgingPool> hedgingPoolList = hedgingService.getHedgingPoolList();
        mav.addObject("hedgingPoolList", hedgingPoolList);
        return mav;
    }


    @TokenRequired
    @PostMapping("/start-hedging")
    public ResponseEntity<String> scheduleHedging() {

        HedgingJobServiceImpl.START = !HedgingJobServiceImpl.START;

        JobDetail jobDetail = buildJobDetail();
        Trigger trigger = buildJobTrigger(jobDetail);
        try {
            scheduler.deleteJob(JobKey.jobKey(hedgingService.getJobKey(), JOB_GROUP));
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("success");
    }

    private JobDetail buildJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();

//        jobDataMap.put("email", scheduleEmailRequest.getEmail());

        String jobKey = UUID.randomUUID().toString();
        hedgingService.setJobKey(jobKey);

        return JobBuilder.newJob(HedgingQuartzJob.class)
                .withIdentity(jobKey, JOB_GROUP)
                .withDescription("Hedging Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "hedging-triggers")
                .withDescription("Hedging Trigger")
//                .startAt(Date.now))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(hedgingService.getInterval()))
                .build();
    }
}
