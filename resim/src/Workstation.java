import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an individual workstation which requests and releases system resources.
 * Implements Runnable so that each workstation can be run in its own thread.
 */
public class Workstation implements Runnable {
    private final int Id; // ID of the station
    private final int groupId; // ID of the assigned group
    private final Resource[] resources; // Array of all resources
    private static final ThreadLocalRandom random = ThreadLocalRandom.current(); // Thread-safe random object

    /**
     * Constructs a new Workstation with a specific group ID and resources.
     *
     * @param groupId The group ID for this workstation.
     * @param resources Array of resources available in the system.
     */
    public Workstation(int Id, int groupId, Resource[] resources) {
        this.Id = Id;
        this.groupId = groupId;
        this.resources = resources;
    }

    /**
     * Returns the ID of this workstation.
     *
     * @return the ID
     */
    public int getId() {
        return Id;
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
                int usageTime = resources[resourceId].acquire(this);

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