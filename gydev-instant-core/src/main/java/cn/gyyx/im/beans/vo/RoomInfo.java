package cn.gyyx.im.beans.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoomInfo {

    private String roomId;

    private String appId;

    private String creator;

    private List<String> userIds;
}
