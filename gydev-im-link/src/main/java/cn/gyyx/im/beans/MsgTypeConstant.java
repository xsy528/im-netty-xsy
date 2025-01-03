package cn.gyyx.im.beans;

public interface MsgTypeConstant {

    /**
     * 参数异常
     */
    Integer Param_Error = 10;

    /**
     * "心跳","前端-》后端"
     */
    Integer Heartbeat = 20;
    /**
     * "心跳响应","后端-》前端"
     */
    Integer Heartbeat_SUCCESS = 21;
    /**
     * "服务器接收消息成功","后端-》前端"
     */
    Integer Server_Receive_Success = 30;
    /**
     * "客户端接收消息成功","前端-》后端"
     */
    Integer Client_Receive_Success = 31;
    /**
     * "服务端重启，维护，通常用于服务端关闭事件通知客户端","后端-》前端"
     */
    Integer Service_Maintenance = 99;

}
