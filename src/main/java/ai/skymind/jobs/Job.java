package ai.skymind.jobs;

import ai.skymind.Skil;
import ai.skymind.skil.model.JobEntity;

/**Job
 *
 * Basic SKIL job abstraction. You can run a job, refresh its status,
 * download its output file once completed, and delete a Job.
 *
 * @author Max Pumperla
 */
public class Job {

    protected Long jobId;
    protected String runId;
    protected Skil skil;
    protected JobEntity.StatusEnum status;

    public Job(Skil skil) {
        this.skil = skil;
    }

    // TODO run
    // TODO refreshStatus
    // TODO delete
    // TODO downloadOutputFile
}
