package cn.gyyx.im.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: growth-diary-server
 * @description: redis服务
 * @author: yuanshuai
 * @create: 2024-04-10 10:09
 **/
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;



    /**
     * 获取指定格式的key
     *
     * @param pattern 格式
     * @return set
     */
    public Set<String> getKeys(String pattern) {
        Set<String> keys = new HashSet<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();

        Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                connection -> connection.scan(options)
        );

        while (null != cursor && cursor.hasNext()) {
            keys.add(new String(cursor.next()));
        }

        if (null != cursor) {
            cursor.close();
        }

        return keys;
    }


    /* ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ String ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ */

    /**
     * 获取指定key的值
     */
    public String get(String key) {
        return String.valueOf(redisTemplate.opsForValue().get(key));
    }

    /**
     * 删除指定的key值
     *
     * @param key key
     * @return {@link String}
     */
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }


    /**
     * 批量删除指定的key值
     *
     * @param keys key
     * @return {@link Long}
     */
    public Long dels(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 指定key的数值执行原子的加1操作
     */
    public void incr(String key) {
        redisTemplate.opsForValue().increment(key);
    }


    /**
     * 指定key的数值执行原子的减1操作，但最小值为0
     *
     * @param key Redis中的key
     */
    public void decr(String key) {
        String script = "local value = redis.call('get', KEYS[1]) " +
                "if value and tonumber(value) > 0 then " +
                "return redis.call('decr', KEYS[1]) " +
                "else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        redisTemplate.execute(redisScript, Collections.singletonList(key));
    }

    /**
     * 指定key的数值执行原子的减去指定值操作，但最小值为0
     *
     * @param key    Redis中的key
     * @param amount 要减去的数值
     */
    public void decrByStep(String key, long amount) {
        String script = "local value = redis.call('get', KEYS[1]) " +
                "if value and tonumber(value) >= tonumber(ARGV[1]) then " +
                "return redis.call('decrby', KEYS[1], tonumber(ARGV[1])) " +
                "else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        redisTemplate.execute(redisScript, Collections.singletonList(key), String.valueOf(amount));
    }


    /* ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ bitMaps ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ */

    /**
     * 设置指定位的值
     *
     * @param key    键
     * @param offset 偏移量 0开始 对应bit的位置
     * @param value  true为1，false为0
     * @return boolean
     */
    public Boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 获取指定位的值
     *
     * @param key    键
     * @param offset 偏移量 0开始
     * @return boolean
     */
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }



    /**
     * 批量统计字符串被设置为1的bit总数
     *
     * @param keys 键
     * @return {@link Long}
     */
    public Long pipelineBitCount(List<String> keys) {
        List<Object> results = redisTemplate.executePipelined(
                (RedisCallback<Long>) connection -> {
                    for (String key : keys) {
                        connection.bitCount(key.getBytes());
                    }
                    return null;
                });

        long totalCount = 0;
        for (Object result : results) {
            if (result != null) {
                totalCount += Integer.parseInt(String.valueOf(result));
            }
        }

        return totalCount;
    }


    /**
     * 统计字符串被设置为1的bit数
     *
     * @param key 键
     * @return long
     */
    public Long bitCount(String key) {
        return redisTemplate.execute(
                (RedisCallback<Long>) connection -> connection.bitCount(key.getBytes())
        );
    }

    /**
     * 统计字符串指定位上被设置为1的bit数
     *
     * @param key   键
     * @param start 开始位置  注意对应byte的位置,是bit位置*8
     * @param end   结束位置
     * @return long
     */
    public Long bitCount(String key, long start, long end) {
        return redisTemplate.execute(
                (RedisCallback<Long>) connection -> connection.bitCount(key.getBytes(), start, end)
        );
    }

    /**
     * 不同字符串之间进行位操作
     *
     * @param op      操作类型：与、或、异或、否
     * @param destKey 最终存放结构的键
     * @param keys    要操作的键
     * @return Long
     */
    public Long bitOp(RedisStringCommands.BitOperation op, String destKey, Collection<String> keys) {
        int size = keys.size();
        byte[][] bytes = new byte[size][];

        int index = 0;
        for (String key : keys) {
            bytes[index++] = key.getBytes();
        }
        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(op, destKey.getBytes(), bytes));
    }

    /**
     * 查找所有bit为1的位置
     *
     * @param key 关键
     * @return {@link List}<{@link Long}>
     */
    public List<Long> findAllSetBits(String key, Long limit) {
        List<Long> setBits = new ArrayList<>();
        int offset = 0;
        int bitSize = 32;
        boolean hasMoreBits = true;

        while (hasMoreBits) {
            // 使用BitFieldSubCommandsBuilder构建BITFIELD命令
            BitFieldSubCommands bitFieldSubCommands = BitFieldSubCommands.create()
                    .get(BitFieldSubCommands.BitFieldType.unsigned(bitSize)).valueAt(offset);

            // 执行命令并获取结果
            List<Long> results = redisTemplate.opsForValue().bitField(key, bitFieldSubCommands);
            if (results != null && !results.isEmpty()) {
                long bitValue = results.get(0);
                String binaryString = Long.toBinaryString(bitValue);
                if (binaryString.length() < bitSize) {
                    // 补0
                    binaryString = String.format("%" + bitSize + "s", binaryString)
                            .replace(' ', '0');
                }
                // 获取所有为1的位
                for (int i = 0; i < binaryString.length(); i++) {
                    if (binaryString.charAt(i) == '1') {
                        setBits.add((long) (offset + i));
                    }
                }
            } else {
                // 没有更多结果时停止循环
                hasMoreBits = false;
            }
            // 终止条件
            if (offset > limit) {
                hasMoreBits = false;
            }

            offset += bitSize;
        }

        return setBits;
    }
}


