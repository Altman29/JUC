package org.example;



import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * 自定义消息队列
 * 生产者/消费者
 * java线程间通信
 */
@Slf4j
public class MessageQueue {

    //消息的队列结合
    private final LinkedList<Message> list = new LinkedList<>();
    //队列容量
    private final int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 获取消息
     */
    public Message take() {
        synchronized (list) {
            while (list.isEmpty()) {
                try {
                    log.debug("队列为空，消费者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //从队列头部取消息并返回
            Message message = list.removeFirst();
            log.debug("已消费消息，{}", message);
            list.notifyAll();
            return message;
        }
    }

    /**
     * 存入消息
     */
    public void put(Message message) {
        synchronized (list) {
            while (list.size() == capacity) {
                try {
                    log.debug("队列以满，生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //将消息存入消息队列尾
            list.addLast(message);
            log.debug("已生产信息，{}", message);
            list.notifyAll();
        }
    }

}


final class Message {
    private final int id;
    private final Object value;

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}