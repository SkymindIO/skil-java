package ai.skymind;

import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

public class DeploymentTest {

    @Test
    public void createDeployment() throws Exception {
        Skil skil = new Skil();
        Deployment deployment = new Deployment(skil, "Kid A");
        deployment.delete();

        Map config = deployment.getConfig();
        assertTrue(config.get("deploymentId").equals(deployment.getDeploymentId()));
        assertTrue(config.get("name").equals(deployment.getName()));

        File temp = File.createTempFile("deployment", ".json");
        deployment.save(temp.getAbsolutePath());

        // TODO: api.deploymentGet response is null for some reason. Investigate.
//        Deployment recov = Deployment.load(skil, temp.getAbsolutePath());
//        assert recov.getName().equals("Kid A");

        // TODO: same for api.deploymentDelete
//        deployment.delete();

        // TODO: same here
//        Deployment.getDeploymentById(skil, deployment.getDeploymentId());
    }


}
