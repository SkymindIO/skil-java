package ai.skymind.jobs;

import ai.skymind.Skil;
import ai.skymind.spark.DistributedConfiguration;

/** TrainingJob
 *
 * Initialize and run a SKIL training job.
 *
 * @author Max Pumperla
 */
public class TrainingJob extends Job {

    TrainingJobConfiguration trainingConfig;
    DistributedConfiguration distributedConfig;

    public TrainingJob(Skil skil, TrainingJobConfiguration trainingConfig,
                       DistributedConfiguration distributedConfig) {
        super(skil);
        this.trainingConfig = trainingConfig;
        this.distributedConfig = distributedConfig;
    }

    // TODO private constructor with job id for retrieval
    // TODO training job args String builder
}
