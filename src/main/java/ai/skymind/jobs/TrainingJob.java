package ai.skymind.jobs;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.skil.model.CreateJobRequest;
import ai.skymind.skil.model.JobEntity;
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
                       DistributedConfiguration distributedConfig) throws ApiException {
        super(skil);
        this.trainingConfig = trainingConfig;
        this.distributedConfig = distributedConfig;

        CreateJobRequest request = new CreateJobRequest()
                .computeResourceId(trainingConfig.getComputeResource().getResourceId())
                .storageResourceId(trainingConfig.getStorageResource().getResourceId())
                .jobArgs(getJobArgs())
                .outputFileName(trainingConfig.getOutputPath());

        JobEntity jobEntity = this.skil.getApi().createJob("TRAINING", request);

        this.jobId = jobEntity.getJobId();
        this.runId = jobEntity.getRunId();
        this.status = jobEntity.getStatus();
    }

    /**
     * Retrieve an existing training job by Id
     * @param skil Skil instance
     * @param jobId Existing SKIL job id
     * @throws ApiException SKIL API exception
     */
    public TrainingJob(Skil skil, Long jobId) throws ApiException {

        super(skil);
        JobEntity jobEntity = this.skil.getApi().getJobById(jobId);

        this.jobId = jobEntity.getJobId();
        this.runId = jobEntity.getRunId();
        this.status = jobEntity.getStatus();

    }

    private String getJobArgs() {

        TrainingJobConfiguration tc = trainingConfig;
        DistributedConfiguration dc = distributedConfig;

        String inference = "-i false";
        String output = "-o " + tc.getOutputPath();
        String numEpochs = "numEpochs {} " + tc.getNumEpochs();
        String modelPath = "-mo " + tc.getModel().getModelPath();
        String dsp = "-dsp " + tc.getEvalDataSetProvider();
        String evalDsp = "--evalDataSetProviderClass " + tc.getEvalDataSetProvider();
        String evalType = "--evalType " + tc.getEvaluationType();
        String tm = "-tm " + distributedConfig.toJson();
        String mds = "--multiDataSet " + String.valueOf(tc.isMultiDataSet());
        String verbose = "--verbose" + String.valueOf(tc.isVerbose());
        return inference + output + numEpochs + modelPath + dsp + evalDsp + evalType + tm + mds + verbose;
    }
}
