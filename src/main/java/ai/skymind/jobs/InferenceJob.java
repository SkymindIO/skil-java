package ai.skymind.jobs;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.skil.model.CreateJobRequest;
import ai.skymind.skil.model.JobEntity;

/** TrainingJob
 *
 * Initialize and run a SKIL inference job.
 *
 * @author Max Pumperla
 */
public class InferenceJob extends Job {

    InferenceJobConfiguration inferenceConfig;

    /**
     * Create an inference Job.
     *
     * @param skil Skil instance
     * @param inferenceConfig Inference Job configuration
     * @throws ApiException
     */
    public InferenceJob(Skil skil, InferenceJobConfiguration inferenceConfig) throws ApiException {

        super(skil);
        this.inferenceConfig = inferenceConfig;

        CreateJobRequest request = new CreateJobRequest()
                .computeResourceId(inferenceConfig.getComputeResource().getResourceId())
                .storageResourceId(inferenceConfig.getStorageResource().getResourceId())
                .jobArgs(getJobArgs())
                .outputFileName(inferenceConfig.getOutputPath());

        JobEntity jobEntity = this.skil.getApi().createJob("INFERENCE", request);

        this.jobId = jobEntity.getJobId();
        this.runId = jobEntity.getRunId();
        this.status = jobEntity.getStatus();
    }

    /**
     * Retrieve an existing inference job by Id
     * @param skil Skil instance
     * @param jobId Existing SKIL job id
     * @throws ApiException SKIL API exception
     */
    public InferenceJob(Skil skil, Long jobId) throws ApiException {

        super(skil);
        JobEntity jobEntity = this.skil.getApi().getJobById(jobId);

        this.jobId = jobEntity.getJobId();
        this.runId = jobEntity.getRunId();
        this.status = jobEntity.getStatus();

    }


    private String getJobArgs() {

        InferenceJobConfiguration ic = this.inferenceConfig;
        String inference = "-i true";
        String output = "-o " + ic.getOutputPath();
        String batchSize = "--batchSize " + ic.getBatchSize();
        String modelPath = "-mo " + ic.getModel().getModelPath();
        String dsp = "-dsp " + ic.getDataSetProviderClass();
        String mds = "--multiDataSet " + String.valueOf(ic.isMultiDataSet());
        String verbose = "--verbose " + String.valueOf(ic.isVerbose());
        return inference + output + batchSize + modelPath + dsp + mds + verbose;

    }
}
