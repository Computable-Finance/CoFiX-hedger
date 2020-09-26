package io.cofix.hedging.utils.response;

import java.util.List;

/**
 * ClassName:QueryExtractServiceChargeData
 * Description:
 */
public class QueryExtractServiceChargeData {
    private List<QueryExtractServiceChargeChains> chains;
    private String currency;
    private String instStatus;

    public List<QueryExtractServiceChargeChains> getChains() {
        return chains;
    }

    public void setChains(List<QueryExtractServiceChargeChains> chains) {
        this.chains = chains;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getInstStatus() {
        return instStatus;
    }

    public void setInstStatus(String instStatus) {
        this.instStatus = instStatus;
    }
}
