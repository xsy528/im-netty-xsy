package cn.gyyx.im.enums;

import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.beans.request.*;
import cn.gyyx.im.beans.request.AcceptVideoMessage;
import cn.gyyx.im.beans.response.*;
import cn.gyyx.im.beans.request.RequestWaitQueue;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * @author 邢少亚
 * @date 2024/1/30  13:39
 * @description 消息类型
 */
@Getter
@AllArgsConstructor
public enum MsgTypeEnum {

    //--------------连接相关 1-100------------------
    Param_Error(10,"参数错误","后端-》前端",IBaseMsg.class),
    Heartbeat(20,"心跳","前端-》后端",SendMessage.class),
    Heartbeat_SUCCESS(21,"心跳响应","后端-》前端",SendMessage.class),
    Server_Receive_Success(30,"服务器接收消息成功","后端-》前端", ServerReceiveSuccessMsg.class),
    Client_Receive_Success(31,"客户端接收消息成功","前端-》后端", ServerReceiveSuccessMsg.class),

    Service_Maintenance(99,"服务端重启，维护，通常用于服务端关闭事件通知客户端","后端-》前端",SendMessage.class),

    //--------------消息相关 100-200------------------
    online_status(101,"在线状态","后端-》前端", SendMessage.class),
    not_read_message(102,"获取未读消息","前端-》后端",SendMessage.class),
    Send_Message(130, "客户端发送消息","前端-》后端",SendMessage.class),
    Send_Message_Success(131,"客户端推送成功","后端-》前端",SendMessage.class),
    Chat_Sign_TimeOut(132,"聊天签名过期失效","后端-》前端",SendMessage.class),
    Message_Bad_Words(133,"聊天敏感词拦截","后端-》前端",SendMessage.class),
    Push_Message(140,"服务端推送消息","后端-》前端",SendMessage.class),
    Push_Message_Success(141, "服务端推送成功","前端-》后端",SendMessage.class),
    View_Message(160,"用户查看某人的消息","前端-》后端",SendMessage.class),
    Get_History_Chat_Log(161,"用户获取历史聊天消息","前端-》后端", GetHistoryChatLogMessage.class),
    Return_History_Chat_Log(162,"返回历史聊天消息","后端-》前端", SendMessage.class),
    Load_Chat_History(1170,"首次登录，拉取每个用户的首条消息","后端-》前端",SendMessage.class),
    UnView_Message_Count(180,"获取未读消息总数","前端-》后端", SendMessage.class),
    Retract_Message(190,"消息撤回","前端-》后端", RetractMessage.class),
    Retract_Message_Promoter_Success(191,"通知消息发送人消息撤回成功","后端-》前端",SendMessage.class),
    Retract_Message_Receive_Success(192,"通知消息接收人消息撤回成功","后端-》前端",SendMessage.class),
    Read_Message(198,"消息已读","前端-》后端",SendMessage.class),
    Read_Message_Success(199,"消息已读成功","后端-》前端",SendMessage.class),

    //--------------用户在线相关 200-300 ------------------
    Login_Out(251,"用户登出/关闭通讯","前端-》后端",SendMessage.class),
    Login_Out_SUCCESS(252,"用户登出成功","后端-》前端",SendMessage.class),
    Friends_Online(200, "好友上线","后端-》前端",FriendLineStatusMsg.class),
    Friends_Offline(210,"好友下线","后端-》前端",FriendLineStatusMsg.class),

    //--------------视频相关 300-400 ------------------
    Request_Video(300,"请求发起视频","前端-》后端",AcceptVideoMessage.class),
    Other_Request_Video(301,"他人请求发起视频","后端-》前端",AcceptVideoMessage.class),
    Accept_Video(310,"接受视频","前端-》后端", AcceptVideoMessage.class),
    Other_Accept_Video(311,"他人接受视频","后端-》前端", AcceptVideoMessage.class),
    Refuse_Video(320, "拒绝视频","前端-》后端",AcceptVideoMessage.class),
    Other_Refuse_Video(321, "他人拒绝视频/挂断视频","后端-》前端",AcceptVideoMessage.class),
    Finish_Video(330, "挂断视频","前端-》后端",AcceptVideoMessage.class),

    Request_Video_Candidate(340,"请求交换Candidate地址","前端-》后端",AcceptVideoMessage.class),
    Other_Request_Video_Candidate(341,"他人请求交换Candidate地址","后端-》前端",AcceptVideoMessage.class),
    Send_Video_Candidate(342,"返回交换Candidate地址","前端-》后端", AcceptVideoMessage.class),
    Other_Send_Video_Candidate(343,"他人返回交换Candidate地址","后端-》前端", AcceptVideoMessage.class),

