package cn.gyyx.im.beans.request;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

@Data
public class GetHistoryChatLogMessage extends IBaseMsg {
    /**
     * 接收人
     */
    private String sender;
    /**
     * 分页参数
     */
    private Integer pageIndex;

    private Integer pageSize;
}
