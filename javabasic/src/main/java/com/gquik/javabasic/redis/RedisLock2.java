package com.gquik.javabasic.redis;

import redis.clients.jedis.Jedis;

import java.util.Collections;

public class RedisLock2 {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 尝试获取分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
        //redis.clients.jedis 2.9.0版本才有多参数set(),其他版本不能
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 释放分布式锁(推荐lua脚本)
     * @param jedis Redis客户端
     * @param key 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean unLock(Jedis jedis, String key, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

    /**
     * 释放分布式锁(简单方式,双命令,不具备原子性)
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean unLock2(Jedis jedis, String lockKey, String requestId) {
        if (requestId.equals(jedis.get(lockKey))) {
            jedis.del(lockKey);
            return true;
        }
        return false;

    }
}
