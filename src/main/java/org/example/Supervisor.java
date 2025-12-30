package org.example;

import java.util.Random;

public class Supervisor implements Runnable {

    private final SystemStateMonitor state;
    private final EventScheduler event;
    private final String name;
    private Thread thread;
    private final Random rnd = new Random();
    private volatile boolean running = true;

    public Supervisor(SystemStateMonitor state, EventScheduler event, String name) {
        this.state = state;
        this.event = event;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (running) {
                event.addConsumer();   // add producer if needed.
                if(state.getEmergencyPatientCount() < 2){
                    state.setEmergencyPriorityEnabled(false);
                }
                else {
                    state.setEmergencyPriorityEnabled(true);
                }
                LogWriter.log(name + " updated emergency prioritization to " + state.isEmergencyPriorityEnabled());
                event.addProducers();   // add producer if needed.
                Thread.sleep(rnd.nextInt(1000));
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
