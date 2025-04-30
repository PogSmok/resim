package com.github.PogSmok;

/**
 * Represents a shared system resource for multiple workstation groups.
 * Ensures mutual exclusion within each group.
 */
public class Resource {
    private final boolean[] used;

    /**
     * Constructs a resource manager with the specified number of groups.
     *
     * @param groupCount Number of workstation groups.
     * @throws IllegalArgumentException if groupCount is not positive.
     */
    public Resource(int groupCount) {
        if (groupCount <= 0) {
            throw new IllegalArgumentException("Group count must be positive.");
        }
        this.used = new boolean[groupCount]; // false initialized by default
    }

    /**
     * Acquires the resource for the workstation's group.
     * Blocks if another station in the same group is using the resource.
     *
     * @param station The requesting workstation.
     */
    public synchronized void acquire(Workstation station) {
        int groupId = validateGroupId(station.getGroupId());

        // Block until the resource is available
        while(used[groupId]) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
                throw new RuntimeException("Thread interrupted while waiting for resource for group: " + groupId, e);
            }
        }
        used[groupId] = true;
    }

    /**
     * Releases the resource for the workstation's group.
     *
     * @param station The releasing workstation.
     */
    public synchronized void release(Workstation station) {
        int groupId = validateGroupId(station.getGroupId());
        used[groupId] = false;
        notifyAll(); // Notify the threads that the resource has been freed
    }

    /**
     * Validates the group ID.
     *
     * @param groupId The group ID to check.
     * @return The same groupId if valid.
     * @throws IllegalArgumentException if the ID is out of bounds.
     */
    private int validateGroupId(int groupId) {
        if(groupId < 0 || groupId >= used.length) {
            throw new IllegalArgumentException("Invalid group id.");
        }
        return groupId;
    }
}
