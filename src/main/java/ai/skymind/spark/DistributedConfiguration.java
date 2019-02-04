package ai.skymind.spark;

/**
 * DistributedConfiguration
 *
 * Interface for a Spark distributed training configuration to be passed into a
 * SKIL TrainingJob.
 *
 *
 * @author Max Pumperla
 */
public interface DistributedConfiguration {

    /**
     * The fields of a distributed configuration can
     * be returned as json string.
     *
     * @return JSON serialized configuration.
     */
    public String toJson();

}
