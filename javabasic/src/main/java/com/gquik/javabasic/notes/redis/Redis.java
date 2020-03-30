package com.gquik.javabasic.notes.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class Redis {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void set(String key,String value){
        try {
            stringRedisTemplate.opsForValue().set(key,value);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("Redis缓存写入异常-"+e.getMessage());
        }
    }

    public String get(String key){
        String value = "";
        try {
            value = stringRedisTemplate.opsForValue().get(key);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("Redis缓存读取异常-"+e.getMessage());
        }
        return value;
    }

    //带过期时间
    public void set(String key, String value, Long times, TimeUnit unit){
        try {
            stringRedisTemplate.opsForValue().set(key,value,times,unit);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("Redis缓存写入异常-"+e.getMessage());
        }
    }

    public static void main(String[] args) {
    }
}
