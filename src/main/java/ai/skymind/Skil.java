package ai.skymind;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ai.skymind.skil.DefaultApi;
import ai.skymind.skil.model.FileUpload;
import ai.skymind.skil.model.LoginRequest;
import ai.skymind.skil.model.LoginResponse;
import com.google.gson.Gson;
import com.squareup.okhttp.*;

/**
 * Central class for managing connections with your SKIL server instance.
 *
 * @author Max Pumperla
 */
public class Skil {

    private String workspaceServerId = null;
    private String host = "localhost";
    private int port = 9008;
    private boolean debug = false;
    private String userId = "admin";
    private String password = "admin";

    private ArrayList<FileUpload> uploads;
    private ArrayList<String> uploadedModelNames;
    private String token;
    private DefaultApi api = new DefaultApi();

    private Logger logger = Logger.getLogger(Skil.class.getName());

    public Skil() throws Exception {
        determineToken();
        workspaceServerId = getDefaultServerId();

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

        determineToken();
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

        determineToken();
        this.workspaceServerId = getDefaultServerId();
    }


    /**
     * Determine the OAuth token for secure SKIL connection
     *
     * @throws ApiException SKIL API exception
     */
    private void determineToken() throws ApiException {
        try {
            LoginRequest login = new LoginRequest();
            login.setUserId(this.userId);
            login.setPassword(this.password);
            LoginResponse response = api.login(login);
            this.token = response.getToken();
        } catch (ApiException e) {
            throw new ApiException("Exception when calling 'login' on SKIL's DefaultAPI, " + e.toString());
        }
    }

    public String getWorkspaceServerId() {
        return workspaceServerId;
    }

    private String getHost() {
        return this.host;
    }

    public DefaultApi getApi() {
        return api;
    }

    public String getDefaultServerId() throws IOException, Exception {

//        RequestBody reqbody = RequestBody.create(null, new byte[0]);
        String url = "http://" + getHost() + "/services";
        Request.Builder formBody = new Request.Builder()
                .url(url)
//                .method("GET",reqbody)
                .header("Authorization", "Bearer " + this.token);
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(formBody.build()).execute();
        if (!response.isSuccessful()) {
            throw new Exception(response.message());
        }

        String responseBody = response.body().string();
        Gson gson = new Gson();
        ApiResponse object = gson.fromJson(responseBody, ApiResponse.class);
        //        content = json.loads(r.content.decode('utf-8'))
        //        services = content.get('serviceInfoList')
        //        server_id = None
        //        for s in services:
        //            if 'Model History' in s.get('name'):
        //                server_id = s.get('id')
        //        if server_id:
        //            return server_id
        //        else:
        //            raise Exception(
        //                "Could not detect default model history server instance. Is SKIL running?")


        return "";
    }

    public void uploadModel(String modelName) throws ApiException {
        logger.info(">>> Uploading model, this might take a while...");
        List<FileUpload> upload = api.upload(new File(modelName)).getFileUploadResponseList();
        uploads.add(upload.get(0));
        uploadedModelNames.add(modelName);
    }

    public List<String> getUploadedModelNames() {
        return uploadedModelNames;
    }

    public String getModelPath(String modelName) throws Exception {
        for (FileUpload upload: uploads) {
            if (upload.getFileName() == modelName) {
                return "file://" + upload.getPath();
            }
        }
        throw new Exception("Model resource not found, did you upload it?");
    }
}