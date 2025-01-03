CREATE TABLE [SecurityMsgDB-Log].dbo.chat_queue_tb (
                                                       code int IDENTITY(1,1) NOT NULL,
    platform varchar(100) NULL,
    user_id varchar(100) NULL,
    create_time datetime DEFAULT getdate() NOT NULL,
    finish_time datetime NULL,
    receiver varchar(100) NULL,
    status int NULL,
    CONSTRAINT chat_queue_tb_pk PRIMARY KEY (code)
    );
EXEC [SecurityMsgDB-Log].sys.sp_addextendedproperty 'MS_Description', N'聊天队列表', 'schema', N'dbo', 'table', N'chat_queue_tb';
EXEC [SecurityMsgDB-Log].sys.sp_addextendedproperty 'MS_Description', N'主键', 'schema', N'dbo', 'table', N'chat_queue_tb', 'column', N'code';
EXEC [SecurityMsgDB-Log].sys.sp_addextendedproperty 'MS_Description', N'聊天平台', 'schema', N'dbo', 'table', N'chat_queue_tb', 'column', N'platform';
EXEC [SecurityMsgDB-Log].sys.sp_addextendedproperty 'MS_Description', N'等待人', 'schema', N'dbo', 'table', N'chat_queue_tb', 'column', N'user_id';
EXEC [SecurityMsgDB-Log].sys.sp_addextendedproperty 'MS_Description', N'创建时间', 'schema', N'dbo', 'table', N'chat_queue_tb', 'column', N'create_time';
EXEC [SecurityMsgDB-Log].sys.sp_addextendedproperty 'MS_Description', N'出队列时间', 'schema', N'dbo', 'table', N'chat_queue_tb', 'column', N'finish_time';
EXEC [SecurityMsgDB-Log].sys.sp_addextendedproperty 'MS_Description', N'接收人/客服', 'schema', N'dbo', 'table', N'chat_queue_tb', 'column', N'receiver';
EXEC [SecurityMsgDB-Log].sys.sp_addextendedproperty 'MS_Description', N'状态', 'schema', N'dbo', 'table', N'chat_queue_tb', 'column', N'status';


CREATE TABLE chat_wave_db.chat_queue_tb (
                                            code INT auto_increment NOT NULL COMMENT '主键',
                                            platform varchar(100) NULL COMMENT '平台',
                                            user_id varchar(100) NULL COMMENT '用户id',
                                            create_time DATETIME DEFAULT now() NULL,
                                            finish_time DATETIME NULL COMMENT '结束时间',
                                            receiver varchar(100) NULL COMMENT '接收人/客服',
                                            status INT NULL COMMENT '状态',
                                            CONSTRAINT chat_queue_tb_pk PRIMARY KEY (code)
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='聊天队列表';
