package io.cofix.hedging.utils.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 17:15
 */

public class SubmitcancelResponse {


    /**
     * status : ok
     * data : 59378
     */

    private String status;
    public String errCode;
    public String errMsg;
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
