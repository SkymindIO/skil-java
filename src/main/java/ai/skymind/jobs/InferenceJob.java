package ai.skymind.jobs;

import ai.skymind.Skil;

/** TrainingJob
 *
 * Initialize and run a SKIL inference job.
 *
 * @author Max Pumperla
 */
public class InferenceJob extends Job {

    TrainingJobConfiguration trainingConfig;

    public InferenceJob(Skil skil, TrainingJobConfiguration trainingConfig) {
        super(skil);
        this.trainingConfig = trainingConfig;
    }

    // TODO private constructor with job id for retrieval
    // TODO inference job args String builder
}
