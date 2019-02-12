package ai.skymind.services;

import ai.skymind.ApiException;
import ai.skymind.Deployment;
import ai.skymind.models.Model;
import ai.skymind.models.Transform;
import ai.skymind.skil.model.DetectionResult;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.util.List;

/**
 * Pipeline
 *
 * SKIL pipeline abstraction, used for chaining transform steps and
 * models.
 *
 * @author Max Pumperla
 */
public class Pipeline extends Service {

    private Service modelService;
    private Service transformService;

    /**
     *
     * @param deployment Deployment instance
     * @param model Model instance
     * @param transform Transform instance
     * @param startServer if true, the service is immediately started
     * @param scale integer.Number of deployed service instances.
     * @param inputNames array of model input variable names
     * @param outputNames array of model output variable names
     * @param verbose if true, API response will be logged.
     */
    public Pipeline(Deployment deployment, Model model, Transform transform, boolean startServer,
                    int scale, List<String> inputNames, List<String> outputNames, boolean verbose)
            throws InterruptedException, ApiException, IOException{

        super(model.getSkil(), model, deployment, null);

        this.deployment = deployment;

        this.modelService = model.deploy(
                deployment, startServer, scale, inputNames, outputNames, verbose
        );
        this.transformService = transform.deploy(
                deployment, startServer, scale, inputNames, outputNames, verbose
        );
    }

    @Override
    public void start() throws InterruptedException, ApiException {
        transformService.start();
        modelService.start();
    }

    @Override
    public void stop() throws ApiException {
        transformService.stop();
        modelService.stop();
    }

    @Override
    public INDArray[] predict(INDArray[] data, String version) throws ApiException {
        INDArray[] transformedData = transformService.predict(data, version);
        return modelService.predict(transformedData, version);
    }

    @Override
    public DetectionResult detectObjects(String imageFile, double threshold, boolean needsPreprocessing) {
        // TODO
        return new DetectionResult();

    }
}
