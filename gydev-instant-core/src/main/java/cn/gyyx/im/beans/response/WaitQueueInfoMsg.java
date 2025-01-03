package cn.gyyx.im.beans.response;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

import java.util.List;

@Data
public class WaitQueueInfoMsg extends IBaseMsg {

    private Integer waitTotal;

    private Integer waitIndex;

    private List<String> userIdList;

}
