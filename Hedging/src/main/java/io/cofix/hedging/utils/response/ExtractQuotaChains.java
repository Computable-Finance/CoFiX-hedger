package io.cofix.hedging.utils.response;

import java.io.Serializable;

/**
 * ClassName:ExtractQuotaChains
 * Description:
 */
public class ExtractQuotaChains implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String chain;        // The name of the chain
    private String maxWithdrawAmt;   // Maximum withdrawal amount at one time
    private String withdrawQuotaPerDay;  // Daily withdrawal limit
    private String remainWithdrawQuotaPerDay;    // The amount remaining on the day of withdrawal
    private String withdrawQuotaPerYear;     // Current withdrawal limit
    private String remainWithdrawQuotaPerYear;   // The amount remaining in the year of withdrawal
    private String withdrawQuotaTotal;       // Total withdrawal limit
    private String remainWithdrawQuotaTotal; // Total withdrawal amount remaining

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getMaxWithdrawAmt() {
        return maxWithdrawAmt;
    }

    public void setMaxWithdrawAmt(String maxWithdrawAmt) {
        this.maxWithdrawAmt = maxWithdrawAmt;
    }

    public String getWithdrawQuotaPerDay() {
        return withdrawQuotaPerDay;
    }

    public void setWithdrawQuotaPerDay(String withdrawQuotaPerDay) {
        this.withdrawQuotaPerDay = withdrawQuotaPerDay;
    }

    public String getRemainWithdrawQuotaPerDay() {
        return remainWithdrawQuotaPerDay;
    }

    public void setRemainWithdrawQuotaPerDay(String remainWithdrawQuotaPerDay) {
        this.remainWithdrawQuotaPerDay = remainWithdrawQuotaPerDay;
    }

    public String getWithdrawQuotaPerYear() {
        return withdrawQuotaPerYear;
    }

    public void setWithdrawQuotaPerYear(String withdrawQuotaPerYear) {
        this.withdrawQuotaPerYear = withdrawQuotaPerYear;
    }

    public String getRemainWithdrawQuotaPerYear() {
        return remainWithdrawQuotaPerYear;
    }

    public void setRemainWithdrawQuotaPerYear(String remainWithdrawQuotaPerYear) {
        this.remainWithdrawQuotaPerYear = remainWithdrawQuotaPerYear;
    }

    public String getWithdrawQuotaTotal() {
        return withdrawQuotaTotal;
    }

    public void setWithdrawQuotaTotal(String withdrawQuotaTotal) {
        this.withdrawQuotaTotal = withdrawQuotaTotal;
    }

    public String getRemainWithdrawQuotaTotal() {
        return remainWithdrawQuotaTotal;
    }

    public void setRemainWithdrawQuotaTotal(String remainWithdrawQuotaTotal) {
        this.remainWithdrawQuotaTotal = remainWithdrawQuotaTotal;
    }

    @Override
    public String toString() {
        return "ExtractQuotaChains{" +
                "chain='" + chain + '\'' +
                ", maxWithdrawAmt='" + maxWithdrawAmt + '\'' +
                ", withdrawQuotaPerDay='" + withdrawQuotaPerDay + '\'' +
                ", remainWithdrawQuotaPerDay='" + remainWithdrawQuotaPerDay + '\'' +
                ", withdrawQuotaPerYear='" + withdrawQuotaPerYear + '\'' +
                ", remainWithdrawQuotaPerYear='" + remainWithdrawQuotaPerYear + '\'' +
                ", withdrawQuotaTotal='" + withdrawQuotaTotal + '\'' +
                ", remainWithdrawQuotaTotal='" + remainWithdrawQuotaTotal + '\'' +
                '}';
    }
}
