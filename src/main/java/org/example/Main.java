package org.example;

public class Main {
    public static void main(String[] args) {

        IntakeQueueBlocking queue = new IntakeQueueBlocking(20);
        SystemStateMonitor state = new SystemStateMonitor();
        ProcessedOrderQueueMonitor processedQueue = new ProcessedOrderQueueMonitor();

        System.out.println("System Started... Waiting 15 seconds before stopping...");
        Producer p1 = new Producer(queue, state, "Clinic Counter -1");
        Producer p2 = new Producer(queue, state, "Clinic Counter -2");

        Consumer c1 = new Consumer(queue, state, processedQueue, "Doctor -1");
        Consumer c2 = new Consumer(queue, state, processedQueue, "Doctor -2");

        Auditor a1 = new Auditor(state, processedQueue, "Auditor -1");
        Auditor a2 = new Auditor(state, processedQueue, "Auditor -2");

        Supervisor sup = new Supervisor(state, "Supervisor");

        p1.start();
        p2.start();
        c1.start();
        c2.start();
        a1.start();
        a2.start();
        sup.start();

        try {
            Thread.sleep(15000);
        } catch (InterruptedException ignored) {}

        p1.shutdown();
        p2.shutdown();
        queue.setExpiration();

        c1.shutdown();
        c2.shutdown();
        processedQueue.setExpiration();

        a1.shutdown();
        a2.shutdown();
        sup.shutdown();

        System.out.println("System shutdown completed....");
    }
}
