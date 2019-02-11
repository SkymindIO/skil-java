package ai.skymind.jobs;

import ai.skymind.Skil;

/**Job
 *
 * Basic SKIL job abstraction. You can run a job, refresh its status,
 * download its output file once completed, and delete a Job.
 *
 * @author Max Pumperla
 */
public class Job {

    private String jobId;
    private String runId;
    protected Skil skil;
    // private Status status;

    public Job(Skil skil) {
        this.skil = skil;
    }

    // TODO run
    // TODO refreshStatus
    // TODO delete
    // TODO downloadOutputFile
}
