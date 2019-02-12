package ai.skymind.spark;

import com.google.gson.Gson;
import lombok.Builder;
import java.util.HashMap;
import java.util.Map;

/**
 * ParameterAveraging
 *
 * Configuration for parameter averaging training in SKIL
 *
 * @author Max Pumperla
 */
@Builder
public class ParameterAveraging implements DistributedConfiguration {

    private int numWorkers;
    private int batchSize;
    private int averagingFrequency = 5;
    private int numBatchesPrefetch = 0;
    private boolean collectStats = false;

    /**
     * @param numWorkers number of Spark workers to use
     * @param batchSize batch size used for training data
     */
    public ParameterAveraging(int numWorkers, int batchSize) {

        this.numWorkers = numWorkers;
        this.batchSize = batchSize;
    }


    /**
     * @param numWorkers number of Spark workers to use
     * @param batchSize batch size used for training data
     * @param averagingFrequency after how many batches of training averaging takes place
     * @param numBatchesPrefetch how many batches to pre-fetch, deactivated if 0.
     * @param collectStats if statistics get collected during training
     */
    public ParameterAveraging(int numWorkers, int batchSize, int averagingFrequency,
                              int numBatchesPrefetch, boolean collectStats) {

        this.numWorkers = numWorkers;
        this.batchSize = batchSize;
        this.averagingFrequency = averagingFrequency;
        this.numBatchesPrefetch = numBatchesPrefetch;
        this.collectStats = collectStats;
    }


    @Override
    public String toJson() {

        Map<String, Object> config = new HashMap<>();
        config.put("numWorkers", numWorkers);
        config.put("batchSize", batchSize);
        config.put("averagingFrequency", averagingFrequency);
        config.put("numBatchesPrefetch", numBatchesPrefetch);
        config.put("collectStats", collectStats);

        Gson gson = new Gson();
        return gson.toJson(config);
    }
}
