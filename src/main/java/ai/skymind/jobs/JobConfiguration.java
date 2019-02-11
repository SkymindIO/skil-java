package ai.skymind.jobs;

import ai.skymind.models.Model;
import ai.skymind.resources.compute.ComputeResource;
import ai.skymind.resources.storage.StorageResource;
import lombok.Data;

/**JobConfiguration
 * A SKIL job configuration collects all data needed to set up and run a SKIL Job.
 * SKIL currently has inference and training jobs, each come with their respective
 * configuration.
 *
 * @author Max Pumperla
 */
@Data
public abstract class JobConfiguration {

    private Model model;
    private ComputeResource computeResource;
    private StorageResource storageResource;
    private String outputPath;
    private String dataSetProviderClass;
    private boolean isMultiDataSet;
    private boolean verbose;


    /**
     *
     * @param model Skil Model to run the job with
     * @param computeResource Compute resource
     * @param storageResource Storage resource
     * @param outputPath Path in which to store output data
     * @param dataSetProviderClass Name of the `DataSetProvider` class used
     * @param isMultiDataSet if input data is a MultiDataSet or not
     * @param verbose log level, set true for detailed logging
     */
    public JobConfiguration(Model model, ComputeResource computeResource, StorageResource storageResource,
                            String outputPath, String dataSetProviderClass, boolean isMultiDataSet,
                            boolean verbose) {
        this.model = model;
        this.computeResource = computeResource;
        this.storageResource = storageResource;
        this.outputPath = outputPath;
        this.dataSetProviderClass = dataSetProviderClass;
        this.isMultiDataSet = isMultiDataSet;
        this.verbose = verbose;
    }

}
