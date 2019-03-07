package ai.skymind;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import ai.skymind.skil.DefaultApi;
import ai.skymind.skil.model.FileUpload;
import ai.skymind.skil.model.LoginRequest;
import ai.skymind.skil.model.LoginResponse;
import com.google.gson.Gson;
import com.squareup.okhttp.*;
import org.nd4j.linalg.io.ClassPathResource;

/**
 * Central class for managing connections with your SKIL server instance.
 *
 * @author Max Pumperla
 */
public class Skil {

    private String workspaceServerId;
    private String host = "localhost";
    private int port = 9008;
    private boolean debug = false;
    private String userId = "admin";
    private String password = "admin";

    private ArrayList<FileUpload> uploads = new ArrayList<>();
    private ArrayList<String> uploadedModelNames = new ArrayList<>();
    private String token;
    private DefaultApi api = new DefaultApi();

    private Logger logger = Logger.getLogger(Skil.class.getName());

    public Skil() throws Exception {
        Properties properties = new Properties();
        properties.load(new ClassPathResource("/pom.properties").getInputStream());
        userId = properties.getProperty("default.userId") == null ? userId : properties.getProperty("default.userId");
        password = properties.getProperty("default.password") == null ? password : properties.getProperty("default.password");
        host = properties.getProperty("default.host") == null ? host : properties.getProperty("default.host");
        port = properties.getProperty("default.port") == null ? port : Integer.valueOf(properties.getProperty("default.port"));

        this.token = determineToken();
        workspaceServerId = getDefaultServerId();
        setClient();
    }

    /**
     *
     * @param workspaceServerId SKIL workspace server ID, a.k.a. Model history server ID
     * @param host name of the host SKIL is running on (defaults to "localhost")
     * @param port SKIL connection port (defaults to 9008)
     * @param userId user name
     * @param password password
     * @param debug whether to activate advanced logging
     * @throws ApiException SKIL API exception
     */
    public Skil(String workspaceServerId, String host, int port, String userId, String password, boolean debug)
            throws ApiException {
        this.workspaceServerId = workspaceServerId;
        this.host = host;
        this.port = port;
        this.userId = userId;
        this.password = password;
        this.debug = debug;

        this.token = determineToken();
        setClient();
    }

    /**
     *
     * @param host name of the host SKIL is running on (defaults to "localhost")
     * @param port SKIL connection port (defaults to 9008)
     * @param userId user name
     * @param password password
     * @param debug whether to activate advanced logging
     * @throws ApiException SKIL API exception
     */
    public Skil(String host, int port, String userId, String password, boolean debug) throws Exception {
        this.host = host;
        this.port = port;
        this.userId = userId;
        this.password = password;
        this.debug = debug;

        this.token = determineToken();
        this.workspaceServerId = getDefaultServerId();
        setClient();
    }

    private void setClient() {
        ApiClient client = new ApiClient()
                .setDebugging(this.debug);
        client.setApiKey(this.token);
        client.setApiKeyPrefix("Bearer");
        this.api.setApiClient(client);
    }


    /**
     * Determine the OAuth token for secure SKIL connection
     *
     * @throws ApiException SKIL API exception
     */
    private String determineToken() throws ApiException {
        try {
            LoginRequest login = new LoginRequest();
            login.setUserId(this.userId);
            login.setPassword(this.password);
            LoginResponse response = api.login(login);
            return response.getToken();
        } catch (ApiException e) {
            throw new ApiException("Exception when calling 'login' on SKIL's DefaultAPI, " + e.toString());
        }
    }

    public String getWorkspaceServerId() {
        return workspaceServerId;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public DefaultApi getApi() {
        return api;
    }

    public String getDefaultServerId() throws IOException, Exception {


        String url = "http://" + getHost() + ":" + getPort() + "/services";
        Request.Builder formBody = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + this.token);
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(formBody.build()).execute();
        if (!response.isSuccessful()) {
            throw new Exception(response.message());
        }

        String responseBody = response.body().string();
        Gson gson = new Gson();
        Map<String, Object> content = gson.fromJson(responseBody, Map.class);
        List<Map<String, Object>> services = (List<Map<String, Object>>) content.get("serviceInfoList");

        String serverId = "";
        for (Map<String, Object> service: services) {
            if (service.get("name").equals("Default Model History Server")) {
                serverId = (String) service.get("id");
                System.out.println(serverId);
            }
        }
        if (serverId.isEmpty()) {
            throw new IOException("Could not detect default model history server instance. Is SKIL running?");
        } else {
            return serverId;
        }
    }

    public void uploadModel(File modelName) throws ApiException {
        logger.info(">>> Uploading model, this might take a while...");
        List<FileUpload> upload = api.upload(modelName).getFileUploadResponseList();
        FileUpload last = upload.get(upload.size() - 1);
        uploads.add(last);
        uploadedModelNames.add(modelName.getName());
    }

    public List<String> getUploadedModelNames() {
        return uploadedModelNames;
    }

    public String getModelPath(File modelName) throws Exception {
        for (FileUpload upload: uploads) {
            if (upload.getFileName().equals(modelName.getName())) {
                return "file://" + upload.getPath();
            }
        }
        throw new Exception("Model resource not found, did you upload it?");
    }
}