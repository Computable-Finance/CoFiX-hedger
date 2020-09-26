package io.cofix.hedging.utils.response;

/**
 * ClassName:DepositAddressBean
 * Description:
 */
public class DepositAddressBean {

    private String currency;
    private String address;
    private String addressTag;
    private String chain;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressTag() {
        return addressTag;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }
}
