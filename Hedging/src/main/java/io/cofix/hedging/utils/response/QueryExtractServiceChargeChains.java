package io.cofix.hedging.utils.response;

/**
 * ClassName:QueryExtractServiceChargeChains
 * Description:
 */
public class QueryExtractServiceChargeChains {
    private String chain;       // The name of the chain
    private String depositStatus;   // State of charge money
    private String maxTransactFeeWithdraw;  // Largest single mention money handling charge (cap is only for the interval type and the proportion of type, effective withdrawFeeType = circulated or thewire)
    private String maxWithdrawAmt;  // Maximum withdrawal amount at one time
    private String minDepositAmt;   // Minimum charge amount per time
    private String transactFeeWithdraw; // A single withdrawing fee (valid only for a fixed type, feeType =fixed)
    private String minTransactFeeWithdraw;  // Minimum single mention money handling charge (only the interval type, effective withdrawFeeType = circulated)
    private String transactFeeRateWithdraw; // A single withdrawing rate (effective only for proportion type, descriptive type =ratio)
    private String minWithdrawAmt;  // Minimum withdrawal amount per time
    private String numOfConfirmations;  // Number of confirmations required for safe account Posting (currency withdrawal is allowed after the number of confirmations is reached)
    private String numOfFastConfirmations;  // The number of confirmations required for rapid account Posting (transactions are allowed after the number of confirmations is reached, but currency withdrawal is not allowed)
    private String withdrawFeeType;     // Withdrawal fee Type (the unique withdrawal fee type for a specific currency on a specific chain)
    private String withdrawPrecision;   // Mention money precision
    private String withdrawQuotaPerDay; // Daily withdrawal limit
    private String withdrawQuotaPerYear;    // Current withdrawal limit
    private String withdrawQuotaTotal;  // Total withdrawal limit
    private String withdrawStatus;      // Mention currency status
    private String instStatus;      // State of the currency

    public String getTransactFeeWithdraw() {
        return transactFeeWithdraw;
    }

    public void setTransactFeeWithdraw(String transactFeeWithdraw) {
        this.transactFeeWithdraw = transactFeeWithdraw;
    }

    public String getTransactFeeRateWithdraw() {
        return transactFeeRateWithdraw;
    }

    public void setTransactFeeRateWithdraw(String transactFeeRateWithdraw) {
        this.transactFeeRateWithdraw = transactFeeRateWithdraw;
    }

    public String getInstStatus() {
        return instStatus;
    }

    public void setInstStatus(String instStatus) {
        this.instStatus = instStatus;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(String depositStatus) {
        this.depositStatus = depositStatus;
    }

    public String getMaxTransactFeeWithdraw() {
        return maxTransactFeeWithdraw;
    }

    public void setMaxTransactFeeWithdraw(String maxTransactFeeWithdraw) {
        this.maxTransactFeeWithdraw = maxTransactFeeWithdraw;
    }

    public String getMaxWithdrawAmt() {
        return maxWithdrawAmt;
    }

    public void setMaxWithdrawAmt(String maxWithdrawAmt) {
        this.maxWithdrawAmt = maxWithdrawAmt;
    }

    public String getMinDepositAmt() {
        return minDepositAmt;
    }

    public void setMinDepositAmt(String minDepositAmt) {
        this.minDepositAmt = minDepositAmt;
    }

    public String getMinTransactFeeWithdraw() {
        return minTransactFeeWithdraw;
    }

    public void setMinTransactFeeWithdraw(String minTransactFeeWithdraw) {
        this.minTransactFeeWithdraw = minTransactFeeWithdraw;
    }

    public String getMinWithdrawAmt() {
        return minWithdrawAmt;
    }

    public void setMinWithdrawAmt(String minWithdrawAmt) {
        this.minWithdrawAmt = minWithdrawAmt;
    }

    public String getNumOfConfirmations() {
        return numOfConfirmations;
    }

    public void setNumOfConfirmations(String numOfConfirmations) {
        this.numOfConfirmations = numOfConfirmations;
    }

    public String getNumOfFastConfirmations() {
        return numOfFastConfirmations;
    }

    public void setNumOfFastConfirmations(String numOfFastConfirmations) {
        this.numOfFastConfirmations = numOfFastConfirmations;
    }

    public String getWithdrawFeeType() {
        return withdrawFeeType;
    }

    public void setWithdrawFeeType(String withdrawFeeType) {
        this.withdrawFeeType = withdrawFeeType;
    }

    public String getWithdrawPrecision() {
        return withdrawPrecision;
    }

    public void setWithdrawPrecision(String withdrawPrecision) {
        this.withdrawPrecision = withdrawPrecision;
    }

    public String getWithdrawQuotaPerDay() {
        return withdrawQuotaPerDay;
    }

    public void setWithdrawQuotaPerDay(String withdrawQuotaPerDay) {
        this.withdrawQuotaPerDay = withdrawQuotaPerDay;
    }

    public String getWithdrawQuotaPerYear() {
        return withdrawQuotaPerYear;
    }

    public void setWithdrawQuotaPerYear(String withdrawQuotaPerYear) {
        this.withdrawQuotaPerYear = withdrawQuotaPerYear;
    }

    public String getWithdrawQuotaTotal() {
        return withdrawQuotaTotal;
    }

    public void setWithdrawQuotaTotal(String withdrawQuotaTotal) {
        this.withdrawQuotaTotal = withdrawQuotaTotal;
    }

    public String getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(String withdrawStatus) {
        this.withdrawStatus = withdrawStatus;
    }
}
