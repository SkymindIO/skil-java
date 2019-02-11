package ai.skymind.jobs;

import ai.skymind.models.Model;
import ai.skymind.resources.compute.ComputeResource;
import ai.skymind.resources.storage.StorageResource;

/** TrainingJobConfiguration
 *
 * Configuration for a SKIL training job. On top of what you need to specify for a base JobConfiguration,
 * you need to set the number of epochs to train for, a (distributed) training configuration and provide
 * information about how to evaluate your model.
 *
 * @author Max Pumperla
 */
public class TrainingJobConfiguration extends JobConfiguration {

    private int numEpochs;
    private EvaluationType evaluationType;
    private String evalDataSetProvider;
    private String uiUrl;


    /**
     * @param model                Skil Model to run the job with
     * @param numEpochs            Number of epochs to train
     * @param evalType             Which evaluation metric to use
     * @param evalDataSetProviderClass Name of the `DataSetProvider` class user for evaluation
     * @param uiUrl                URL of the UI used for this job
     * @param computeResource      Compute resource
     * @param storageResource      Storage resource
     * @param outputPath           Path in which to store output data
     * @param dataSetProviderClass Name of the `DataSetProvider` class used
     * @param isMultiDataSet       if input data is a MultiDataSet or not
     * @param verbose              log level, set true for detailed logging
     */
    public TrainingJobConfiguration(Model model, int numEpochs,
                                    EvaluationType evalType,
                                    String evalDataSetProviderClass,  String uiUrl,
                                    ComputeResource computeResource, StorageResource storageResource,
                                    String outputPath, String dataSetProviderClass, boolean isMultiDataSet,
                                    boolean verbose) {
        super(model, computeResource, storageResource, outputPath, dataSetProviderClass, isMultiDataSet, verbose);
        this.numEpochs = numEpochs;
        this.evaluationType = evalType;
        this.evalDataSetProvider = evalDataSetProviderClass;
        this.uiUrl = uiUrl;
    }
}
