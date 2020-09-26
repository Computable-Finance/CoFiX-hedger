package io.cofix.hedging.utils.response;

import java.io.Serializable;

/**
 * ClassName:ExtractQuotaResponse
 * Description:
 */
public class ExtractQuotaResponse<T> implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
