package me.ctf.lm.service;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 18:00
 */
public interface DistributeTaskService<T> {
    /**
     * 分发任务
     *
     * @param t
     */
    void distribute(T t);
}
