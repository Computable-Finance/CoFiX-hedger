package io.cofix.hedging.utils.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 15:25
 */

public class HistoryTradess {
    /**
     * id : 31459998
     * ts : 1502448920106
     * data : [{"id":17592256642623,"amount":0.04,"price":1997,"direction":"buy","ts":1502448920106}]
     */

    private long id;
    private long ts;
    private HistoryTrade data;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public HistoryTrade getData() {
        return data;
    }

    public void setData(HistoryTrade data) {
        this.data = data;
    }
}
