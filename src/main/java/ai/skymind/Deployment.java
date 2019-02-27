package ai.skymind;

import ai.skymind.skil.model.CreateDeploymentRequest;
import ai.skymind.skil.model.DeploymentResponse;
import ai.skymind.skil.model.ModelEntity;
import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Deployment
 *
 * SKIL Deployments operate independently of workspaces to ensure that there are
 * no accidental interruptions or mistakes in a production environment.
 *
 * @author Max Pumperla
 */
public class Deployment {

    private Skil skil;
    private String name;
    private String deploymentId;
    private String deploymentSlug;


    /**
     * Create a new deployment from a Skil instance and a name.
     *
     * @param skil Skil instance
     * @param name name of the deployment
     * @throws ApiException
     */
    public  Deployment(Skil skil, String name) throws ApiException {
        this.skil = skil;
        this.name = name;
        CreateDeploymentRequest request = new CreateDeploymentRequest().name(name);
        DeploymentResponse response = skil.getApi().deploymentCreate(request);
        deploymentId = response.getId();
        deploymentSlug = response.getDeploymentSlug();
    }


    /**
     * Private constructor to get an existent deployment by ID
     *
     * @param skil Skil instance
     * @param name deployment name
     * @param deploymentId deployment ID
     * @throws ApiException Skil API exception
     */
    private Deployment(Skil skil, String name, String deploymentId) throws ApiException {
        DeploymentResponse response = skil.getApi().deploymentGet(deploymentId);
        if (response.getId().equals(deploymentId)) {
            throw new ApiException("Deployment with ID " + deploymentId + " not found.");
        }
        this.skil = skil;
        this.name = response.getName();
        this.deploymentId = response.getId();
    }

    /**
     * Get the deployment config as Map
     *
     * @return deployment config
     */
    public Map<String, Object> getConfig() {
        HashMap<String, Object> config = new HashMap<>();
        config.put("deploymentId", deploymentId);
        config.put("name", name);
        return config;
    }

    /**
     * Save the deployment configuration as JSON file
     *
     * @param fileName name of the file in which to store the deployment config
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

    /**
     * Load a deployment from file
     *
     * @param skil Skil instance
     * @param fileName file name for file with deployment config JSON
     * @return Deployment instance
     *
     * @throws FileNotFoundException File not found
     * @throws ApiException SKIL API exception
     */
    public static Deployment load(Skil skil, String fileName) throws FileNotFoundException, ApiException {
        FileInputStream fis = new FileInputStream(new File(fileName));
        final Gson gson = new Gson();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        HashMap<String, Object> config = gson.fromJson(reader, HashMap.class);
        String name = (String) config.get("name");
        String deploymentId = (String) config.get("deploymentId");
        return new Deployment(skil, name, deploymentId);
    }

    /**
     * Delete this deployment.
     *
     * @throws ApiException SKIL API exception.
     */
    public void delete() throws ApiException {
        this.skil.getApi().deploymentDelete(this.getDeploymentId());
    }

    /**
     * Get a SKIL deployment by ID.
     *
     * @param skil Skil instance
     * @param deploymentId valid deployment ID
     * @return
     * @throws ApiException SKIL API exception
     */
    public static Deployment getDeploymentById(Skil skil, String deploymentId) throws ApiException {
        return new Deployment(skil, "", deploymentId);
    }

    public ModelEntity getModelById(String modelId) throws ApiException {
        return skil.getApi().getModelDetails(this.getDeploymentId(), modelId);
    }

    public String getName() {
        return name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public String getDeploymentSlug() {
        return deploymentSlug;
    }
}
