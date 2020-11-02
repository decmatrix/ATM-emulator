package com.bsokolovskyi.postBoxUtils;

import java.util.*;

public class PostBox<T> {
    private final Queue<T> messageList;
    private final int size;
    private boolean postBoxClosed = false;

    public PostBox(int size) {
        this.size = size;
        this.messageList = new LinkedList<>();
    }

    public synchronized boolean postBoxClosed() {
        return postBoxClosed;
    }

    public synchronized void closePostBox() {
        postBoxClosed = true;
    }

    public synchronized void sendMessage(T message) {
        if(isFull()) {
            return;
        }

        Objects.requireNonNull(message);
        messageList.offer(message);
    }

    public synchronized T nextMessage() {
        return messageList.poll();
    }

    public synchronized boolean isEmpty() {
        return messageList.isEmpty();
    }

    public synchronized boolean isFull() {
        return messageList.size() == size;
    }
}
