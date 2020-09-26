package io.cofix.hedging.utils.response;

import java.util.List;

/**
 * ClassName:DepositAddressResponse
 * Description:
 */
public class DepositAddressResponse {
    private int code;
    private String message;
    private List<DepositAddressBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DepositAddressBean> getData() {
        return data;
    }

    public void setData(List<DepositAddressBean> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
