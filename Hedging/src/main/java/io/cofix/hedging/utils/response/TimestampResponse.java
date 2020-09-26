package io.cofix.hedging.utils.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 15:53
 */

public class TimestampResponse {

    /**
     * status : ok
     * data : 1494900087029
     */

    private String status;
    private long data;
    private String dateTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getDateTime() {
        return this.dateTime;
    }

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
}
