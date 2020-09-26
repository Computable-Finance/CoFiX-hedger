package io.cofix.hedging.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cofix.hedging.annotation.TokenRequired;
import io.cofix.hedging.constant.Constant;
import io.cofix.hedging.service.HedgingJobService;
import io.cofix.hedging.service.HedgingPoolService;
import io.cofix.hedging.service.HedgingService;

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
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
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

    final private TransactionServiceImpl eatOfferAndTransactionService;

    final private Scheduler scheduler;

    final static String JOB_GROUP = "hedging-jobs";

    @Autowired
    HedgingJobService hedgingJob;

    @Qualifier("HBTC")
    @Autowired
    private HedgingPoolService  hedgingHbtcPoolService;

    @Qualifier("USDT")
    @Autowired
    private HedgingPoolService  hedgingUsdtPoolService;

    HedgingController(final HedgingService hedgingService, final TransactionServiceImpl eatOfferAndTransactionService, final Scheduler scheduler) {
        this.hedgingService = hedgingService;
        this.eatOfferAndTransactionService = eatOfferAndTransactionService;
        this.scheduler = scheduler;
    }

    /**
     * Set scheduler interval in seconds.
     */
    @TokenRequired
    @PostMapping("updateInterval")
    public int updateInterval(@RequestParam Integer interval) {
        return hedgingService.updateInterval(interval);
    }


    /**
     * Set the account private key
     */
    @TokenRequired
    @PostMapping("/updatePrivateKey")
    public String updateUserPrivatekey(@RequestParam(name = "privateKey") String privateKey) {
        hedgingService.updateUserPrivateKey(privateKey);
        return hedgingService.selectUserWalletAddress();
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

            String result = eatOfferAndTransactionService.updateExchangeApiKey(apiKey, apiSecret);
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
    public String updateThreshold(@RequestParam(name = "ethThreshold") BigDecimal ethThreshold,
                                  @RequestParam(name = "usdtThreshold") BigDecimal usdtThreshold,
                                  @RequestParam(name = "hbtcThreshold") BigDecimal hbtcThreshold) {

        ethThreshold = ethThreshold.multiply(Constant.UNIT_ETH);
        usdtThreshold = usdtThreshold.multiply(Constant.UNIT_USDT);
        hbtcThreshold = hbtcThreshold.multiply(Constant.UNIT_HBTC);

        hedgingHbtcPoolService.setEthThreshold(ethThreshold);
        hedgingHbtcPoolService.setErc20Threshold(hbtcThreshold);

        hedgingUsdtPoolService.setEthThreshold(ethThreshold);
        hedgingUsdtPoolService.setErc20Threshold(usdtThreshold);

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
        HttpClientUtil.updateProxy(proxyIp, proxyPort);
        return "ok";
    }

    /**
     * View miner details
     */
//    @TokenRequired
    @GetMapping("/hedgingData")
    public ModelAndView miningData() {
        String address = hedgingService.selectUserWalletAddress();
        if (address == null) address = "Please fill in the correct private key first";

        ModelAndView mav = new ModelAndView("hedgingData");
        mav.addObject("address", address);
        mav.addObject("node", hedgingService.getNode());
        mav.addObject("interval", hedgingService.getInterval());
        mav.addObject("proxyIp", HttpClientUtil.getProxyIp());
        mav.addObject("proxyPort", HttpClientUtil.getProxyPort());
        mav.addObject("start", HedgingJobServiceImpl.START ? "OPEN" : "CLOSE");
        mav.addObject("ethThreshold", hedgingUsdtPoolService.getEthThreshold().divide(Constant.UNIT_ETH, 18, RoundingMode.HALF_UP).toPlainString());
        mav.addObject("usdtThreshold", hedgingUsdtPoolService.getErc20Threshold().divide(Constant.UNIT_USDT, 10, RoundingMode.HALF_UP).toPlainString());
        mav.addObject("hbtcThreshold", hedgingHbtcPoolService.getErc20Threshold().divide(Constant.UNIT_HBTC, 18, RoundingMode.HALF_UP).toPlainString());

        return mav;
    }

    @GetMapping("/balance-of")
    public BigInteger balanceOf() {
        return hedgingService.balanceOfHBTC();
    }

    @GetMapping("/total-supply")
    public BigInteger totalSupply() {
        return hedgingService.totalSupplyOfHBTC();
    }

    @GetMapping("/eth-of-hbtc")
    public BigInteger ethOfHBTC() {
        return hedgingService.balanceOfEthOfHBTC();
    }

    @GetMapping("/hbtc-of-hbtc")
    public BigInteger hbtcOfHBTC() {
        return hedgingService.balanceOfHbtcOfHBTC();
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
