package me.ctf.lm.service;

/**
 * 分布式锁
 *
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 14:56
 */
public interface DistributedLockService {
    /**
     * 锁
     *
     * @param lockName
     * @return
     */
    boolean lock(String lockName);

    /**
     * 释放锁
     *
     * @param lockName
     */
    void release(String lockName);
}
