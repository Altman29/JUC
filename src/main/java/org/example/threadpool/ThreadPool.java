package org.example.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 */
@Slf4j(topic = "c.ThreadPool")
public class ThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    //线程集合 共享资源 非线程安全的 需要保证 所以execute需要 synchronized
    private HashSet<Worker> workers = new HashSet();

    // 核心线程数
    private int coreSize;

    // 获取任务的超时时间
    private long timeout;
    private TimeUnit timeUnit;

    // 拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.rejectPolicy = rejectPolicy;
        taskQueue = new BlockingQueue<>(queueCapacity);
    }


    public void execute(Runnable task) {
        // 1.当任务数没有超过coreSize 直接交给Worker执行
        // 2.如果超过了，就加入任务队列暂存
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增 worker {}, {}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
//                taskQueue.put(task);
                // 1.死等
                // 2.带超时时间等待
                // 3.让调用者放弃任务执行
                // 4.让调用者抛出异常
                // 5.让调用者自己执行任务
                // ... (写这就写死了，搞一个策略模式让调用者自己决定什么策略)
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }


    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //执行任务
            // 1.当task不为空，执行任务
            // 2.当执行完了，再接着从任务队列获取任务
//            while (task != null || (task = taskQueue.take()) != null) {//死等
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {//有超时时间
                try {
                    log.debug("正在执行... {}", task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            synchronized (workers) {
                log.debug("worker 被移除 {}", this);
                workers.remove(this);
            }
        }
    }
}

//拒绝策略,只有一个方法，做成函数式接口
@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}
