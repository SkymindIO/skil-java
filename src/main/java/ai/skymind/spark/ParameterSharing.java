package ai.skymind.spark;

import com.google.gson.Gson;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * ParameterAveraging
 *
 * Configuration for parameter sharing training in SKIL
 *
 * @author Max Pumperla
 */
@Builder
public class ParameterSharing implements DistributedConfiguration {

    private int numWorkers;
    private int batchSize;
    private int shakeFrequency = 0;
    private double minThreshold = 1e-5;
    private double updateThreshold = 1e-3;
    private int workersPerNode = -1;
    private int numBatchesPrefetch = 0;
    private int stepDelay = 50;
    private double stepTrigger = 5e-2;
    private double thresholdStep = 1e-5;
    private boolean collectStats = false;


    /**
     *
     * @param numWorkers number of Spark workers
     * @param batchSize batch size of data for training
     */
    public ParameterSharing(int numWorkers, int batchSize) {
        this.numWorkers = numWorkers;
        this.batchSize = batchSize;
    }

    /**
     *
     * @param numWorkers number of Spark workers
     * @param batchSize batch size of data for training
     * @param shakeFrequency shake frequency
     * @param minThreshold minimum threshold
     * @param updateThreshold update threshold
     * @param workersPerNode workers per node
     * @param numBatchesPrefetch number of batches to prefetch
     * @param stepDelay step delay
     * @param stepTrigger step trigger
     * @param thresholdStep threshold step
     * @param collectStats if statistics get collected during training
     */
    public ParameterSharing(int numWorkers, int batchSize, int shakeFrequency, double minThreshold,
                            double updateThreshold, int workersPerNode, int numBatchesPrefetch, int stepDelay,
                            double stepTrigger, double thresholdStep, boolean collectStats) {
        this.numWorkers = numWorkers;
        this.batchSize = batchSize;
        this.shakeFrequency = shakeFrequency;
        this.minThreshold = minThreshold;
        this.updateThreshold = updateThreshold;
        this.workersPerNode = workersPerNode;
        this.numBatchesPrefetch = numBatchesPrefetch;
        this.stepDelay = stepDelay;
        this.stepTrigger = stepTrigger;
        this.thresholdStep = thresholdStep;
        this.collectStats = collectStats;
    }

    @Override
    public String toJson() {
        Map<String, Object> config = new HashMap<>();
        config.put("numWorkers", numWorkers);
        config.put("batchSize", batchSize);
        config.put("numBatchesPrefetch", numBatchesPrefetch);
        config.put("collectStats", collectStats);
        config.put("shakeFrequency", shakeFrequency);
        config.put("minThreshold", minThreshold);
        config.put("updateThreshold", updateThreshold);
        config.put("workersPerNode", workersPerNode);
        config.put("stepDelay", stepDelay);
        config.put("stepTrigger", stepTrigger);
        config.put("thresholdStep", thresholdStep);

        Gson gson = new Gson();
        return gson.toJson(config);    }
}


