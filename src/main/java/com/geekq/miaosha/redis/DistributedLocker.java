package com.geekq.miaosha.redis;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 */
public interface DistributedLocker {

    /**
     * 加锁
     * @param lockKey
     */
    void lock(String lockKey);

    /**
     * 解锁
     * @param lockKey
     */
    void unlock(String lockKey);

    /**
     * 加锁+过期时间
     * @param lockKey
     * @param timeout
     */
    void lock(String lockKey, int timeout);

    /**
     * 加锁+过期时间+时间单位
     * @param lockKey
     * @param unit
     * @param timeout
     */
    void lock(String lockKey, TimeUnit unit , int timeout);
}
