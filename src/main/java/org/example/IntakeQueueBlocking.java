package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IntakeQueueBlocking {
    private final BlockingQueue<TestOrder> emergencyQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<TestOrder> normalQueue = new LinkedBlockingQueue<>();
    private final int capacity;

    public IntakeQueueBlocking(int capacity) {
        this.capacity = capacity;
    }

    private int totalSize() {
        return emergencyQueue.size() + normalQueue.size();
    }

    public void produce(TestOrder order) throws InterruptedException {
        // block until there is space
        while (totalSize() >= capacity) {
            Thread.sleep(10);
        }

        if (order.priority == TestOrder.Priority.EMERGENCY)
            emergencyQueue.put(order);
        else
            normalQueue.put(order);
    }

    public TestOrder consume(boolean emergencyFirst) throws InterruptedException {

        if (emergencyFirst) {
            TestOrder em = emergencyQueue.poll();
            if (em != null) return em;
        }

        TestOrder normal = normalQueue.poll();
        if (normal != null) return normal;

        // nothing available → block
        return emergencyQueue.take();
    }

    public void setExpiration() {
        LogWriter.log("============= System set to ShutDown =============");

        normalQueue.forEach(o ->
                LogWriter.log(o + " expired due to system timeout"));
        emergencyQueue.forEach(o ->
                LogWriter.log(o + " expired due to system timeout"));

        normalQueue.clear();
        emergencyQueue.clear();
    }
}
