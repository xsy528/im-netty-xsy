package cn.gyyx.im.service;

import cn.gyyx.im.enums.Constant;
import cn.gyyx.im.utils.DateTimeUnit;
import cn.gyyx.im.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RecordImService {

    public static void recordMaxNumber(RedisTemplate<String,String> redisTemplate,int currentNumber,String record,int leaveSeconds){
        //记录最大在线人数
        Object object = redisTemplate.opsForValue().get(record);
        int number = object==null?0:Integer.valueOf(String.valueOf(object));

        //只有超过最大数量时才重新记录
        if(currentNumber>number) {
            //记录最大登录数
            if (leaveSeconds > 0) {
                redisTemplate.opsForValue().set(record, String.valueOf(currentNumber),
                        leaveSeconds, TimeUnit.SECONDS);
                return;
            }
        }
        log.info("记录{}失败，信息：{}，{}，{}",record,currentNumber,number,leaveSeconds);
    }

    public static void recordTotalNumber(RedisTemplate<String,String> redisTemplate,String record,int leaveSeconds){
        //记录每日发送消息数量
        Object object = redisTemplate.opsForValue().get(record);
        int number = object==null?0:Integer.valueOf(String.valueOf(object));
        number++;
        if(leaveSeconds>0) {
            redisTemplate.opsForValue().set(record,String.valueOf(number),leaveSeconds,TimeUnit.SECONDS);
            return;
        }
        log.info("记录{}失败，信息：{}，{}",record,number,leaveSeconds);
    }


    public static void recordTotalUserNumber(RedisTemplate<String,String> redisTemplate,String sessionKey,int leaveSeconds){
        String online = redisTemplate.opsForValue().get(Constant.getDayLoginKey(sessionKey));
        if(StringUtils.isEmpty(online)) {
            String record = Constant.IM_TOTAL_USE_NUMBER;
            Object object = redisTemplate.opsForValue().get(record);
            int number = object == null ? 0 : Integer.valueOf(String.valueOf(object));
            number++;
            if (leaveSeconds > 0) {
                redisTemplate.opsForValue().set(record, String.valueOf(number), leaveSeconds, TimeUnit.SECONDS);
                return;
            }
        }
        log.info("记录每日总使用人数失败，信息：{}，{}",online,leaveSeconds);
    }
}
