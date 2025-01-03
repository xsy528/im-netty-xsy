# 后端开发流程 2.0 netty
1. 完成下面的ws协议
2. 补充MsgTypeEnum.class
3. 开发通讯结构体，请求放在beans.request包下，返回放在beans.response包下，注意：所有通讯结构体需继承IBaseMsg接口,IBaseMsg.convert(IBaseMsg msg)可以自动拷贝IBaseMsg基础对象
4. 开发对应的Service,路径/service/impl，需实现IMsgHandler，注意：所有服务端发送给前端消息都走IMsgHandler.sendClientMessage() 统一管理
5. 自测方法：打开浏览器,输入网址：http://www.jsons.cn/websocket ，输入ws://127.0.0.1/ws


其他：如需获取当前连接的用户信息，可调用 channel.attr(SESSION).get() 获取，
        返回 beans.netty.cn.gyyx.im.UserSession

# 前端接入流程
名词讲解：
        业务端：指对应业务的服务端如代练，论坛，家事记，客服   
        im：负责聊天的服务端   
        im-web：http接口的im服务端
1. 前端请求video-service.gyyx.cn/im/sign 获取websocket（下面都用ws指代）连接url，参考url:ws://ws-ip:ws-port?appId=1477358668&msgType=10&version=1.10&
   userId=48057&sign=e31a517d51ba8951fdf4d4bd74d6335d&txNo=1708742794846
2. 前端建立ws连接
3. websocket链接成功后需要每隔一定时间后发送心跳信息（间隔根据appId来定，一般是30s）
4. 用户A给用户B发送消息时，前端调用业务服务端校验是否具备聊天权限并返回聊天签名，前端拿到签名后请求im发送消息接口
5. 其他功能及消息协议：参考下述协议


