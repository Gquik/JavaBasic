package com.gquik.javabasic.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * Redis分布式锁实现方式1
 * @Author GeQuanKai
 * @Date 2020/3/31 10:47
 */
public class RedisLock1 {
    private JedisPool jedisPool = null;
    private static final String LOCK_SUCCESS = "OK";

    /**
     * 尝试获得锁
     * @Author GeQuanKai
     * @Date 2020/3/31 10:48
     * @param key 锁
     * @param requestId 请求标识(区别服务器)
     * @param seconds 超期时间
     */
    public boolean tryLock(String key, String requestId, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            String lockScript = "if (redis.call('exists',KEYS[1]) == 0 or redis.call('get',KEYS[1]) == ARGV[2]) then return redis.call('setex',KEYS[1],ARGV[1],ARGV[2]) else return -1 end";
            Object result = jedis.eval(lockScript, List.of(key), List.of(String.valueOf(seconds), requestId));
            if (null != result && result.equals(LOCK_SUCCESS)) {
                return true;
            }
        }
        return false;
    }

    /**
      * 解锁
      * @Author GeQuanKai
     * @Date 2020/3/31 10:48
     *
     */
    public boolean unLock(String key, String requestId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String unlockScript = "if (redis.call('exists',KEYS[1]) == 0 or redis.call('get',KEYS[1]) == ARGV[1]) then return redis.call('del',KEYS[1]) else return -1 end";
            Object result = jedis.eval(unlockScript, List.of(key), List.of(requestId));
            if (null == result) {
                return false;
            }
            int affectedRows = Integer.parseInt(String.valueOf(result));
            if (1 == affectedRows || 0 == affectedRows) {
                return true;
            }
        }
        return false;
    }

}
