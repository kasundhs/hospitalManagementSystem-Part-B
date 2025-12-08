package org.example;

import java.util.Random;

public class Consumer implements Runnable {

    private final IntakeQueueBlocking queue;
    private final SystemStateMonitor state;
    private final ProcessedOrderQueueMonitor processedOrderQueue;
    private final String name;
    private Thread thread;
    private final Random rnd = new Random();
    private volatile boolean running = true;

    public Consumer(IntakeQueueBlocking queue, SystemStateMonitor state,
                    ProcessedOrderQueueMonitor processedOrderQueue, String name) {

        this.queue = queue;
        this.state = state;
        this.processedOrderQueue = processedOrderQueue;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (running) {
                TestOrder order = queue.consume(state.isEmergencyPriorityEnabled());
                if (order == null) continue;
                if (order.priority == TestOrder.Priority.EMERGENCY)
                    state.decrementEmergencyPatientCount();
                LogWriter.log(name + " processing " + order);
                Thread.sleep(200 + rnd.nextInt(250));
                state.incrementProcessed();
                processedOrderQueue.addProcessedOrder(order);
            }

        } catch (InterruptedException ignored) {}
    }

    public void start() {
        thread = new Thread(this, name);
        thread.start();
    }

    public void shutdown() {
        running = false;
        if (thread != null) thread.interrupt();
    }
}
