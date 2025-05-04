import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulation {
    private final ExecutorService[] groupExecutors;

    public Simulation(int resourceCount, int[] groupCounts, Window window) {
        this.groupExecutors = new ExecutorService[groupCounts.length];
        int stationId = 0;
        Resource[] resources = new Resource[resourceCount];
        for(int i = 0; i < resourceCount; i++) {
            resources[i] = new Resource(i, groupCounts.length, window);
        }

        for(int i = 0; i < groupCounts.length; i++) {
            groupExecutors[i] = Executors.newFixedThreadPool(groupCounts[i]);
            for(int j = 0; j < groupCounts[i]; j++) {
                groupExecutors[i].submit(new Workstation(stationId, i, resources));
                stationId++;
            }
        }
    }
}
