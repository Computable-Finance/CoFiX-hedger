package io.cofix.hedging.utils.response;

import io.cofix.hedging.utils.api.ApiException;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 11:56
 */

public class KlineResponse<T> {

    private String status;
    private String ch;
    private String ts;
    public String errCode;
    public String errMsg;
    public T data;

    public T checkAndReturn() {
        if ("ok".equals(status)) {
            return data;
        }
        throw new ApiException(errCode, errMsg);
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
