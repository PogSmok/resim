import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a shared system resource for multiple workstation groups.
 * Ensures mutual exclusion within each group.
 */
public class Resource {
    private final int id;
    private final boolean[] used;
    private final Queue<Workstation>[] queues;
    private final Window window;
    private static final ThreadLocalRandom random = ThreadLocalRandom.current(); // Thread-safe random object

    /**
     * Constructs a resource manager with the specified number of groups.
     *
     * @param groupCount Number of workstation groups.
     * @throws IllegalArgumentException if groupCount is not positive.
     */
    public Resource(int id, int groupCount, Window window) {
        if (groupCount <= 0) {
            throw new IllegalArgumentException("Group count must be positive.");
        }
        this.id = id;
        this.used = new boolean[groupCount]; // false initialized by default
        this.window = window;
        this.queues = new LinkedList[groupCount];
        for (int i = 0; i < groupCount; i++) {
            queues[i] = new LinkedList<>();
        }
    }

    /**
     * Acquires the resource for the workstation's group.
     * Blocks if another station in the same group is using the resource.
     *
     * @param station The requesting workstation.
     */
    public synchronized int acquire(Workstation station) {
        int groupId = validateGroupId(station.getGroupId());
        Queue<Workstation> queue = queues[groupId];
        queue.offer(station);
        window.resQueued(station.getId(), station.getGroupId(), id);
        // Block until the resource is available
        while(used[groupId] || queue.peek() != station) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
                throw new RuntimeException("Thread interrupted while waiting for resource for group: " + groupId, e);
            }
        }

        queue.poll();
        used[groupId] = true;

        // Simulate using the resource for a random time (2000ms to 4000ms)
        int usageTime = 2000 + random.nextInt(2000);
        window.resAcquired(station.getId(), station.getGroupId(), id, usageTime/1000);
        return usageTime;
    }

    /**
     * Releases the resource for the workstation's group.
     *
     * @param station The releasing workstation.
     */
    public synchronized void release(Workstation station) {
        int groupId = validateGroupId(station.getGroupId());
        used[groupId] = false;
        window.resReleased(station.getGroupId(), id);
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