    //--------------队列相关  400-500------------------
    Request_Enter_Queue(400, "进入排队队列","前端-》后端",RequestWaitQueue.class),
    Request_Outer_Queue(401, "领取排队队列","前端-》后端",RequestWaitQueue.class),
    Request_Outer_Queue_Success(402, "领取排队队列成功","后端-》前端",RequestWaitQueue.class),
    Wait_Queue_Info(410, "排队信息（广播通知）","后端-》前端",WaitQueueInfoMsg.class),
    Request_Quit_Room(420, "用户主动离开房间/队列","前端-》后端",IBaseMsg.class),
    Quit_Room(421, "用户离开房间（广播通知）","后端-》前端",IBaseMsg.class),
    Request_All_Queue(430, "获取队列信息","前端-》后端",IBaseMsg.class),
    All_Queue_Info(431, "队列信息","后端-》前端",WaitQueueInfoMsg.class),

    //--------------房间相关  500-600------------------
    //只有在房间中的用户才能发起聊天
    Request_All_Room_Info(540, "请求获取所有房间信息","前端-》后端", AllRoomInfoMsg.class),
    All_Room_Info(541, "返回所有房间信息","后端-》前端", AllRoomInfoMsg.class),
    Request_Current_Room_Info(550, "请求当前房间信息","前端-》后端",CurrentRoomInfoMsg.class),
    Current_Room_Info(551, "返回房间信息","后端-》前端",CurrentRoomInfoMsg.class),
    Room_Disband_Info(560, "房间解除信息","后端-》前端",CurrentRoomInfoMsg.class),

    ;

    /**
     * 枚举值
     */
    private int intValue;
    /**
     * 业务名称
     */
    private String business;
    /**
     * 传递方向
     */
    private String purpose;
    /**
     * 附件信息类型
     */
    Class<? extends IBaseMsg> classType;

    public static MsgTypeEnum search(int msgType) {
        Optional<MsgTypeEnum> findFirst = Arrays.stream(MsgTypeEnum.values())
                .filter(p -> p.getIntValue()==msgType)
                .findFirst();
        return findFirst.orElse(null);
    }

    /**
     * 生成所有参数名的map
     * @param args
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
//    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
//        Map<String,String> paramNameMap = new HashMap<>();
//        for (MsgTypeEnum value : MsgTypeEnum.values()) {
//            JSONObject jsonObject = JSONObject.from(value.getClassType().newInstance());
//            for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
//                paramNameMap.put(stringObjectEntry.getKey(),"请输入用途");
//            }
//        }
//
//        StringJoiner result = new StringJoiner("\n");
//        paramNameMap.forEach((key,value)->{
//            result.add("put(\""+key+"\",\"请输入含义\");");
//        });
//        System.out.println(result);
//    }

    /**
     * 打印所有协议信息
     * @param args
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
//    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
//        Map<String,String> useMap = new HashMap<>(){
//            {
//                put("msgType","协议编号");
//                put("role","角色");
//                put("sign","加密串");
//                put("pageSize","页面数量");
//                put("requestUserId","视频请求人userId");
//                put("isOnline","是否在线");
//                put("receiveMsgType","接收到的协议id");
//                put("deviceId","设备号（随机生成）");
//                put("roomId","房间号");
//                put("sendTimeDate","发送时间");
//                put("receiveUserId","接收人userId");
//                put("total","总数量");
//                put("appId","平台id");
//                put("waitTotal","排队等待总人数");
//                put("contentType","消息文本类型");
//                put("timestamp","时间戳");
//                put("receiveSign","接收加密串");
//                put("info","额外信息（后端不处理）");
//                put("creator","房间创建人");
//                put("waitIndex","排队第几位");
//                put("messageId","消息id");
//                put("roomInfoMsgs","所有房间信息集合List");
//                put("message","消息内容");
//                put("userId","用户id");
//                put("version","版本");
//                put("sendTime","发送时间");
//                put("pageIndex","页码");
//                put("sender","发送人");
//                put("unique","消息唯一id");
//                put("friendUserId","朋友userId");
//                put("mark","预留自传字段（原样返回给前端，后端不处理）");
//                put("retractTime","消息撤销时间");
//            }
//        };
//        System.out.println("| 业务                 | 业务类型 | 通讯方向         | 协议内容 |");
//        System.out.println("|--------------------|------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|");
//        for (MsgTypeEnum value : MsgTypeEnum.values()) {
//            String content = "{";
//            JSONObject jsonObject = JSONObject.from(value.getClassType().newInstance());
//            for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
//                content+="\""+stringObjectEntry.getKey()+"\":\""+useMap.get(stringObjectEntry.getKey())+"\",";
//            }
//            content+="}";
//
//            System.out.println("| "+value.getBusiness()+"  | "+value.getIntValue()+" | "+value.getPurpose()+"    | "+
//                    content+" |");
//        }
//    }

}
