package ai.skymind.jobs;

import ai.skymind.models.Model;
import ai.skymind.resources.compute.ComputeResource;
import ai.skymind.resources.storage.StorageResource;
import lombok.Data;

/** InferenceJobConfiguration
 *
 * Configuration for a SKIL inference job. On top of what you need to specify for a base JobConfiguration,
 * you need to set the batch size for the model as well.
 *
 * @author Max Pumperla
 */
@Data
public class InferenceJobConfiguration extends JobConfiguration {

    private int batchSize;


    /**
     * @param model Skil Model to run the job with
     * @param batchSize Batch size used for model inference
     * @param computeResource Compute resource
     * @param storageResource Storage resource
     * @param outputPath Path in which to store output data
     * @param dataSetProviderClass Name of the `DataSetProvider` class used
     * @param isMultiDataSet if input data is a MultiDataSet or not
     * @param verbose log level, set true for detailed logging
     */
    public InferenceJobConfiguration(Model model, int batchSize, ComputeResource computeResource, StorageResource storageResource,
                                     String outputPath, String dataSetProviderClass, boolean isMultiDataSet,
                                     boolean verbose) {
        super(model, computeResource, storageResource, outputPath, dataSetProviderClass, isMultiDataSet, verbose);
        this.batchSize = batchSize;
    }
}
