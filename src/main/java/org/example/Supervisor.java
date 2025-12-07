package org.example;

import java.util.Random;

public class Supervisor implements Runnable {

    private final SystemStateMonitor state;
    private final String name;
    private Thread thread;
    private final Random rnd = new Random();
    private volatile boolean running = true;

    public Supervisor(SystemStateMonitor state, String name) {
        this.state = state;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (running) {

                state.lockWrite();

                boolean enable = state.getEmergencyPatientCount() >= 2;
                state.setEmergencyPriorityEnabled(enable);

                LogWriter.log(name + " updated emergency prioritization to " + enable);

                state.unlockWrite();

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
