package org.example.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c.main")
public class TestThreadPool {
    public static void main(String[] args) {
        ThreadPool threadPool =
                new ThreadPool(1, 1000, TimeUnit.MILLISECONDS, 1,
//                        (queue, task) -> {
//                            queue.put(task);// 1.死等策略
//                        }
//                        ((queue, task) -> {// 2.带超时等待
//                            queue.offer(task,1500,TimeUnit.MILLISECONDS);
//                        })
//                        ((queue, task) -> {
//                            log.debug("放弃 {}", task);//3.放弃
//                        })
//                        ((queue, task) -> {
//                            throw new RuntimeException("任务执行失败 "+task);//4.让调用者抛出异常
//                        })
                        ((queue, task) -> {
                            log.debug("自己执行 {}", task);
                            task.run();// 5.让调用者自己执行
                        })
                );

        //主线程创建5个任务交给线程池执行
        for (int i = 0; i < 3; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("{}", j);
            });
        }
    }
}
