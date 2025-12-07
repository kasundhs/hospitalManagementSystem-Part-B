package org.example;

public class Auditor implements Runnable {

    private final SystemStateMonitor state;
    private final ProcessedOrderQueueMonitor processedOrderQueue;
    private final String name;
    private Thread thread;
    private volatile boolean running = true;

    public Auditor(SystemStateMonitor state, ProcessedOrderQueueMonitor processedOrderQueue, String name) {
        this.state = state;
        this.processedOrderQueue = processedOrderQueue;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (running) {
                TestOrder order = processedOrderQueue.consumeForReport();

                if (order != null) {
                    LogWriter.log(name + " generating report for " + order);

                    ReportGenrator reportGenrator = new ReportGenrator(order);
                    reportGenrator.reportDetails(order);

                    state.incrementReportCount();

                    state.lockRead();
                    LogWriter.log("Processed: " + state.getTotalProcessed() +
                            ", Reports: " + state.getTotalReportGenerateCount());
                    state.unlockRead();
                }
                processedOrderQueue.releaseProcessingLock();
                Thread.sleep(300);
            }

        } catch (InterruptedException e) {
            // Interruption is expected during shutdown, restore interrupt status
            Thread.currentThread().interrupt();
            LogWriter.log(name + " interrupted - shutting down");
        } finally {
            processedOrderQueue.releaseProcessingLock();
        }
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
