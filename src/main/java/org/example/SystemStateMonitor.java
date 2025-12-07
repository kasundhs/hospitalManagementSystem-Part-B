package org.example;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SystemStateMonitor {

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private int totalProcessedReportCount = 0;
    private int emergencyPatientCount = 0;
    private boolean emergencyPriorityEnabled = true;
    private int totalReportGenerateCount = 0;

    // ---------------- READ LOCK ----------------
    public void lockRead() {
        rwLock.readLock().lock();
    }

    public void unlockRead() {
        rwLock.readLock().unlock();
    }

    // ---------------- WRITE LOCK ----------------
    public void lockWrite() {
        rwLock.writeLock().lock();
    }

    public void unlockWrite() {
        rwLock.writeLock().unlock();
    }

    // ---------- STATE VARIABLES (unchanged) ----------
    public synchronized void incrementProcessed() {totalProcessedReportCount++;}
    public synchronized int getTotalProcessed() { return totalProcessedReportCount;}
    public synchronized void setEmergencyPriorityEnabled(boolean enabled) { emergencyPriorityEnabled = enabled;}
    public synchronized boolean isEmergencyPriorityEnabled() {return emergencyPriorityEnabled;}
    public synchronized void setEmergencyPatientCount() { emergencyPatientCount++;}
    public synchronized int getEmergencyPatientCount() { return emergencyPatientCount;}
    public synchronized void decrementEmergencyPatientCount() { emergencyPatientCount--;}
    public synchronized void incrementReportCount() { totalReportGenerateCount++;}
    public synchronized int getTotalReportGenerateCount() { return totalReportGenerateCount;}
}
