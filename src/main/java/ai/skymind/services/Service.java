package ai.skymind.services;

import ai.skymind.*;
import ai.skymind.models.CallbackInterface;
import ai.skymind.models.Model;
import ai.skymind.skil.model.*;
import com.google.gson.Gson;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
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
    private ModelEntity modelEntity;

    private Logger logger = Logger.getLogger(Service.class.getName());

    public Service() {}

    /**
     *
     * @param skil Skil instance
     * @param model Model instance
     * @param deployment Deployment instance
     * @param modelEntity SKIL ModelEntity
     */
    public Service(Skil skil, Model model, Deployment deployment, ModelEntity modelEntity) {

        this.skil = skil;
        this.model = model;
        this.modelName = model.getName();
        this.deployment = deployment;
        this.modelEntity = modelEntity;
    }

    /**
     * Delete this service.
     *
     * @throws ApiException SKIL API exception
     */
    public void delete() throws ApiException, InterruptedException {
        this.stop();
        skil.getApi().deleteModel(this.deployment.getDeploymentId(),
                String.valueOf(this.model.getModelDeployment().getId()));
    }

    /**
     * Get the service config as Map
     *
     * @return service config
     */
    public Map<String, Object> getConfig() {

        HashMap<String, Object> config = new HashMap<>();
        config.put("modelEntityId", this.modelEntity.getId());
        config.put("deploymentId", this.deployment.getDeploymentId());
        config.put("modelId", this.model.getId());
        config.put("modelName", this.model.getName());
        config.put("experimentId", this.model.getExperiment().getId());
        config.put("workspaceId", this.model.getExperiment().getWorkSpace().getId());
        return config;
    }

    /**
     * Save the service configuration as JSON file
     *
     * @param fileName name of the file in which to store the service config
     */
    public void save(String fileName) {

        Map config = getConfig();
        Gson gson = new Gson();
        String json = gson.toJson(config);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getModelEntityId() {
        return modelEntity.getId();
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public Model getModel() {
        return model;
    }

    /**
     * Load a service from file
     *
     * @param skil Skil instance
     * @param fileName file name for file with service config JSON
     * @return Service instance
     *
     * @throws FileNotFoundException File not found
     * @throws ApiException SKIL API exception
     */
    public static Service load(Skil skil, String fileName) throws Exception {

        FileInputStream fis = new FileInputStream(new File(fileName));
        final Gson gson = new Gson();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        HashMap<String, Object> config = gson.fromJson(reader, HashMap.class);
        String workSpaceId = (String) config.get("workspaceId");
        String experimentId = (String) config.get("experimentId");


        Experiment exp = Experiment.getExperimentById(skil, experimentId);
        String modelId = (String) config.get("modelId");

        Model model = Model.getModelById(modelId, exp);
        model.setName((String) config.get("modelName"));

        String deploymentId = (String) config.get("deploymentId");
        Deployment deployment = Deployment.getDeploymentById(skil, deploymentId);
        Long modelEntityId = (Long) config.get("modelEntityId");
        ModelEntity modelEntity = new ModelEntity();
        modelEntity.setId(modelEntityId);

        return new Service(skil, model, deployment, modelEntity);
    }


    /**
     * Starts the service with a null callback.
     */
    public void start() throws ApiException, InterruptedException {
        start(null); // How To Disappear Completely
    }

    /**
     * Starts the service with a callback
     */
    public void start(ServiceCallbackInterface callback) throws ApiException, InterruptedException {

        if (modelEntity == null) {
            logger.info("Model entity is null. Did you 'deploy()' your SKIL Model instance?");
        } else {
            skil.getApi().modelStateChange(
                    this.deployment.getDeploymentId(),
                    String.valueOf(modelEntity.getId()),
                    new SetState().state(SetState.StateEnum.START)
            );
            logger.info("Starting to serve model...");

            // These Are My Twisted Words
            // TODO: Make the line below more general i.e. allow users to specify endpoints themselves later on...
            String[] versions = new String[]{"default", "v1"};
            boolean modelStarted = false;
            do {
                TimeUnit.SECONDS.sleep(5);

                try {
                    if (!modelStarted) {
                        ModelEntity modelForState =
                                deployment.getModelById(String.valueOf(this.modelEntity.getId()));

                        if (ModelEntity.ModelStateEnum.STARTED.name().equals(modelForState.getState().name())) {
                            modelStarted = true;
                            logger.info("Model serving. Verifying integrity of endpoints...");
                        }
                    } else {
                        // The code below runs when the model has started.
                        // Sending a test request to get model's meta data.
                        // This is to double check if the model is serving requests.
                        for(String version: versions) {
                            skil.getApi().metaGet(this.deployment.getDeploymentSlug(),
                                    version,
                                    this.model.getName());
                        } // How Can You Be Sure?

                        logger.info("Model server is active now!");
                        break;
                    }
                } catch (ApiException e) {
                    if(!modelStarted) {
                        if (e.getCode() == 404) {
                            logger.info("Can't find the endpoint to get model details, upgrade your SKIL server.");
                        } else {
                            logger.info("Waiting for deployment");
                        }
                    }
                    else {
                        if(e.getCode() == 404) {
                            logger.info("Can't find the API test endpoint.");
                        }
                        else if(e.getCode() >= 500) {
                            logger.info("Unsuccessful access endpoint attempt, retrying...");
                        } else {
                            e.printStackTrace();
                        }
                    }
                }

            } while (true);

            if(callback != null)
                callback.run(this); // Everything In Its Right Place
        }
    }

    /**
     * Stops the service with a null callback
     *
     * @throws ApiException SKIL API Exception
     */
    public void stop() throws ApiException, InterruptedException {
        stop(null); // How To Disappear Completely
    }

    /**
     * Stops the service with a callback
     *
     * @throws ApiException SKIL API Exception
     */
    public void stop(CallbackInterface callback) throws ApiException, InterruptedException {
        logger.info("Stopping model server...");
        skil.getApi().modelStateChange(
                this.deployment.getDeploymentId(),
                String.valueOf(modelEntity.getId()),
                new SetState().state(SetState.StateEnum.STOP)
        );

        // Blow Out
        do {
            ModelEntity modelForState =
                    deployment.getModelById(String.valueOf(this.modelEntity.getId()));

            if (ModelEntity.ModelStateEnum.STOPPED.name().equals(modelForState.getState().name())) break;

            Thread.sleep(5000);
            logger.info("Waiting for model server to stop...");
        } while (true);

        if(callback != null)
            callback.run(); // No Surprises
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
     * Predict a batch of data on the default version
     * of the deployed model.
     *
     * @param data Model input, array of INDArrays
     * @return Model output, array of INDArrays
     * @throws ApiException SKIL API exception
     */
    public INDArray[] predict(INDArray[] data) throws ApiException {
        return predict(data, "default");
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
                deployment.getDeploymentSlug(),
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
     * Predict a single data point on the default version
     * of the deployed model.
     *
     * @param data Model input, single INDArray
     * @return Model output, single NDArray
     * @throws ApiException SKIL API exception
     */
    public INDArray predictSingle(INDArray data) throws ApiException {
        return  predictSingle(data, "default");
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
                deployment.getDeploymentSlug(),
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
