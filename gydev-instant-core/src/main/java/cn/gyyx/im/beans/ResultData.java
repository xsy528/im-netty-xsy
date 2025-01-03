package cn.gyyx.im.beans;

import lombok.Data;

@Data
public class ResultData {
    private String info;
    private boolean success;

    public ResultData(String info, boolean success) {
        this.info = info;
        this.success = success;
    }

    public static ResultData success(String info) {
        return new ResultData(info,true);
    }

    public static ResultData fail(String info) {
        return new ResultData(info,false);
    }
}
