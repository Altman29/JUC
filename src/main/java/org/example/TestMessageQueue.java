package org.example;

import java.util.concurrent.TimeUnit;

public class TestMessageQueue {
    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);

        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(() -> {
                queue.put(new Message(id, "值" + id));
            }, "生产者" + id).start();
        }


        new Thread(() -> {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    Message message = queue.take();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }, "消费者").start();
    }
}
