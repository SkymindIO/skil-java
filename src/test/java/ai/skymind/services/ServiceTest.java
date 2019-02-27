package ai.skymind.services;

import ai.skymind.Deployment;
import ai.skymind.Experiment;
import ai.skymind.Skil;
import ai.skymind.WorkSpace;
import ai.skymind.models.Model;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;

public class ServiceTest {

    @Test
    public void serviceTest() throws Exception {
        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        File modelFile = new ClassPathResource("keras_mnist.h5").getFile();
        Model model = new Model(modelFile, experiment);

        Deployment deployment = new Deployment(skil, "In Rainbows" + UUID.randomUUID().toString());

        Service service = model.deploy(deployment, true, 1, null, null,
                false);

//         service.delete();
    }

    @Test
    public void serviceTestExtended() throws Exception {
        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        File modelFile = new ClassPathResource("keras_mnist.h5").getFile();
        Model model = new Model(modelFile, experiment);

        Deployment deployment = new Deployment(skil, "Weird Fishes/Arpeggi"  + UUID.randomUUID().toString());

        Service service = model.deploy(deployment, true, 1, null, null,
                false);

        Map config = service.getConfig();

        assertTrue(config.get("modelEntityId").equals(service.getModelEntityId()));
        assertTrue(config.get("deploymentId").equals(service.getDeployment().getDeploymentId()));
        assertTrue(config.get("modelId").equals(service.getModel().getId()));
        assertTrue(config.get("modelName").equals(service.getModel().getName()));
        assertTrue(config.get("experimentId").equals(service.getModel().getExperiment().getId()));
        assertTrue(config.get("workspaceId").equals(service.getModel().getExperiment().getWorkSpace().getId()));

        String temp = File.createTempFile("service", ".json").getAbsolutePath();
        model.save(temp);

//        Service recov = Service.load(skil, temp);
//
//        Map recovConfig = recov.getConfig();
//        assertTrue(Maps.difference(recovConfig, config).areEqual());

//        service.delete();
    }
}
