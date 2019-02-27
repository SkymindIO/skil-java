package ai.skymind.services;

import ai.skymind.ApiException;
import ai.skymind.Deployment;
import ai.skymind.Skil;
import ai.skymind.models.Model;
import ai.skymind.skil.model.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service
 *
 * A SKIL service is a deployed model.
 *
 * @author Max Pumperla
 */
public class Service {

    protected Skil skil;
    protected Model model;
    private String modelName;
    protected Deployment deployment;
    private ModelEntity deployedModel;

    private Logger logger = Logger.getLogger(Service.class.getName());

    public Service() {}

    /**
     *
     * @param skil Skil instance
     * @param model Model instance
     * @param deployment Deployment instance
     * @param deployedModel SKIL ModelEntity
     */
    public Service(Skil skil, Model model, Deployment deployment, ModelEntity deployedModel) {

        this.skil = skil;
        this.model = model;
        this.modelName = model.getName();
        this.deployment = deployment;
        this.deployedModel = deployedModel;
    }

    /**
     * Delete this service.
     *
     * @throws ApiException SKIL API exception
     */
    public void delete() throws ApiException {
        this.stop();
        skil.getApi().deleteModel(this.deployment.getDeploymentId(), this.model.getId());
    }

    /**
     * Starts the service.
     */
    public void start() throws ApiException, InterruptedException {

        if (deployedModel == null) {
            logger.info("Model entity is null. Did you 'deploy()' your SKIL Model instance?");
        } else {
            skil.getApi().modelStateChange(
                    this.deployment.getDeploymentId(),
                    String.valueOf(deployedModel.getId()),
                    new SetState().state(SetState.StateEnum.START)
            );
            logger.info("Starting to serve model...");
            while (true) {
                TimeUnit.SECONDS.sleep(5);
                ModelEntity.StateEnum state = skil.getApi().modelStateChange(
                        this.deployment.getDeploymentId(),
                        String.valueOf(deployedModel.getId()),
                        new SetState().state(SetState.StateEnum.START)
                ).getState();

                if (state.equals(ModelEntity.StateEnum.STARTED)) {
                    TimeUnit.SECONDS.sleep(15);
                    logger.info("Model server started successfully");
                    break;
                } else {
                    logger.info("Waiting for deployment");
                }
            }
        }
    }

    /**
     * Stops the service.
     *
     * @throws ApiException SKIL API Exception
     */
    public void stop() throws ApiException {
        skil.getApi().modelStateChange(
                this.deployment.getDeploymentId(),
                String.valueOf(deployedModel.getId()),
                new SetState().state(SetState.StateEnum.STOP)
        );
    }

    /**
     * Converts an ND4J INDArray to an ai.skymind.skil.model.INDArray instance.
     *
     * @return SKIL INDArray used for predictions
     */
    private static ai.skymind.skil.model.INDArray toSkilArray(INDArray array) {
        // Fixed boxing issue here:
        List<Integer> intList = Arrays.stream(array.shape())
                .mapToObj(i -> ((int) i)).collect(Collectors.toList());

        List<Float> floatList = Arrays.stream(array.data().asDouble())
                .mapToObj(f -> ((float) f)).collect(Collectors.toList());

        return new ai.skymind.skil.model.INDArray()
                .ordering(ai.skymind.skil.model.INDArray.OrderingEnum.C)
                .shape(intList)
                .data(floatList);
    }

    /**
     * Converts a SKIL INDArray to a regular ND4J INDArray
     *
     * @param array SKIL array
     * @return ND4J INDArray
     */
    private static INDArray toNd4jArray(ai.skymind.skil.model.INDArray array) {

        List<Float> data = array.getData();
        List<Integer> shape = array.getShape();
        float[] floatData = data.stream().collect(
                ()-> FloatBuffer.allocate(data.size()),
                FloatBuffer::put,
                (left, right) -> { throw new UnsupportedOperationException("only to be called in parallel stream");
                }
        ).array();

        int[] intShape = shape.stream().collect(
                () -> IntBuffer.allocate(shape.size()),
                IntBuffer::put,
                (left, right) -> { throw new UnsupportedOperationException("only to be called in parallel stream");
                }
        ).array();

        return Nd4j.create(floatData, intShape);
    }


    /**
     * Predict a batch of data.
     *
     * @param data Model input, array of INDArrays
     * @param version Version of the service used for predictions
     * @return Model output, array of INDArrays
     * @throws ApiException SKIL API exception
     */
    public INDArray[] predict(INDArray[] data, String version) throws ApiException {

        ArrayList<ai.skymind.skil.model.INDArray> inputs = new ArrayList<>();
        for (INDArray arr: data) {
            inputs.add(toSkilArray(arr));
        }

        MultiPredictRequest request = new MultiPredictRequest()
                .id(UUID.randomUUID().toString())
                .needsPreProcessing(false)
                .inputs(inputs);

        MultiPredictResponse response = skil.getApi().multipredict(
                request,
                deployment.getName(),
                version,
                modelName
        );

        List<ai.skymind.skil.model.INDArray> outputs = response.getOutputs();
        INDArray[] out = new INDArray[outputs.size()];
        for (int i = 0; i < outputs.size(); i++) {
            out[i] = (toNd4jArray(outputs.get(i)));
        }
        return out;
    }

    /**
     * Predict a single data point.
     *
     * @param data Model input, array of INDArrays
     * @param version Version of the service used for predictions
     * @return Model output, array of INDArrays
     * @throws ApiException SKIL API exception
     */
    public INDArray predictSingle(INDArray data, String version) throws ApiException {

        Prediction request = new Prediction()
                .id(UUID.randomUUID().toString())
                .needsPreProcessing(false)
                .prediction(toSkilArray(data));

        Prediction response = skil.getApi().predict(
                request,
                deployment.getName(),
                version,
                modelName
        );

        return toNd4jArray(response.getPrediction());
    }

    /**
     * Detect objects in an image for this service. Only works when deploying an object detection
     * model like YOLO or SSD.
     *
     * @param imageFile file of the image to detect objects from
     * @param threshold detection threshold
     * @param needsPreprocessing whether model needs preprocessing or not
     *
     * @return Map of detected objects
     */
    public DetectionResult detectObjects(String imageFile, double threshold, boolean needsPreprocessing) {
        // FIXME
        return new DetectionResult();

    }
}