# ws协议
| 业务                 | 业务类型 | 通讯方向         | 协议内容 |
|--------------------|------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 参数错误  | 10 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 心跳  | 20 | 前端-》后端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 心跳响应  | 21 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 服务器接收消息成功  | 30 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","receiveMsgType":"接收到的协议id","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 客户端接收消息成功  | 31 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","receiveMsgType":"接收到的协议id","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 服务端重启，维护，通常用于服务端关闭事件通知客户端  | 99 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 在线状态  | 101 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 获取未读消息  | 102 | 前端-》后端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 客户端发送消息  | 130 | 前端-》后端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 客户端推送成功  | 131 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 聊天签名过期失效  | 132 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 聊天敏感词拦截  | 133 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 服务端推送消息  | 140 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 服务端推送成功  | 141 | 前端-》后端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 用户查看某人的消息  | 160 | 前端-》后端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 用户获取历史聊天消息  | 161 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","pageIndex":"页码","pageSize":"页面数量","sender":"发送人","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 返回历史聊天消息  | 162 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 首次登录，拉取每个用户的首条消息  | 1170 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 获取未读消息总数  | 180 | 前端-》后端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 消息撤回  | 190 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","messageId":"消息id","msgType":"协议编号","receiveUserId":"接收人userId","retractTime":"消息撤销时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 通知消息发送人消息撤回成功  | 191 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 通知消息接收人消息撤回成功  | 192 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 消息已读  | 198 | 前端-》后端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 消息已读成功  | 199 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 用户登出/关闭通讯  | 251 | 前端-》后端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 用户登出成功  | 252 | 后端-》前端    | {"appId":"平台id","contentType":"消息文本类型","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","messageId":"消息id","msgType":"协议编号","receiveSign":"接收加密串","receiveUserId":"接收人userId","sendTime":"发送时间","sendTimeDate":"发送时间","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 好友上线  | 200 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","friendUserId":"朋友userId","isOnline":"是否在线","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 好友下线  | 210 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","friendUserId":"朋友userId","isOnline":"是否在线","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 请求发起视频  | 300 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","msgType":"协议编号","requestUserId":"视频请求人userId","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 他人请求发起视频  | 301 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","msgType":"协议编号","requestUserId":"视频请求人userId","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 接受视频  | 310 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","msgType":"协议编号","requestUserId":"视频请求人userId","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 他人接受视频  | 311 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","msgType":"协议编号","requestUserId":"视频请求人userId","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 拒绝视频  | 320 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","msgType":"协议编号","requestUserId":"视频请求人userId","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 他人拒绝视频/挂断视频  | 321 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","msgType":"协议编号","requestUserId":"视频请求人userId","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 挂断视频  | 330 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","message":"消息内容","msgType":"协议编号","requestUserId":"视频请求人userId","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 进入排队队列  | 400 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","role":"角色","roomId":"房间号","sign":"加密串","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 领取排队队列  | 401 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","role":"角色","roomId":"房间号","sign":"加密串","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 排队信息（广播通知）  | 410 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本","waitIndex":"排队第几位","waitTotal":"排队等待总人数",} |
| 用户主动离开房间/队列  | 420 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 用户离开房间（广播通知）  | 421 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 获取队列信息  | 430 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 队列信息  | 431 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本","waitIndex":"排队第几位","waitTotal":"排队等待总人数",} |
| 请求获取所有房间信息  | 540 | 前端-》后端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","roomInfoMsgs":"所有房间信息集合List","timestamp":"时间戳","total":"总数量","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 返回所有房间信息  | 541 | 后端-》前端    | {"appId":"平台id","deviceId":"设备号（随机生成）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","roomInfoMsgs":"所有房间信息集合List","timestamp":"时间戳","total":"总数量","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 请求当前房间信息  | 550 | 前端-》后端    | {"appId":"平台id","creator":"房间创建人","deviceId":"设备号（随机生成）","info":"额外信息（后端不处理）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","roomId":"房间号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |
| 返回房间信息  | 551 | 后端-》前端    | {"appId":"平台id","creator":"房间创建人","deviceId":"设备号（随机生成）","info":"额外信息（后端不处理）","mark":"预留自传字段（原样返回给前端，后端不处理）","msgType":"协议编号","roomId":"房间号","timestamp":"时间戳","unique":"消息唯一id","userId":"用户id","version":"版本",} |


# 前端接入涉及内容及协议
请求video-service.gyyx.cn/im/sign获取ws连接串-》ws连接-》定时发送MsgType=20心跳
# 发送消息涉及协议
发送MsgType=30 -》 服务器推送 MsgType=40
# 视频聊天涉及协议
用户角色MsgType=400进入排队，前端展示排队效果-》服务器推送MsgType=401告知排队情况-》队列信息有变化，MsgType=401告知排队情况-》继续等待/用户主动退出排队MsgType=403/用户断联-》客服角色MsgType=400消耗队列-》建立房间，服务器推送当前房间信息MsgType=406，并携带房间密钥
-》用户端前端发起视频聊天申请MsgType=300，并携带sdp信息-》客服端接收请求MsgType=300，处理sdp协议，自动接受视频MsgType=301，返回客服Candidate地址-》用户端接收到MsgType=301及客服Candidate地址开始建立视频链接
-》视频结束/用户主动结束MsgType=302，服务器关闭房间信息，释放房间资源-》客服端是否保存视频，前端提交上传视频

# im链路架构图
http://lili-online.cloud/webapp/index.html?lightbox=1&title=%E8%A7%86%E9%A2%91%E5%AE%A2%E6%9C%8D%E6%9E%B6%E6%9E%84#UchartId=520
# im聊天业务时序图
http://lili-online.cloud/create?fileId=160&fileName=IM%E6%97%B6%E5%BA%8F%E5%9B%BE&view=1http://lili-online.cloud/create?fileId=160
# im视频业务时序图
http://lili-online.cloud/webapp/index.html?lightbox=1&title=%E8%A7%86%E9%A2%91%E5%AE%A2%E6%9C%8D#UchartId=519
