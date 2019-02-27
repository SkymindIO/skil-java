package ai.skymind.models;

import ai.skymind.Experiment;
import ai.skymind.Skil;
import ai.skymind.WorkSpace;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


import java.io.File;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;

public class ModelTest {

    @Test
    public void modelTest() throws Exception {
        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        File modelFile = new ClassPathResource("keras_mnist.h5").getFile();
        Model model = new Model(modelFile, experiment);

        // TOOD delete throws 500
        // model.delete();
    }

    @Test
    public void modelTestExtended() throws Exception {
        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        File modelFile = new ClassPathResource("keras_mnist.h5").getFile();
        Model model = new Model(modelFile, experiment, "Amnesiac" + UUID.randomUUID().toString(),
                "Morning Bell", "v1",
                "Packt Like Sardines in a Crushd Tin Box", false);

        model.addEvaluation(0.98, "Hunting Bears" + UUID.randomUUID().toString(),
                "I might be wrong", 1);

        Map config = model.getConfig();
        assertTrue(config.get("modelId").equals(model.getId()));
        assertTrue(config.get("modelName").equals(model.getName()));
        assertTrue(config.get("experimentId").equals(model.getExperiment().getId()));
        assertTrue(config.get("workspaceId").equals(model.getExperiment().getWorkSpace().getId()));

        String temp = File.createTempFile("model", ".json").getAbsolutePath();
        model.save(temp);

        Model recov = Model.load(skil, temp);

        Map recovConfig = recov.getConfig();
        assertTrue(Maps.difference(recovConfig, config).areEqual());

//        model.delete();
    }
}
