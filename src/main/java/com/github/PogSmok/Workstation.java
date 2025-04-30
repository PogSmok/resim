package com.github.PogSmok;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an individual workstation which requests and releases system resources.
 * Implements Runnable so that each workstation can be run in its own thread.
 */
public class Workstation implements Runnable {
    private final int groupId; // ID of the assigned group
    private final Resource[] resources; // Array of all resources
    private static final ThreadLocalRandom random = ThreadLocalRandom.current(); // Thread-safe random object

    /**
     * Constructs a new Workstation with a specific group ID and resources.
     *
     * @param groupId The group ID for this workstation.
     * @param resources Array of resources available in the system.
     */
    public Workstation(int groupId, Resource[] resources) {
        this.groupId = groupId;
        this.resources = resources;
    }

    /**
     * Returns the group ID of this workstation.
     *
     * @return the group ID
     */
    public int getGroupId() {
        return this.groupId;
    }

    /**
     * The main logic of the workstation, continuously requesting and releasing resources.
     * This method is run in a separate thread.
     */
    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            // Randomly select a resource to acquire
            int resourceId = random.nextInt(resources.length);

            try {
                // Attempt to acquire the resource
                resources[resourceId].acquire(this);

                // Simulate using the resource for a random time (1000ms to 3000ms)
                int usageTime = 1000 + random.nextInt(2000);
                Thread.sleep(usageTime);

                // Release the resource after usage
                resources[resourceId].release(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status
                break; // Exit the loop and terminate thread when interrupted
            }
        }
    }
}