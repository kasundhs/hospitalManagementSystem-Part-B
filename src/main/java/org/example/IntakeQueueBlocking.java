package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class IntakeQueueBlocking {
    private final BlockingQueue<TestOrder> emergencyQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<TestOrder> normalQueue = new LinkedBlockingQueue<>();
    private final Semaphore capacitySemaphore;
    private boolean isForNormalPatients = true;

    public IntakeQueueBlocking(int capacity) {
        this.capacitySemaphore = new Semaphore(capacity); // global capacity no need to manual focus
    }

    public void produce(TestOrder order) throws InterruptedException {
        capacitySemaphore.acquire();
        if (order.priority == TestOrder.Priority.EMERGENCY) {
            emergencyQueue.put(order);
        } else {
            normalQueue.put(order);
        }
    }

    public TestOrder consume(boolean emergencyFirst) throws InterruptedException {
        TestOrder order = null;
        if (emergencyFirst && !emergencyQueue.isEmpty()) {
            order = emergencyQueue.poll();
        }
        else if (isForNormalPatients && !normalQueue.isEmpty()) {
            order = normalQueue.poll();
            isForNormalPatients = false;
        }
        else if(!emergencyQueue.isEmpty()){
            order = emergencyQueue.poll();
            isForNormalPatients = true;
        }
        capacitySemaphore.release();
        return order;
    }

    public void setExpiration() {
        LogWriter.log("============= System set to ShutDown =============");
        emergencyQueue.forEach(order ->
                LogWriter.log(order + " expired due to system timeout"));
        normalQueue.forEach(order ->
                LogWriter.log(order + " expired due to system timeout"));
        emergencyQueue.clear();
        normalQueue.clear();
        capacitySemaphore.drainPermits();   // Reset semaphore completely
    }
}