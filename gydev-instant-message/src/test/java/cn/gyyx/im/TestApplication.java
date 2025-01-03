package cn.gyyx.im;

import cn.gydev.lib.bean.ResultBean;
import cn.gyyx.im.platform.feign.DaoUserFeign;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@Slf4j
@SpringBootTest
public class TestApplication {

    @Autowired
    private DaoUserFeign daoUserFeign;

    @Test
    public void createTbSql(){

    }

    @Test
    public void alterTbSql(){
        int tableIndex = 0;
        while (tableIndex < 100) {
            String tableName = tableIndex >= 10 ? String.valueOf(tableIndex) : "0" + tableIndex;
            String sql = "ALTER TABLE chat_wave_db.im_chat_log_" + tableName + " ADD content_type varchar(50) DEFAULT 'text' NULL COMMENT '消息内容类型';";
            System.out.println(sql);
            tableIndex++;
        }
    }

    @Test
    public void updateTbSql(){
        String sql = "UPDATE";
    }

    @Test
    public void deleteTbSql(){
        int tableIndex = 0;
        while (tableIndex < 100) {
            String tableName = tableIndex >= 10 ? String.valueOf(tableIndex) : "0" + tableIndex;
            String sql = "ALTER TABLE chat_wave_db.im_chat_log_" + tableName + " DROP COLUMN contentType;";
            System.out.println(sql);
            tableIndex++;
        }
    }
    @Test
    public void test(){
        ResultBean<List<String>> chatList = daoUserFeign.getChatList(11154);
        System.out.println(chatList.getData());
    }
}
