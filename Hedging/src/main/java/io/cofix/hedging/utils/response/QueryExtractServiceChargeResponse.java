package io.cofix.hedging.utils.response;

import java.util.List;

/**
 * ClassName:QueryExtractServiceChargeResponse
 * Description:
 */
public class QueryExtractServiceChargeResponse {
    private int code;
    private String message;
    private List<QueryExtractServiceChargeData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<QueryExtractServiceChargeData> getData() {
        return data;
    }

    public void setData(List<QueryExtractServiceChargeData> data) {
        this.data = data;
    }
}
