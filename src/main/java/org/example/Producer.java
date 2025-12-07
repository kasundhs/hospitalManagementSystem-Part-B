package org.example;

import java.util.Random;

public class Producer implements Runnable {

    private final IntakeQueueBlocking queue;
    private final SystemStateMonitor state;
    private final String name;
    private volatile boolean running = true;
    private Thread thread;
    private final Random rnd = new Random();

    public Producer(IntakeQueueBlocking queue, SystemStateMonitor state, String name) {
        this.queue = queue;
        this.state = state;
        this.name = name;
    }

    @Override
    public void run() {
        String[] types = {"PCR", "Blood Test", "Histopathology"};

        try {
            while (running) {
                TestOrder.Priority priority = rnd.nextInt(10) < 5 ? TestOrder.Priority.EMERGENCY : TestOrder.Priority.NORMAL;
                TestOrder.IsSpecialTest special = rnd.nextInt(10) < 2 ? TestOrder.IsSpecialTest.YES : TestOrder.IsSpecialTest.NO;
                TestOrder order = new TestOrder(types[rnd.nextInt(types.length)], priority, special);

                if (order.isSpecialTest == TestOrder.IsSpecialTest.YES) {
                    LogWriter.log(order + " rejected (special test unavailable)");
                    continue;
                }

                if (priority == TestOrder.Priority.EMERGENCY)
                    state.setEmergencyPatientCount();
                queue.produce(order);
                LogWriter.log(name + " registered " + order);
                Thread.sleep(100 + rnd.nextInt(300));
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
