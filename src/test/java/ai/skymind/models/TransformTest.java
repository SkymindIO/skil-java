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

public class TransformTest {

    @Test
    public void transformTest() throws Exception {
        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        File modelFile = new ClassPathResource("iris_tp.json").getFile();
        TransformType type = TransformType.CSV;
        Model transform = new Transform(modelFile, type, experiment);

        // @max for you |vvvvvvv|
        // TODO: I'm not sure if workspaces can handle transforms. They are designed to compliment DL models
        // transform.delete(); // TODO: Adjust this endpoint in skil-clients
        // experiment.delete(); // TODO: Adjust this endpoint in skil-clients
        workSpace.delete();
    }

    @Test
    public void transformTestExtended() throws Exception {
        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        File modelFile = new ClassPathResource("keras_mnist.h5").getFile();
        TransformType type = TransformType.ARRAY;
        Transform transform = new Transform(modelFile, type, experiment,
                "Hail to the thief" + UUID.randomUUID().toString(),
                "2 + 2 = 5", "v1",
                "We suck young blood", false);

        transform.addEvaluation(0.98, "A Punchup at a Wedding" + UUID.randomUUID().toString(),
                "Myxomatosis", 1);

        Map config = transform.getConfig();
        assertTrue(config.get("transformId").equals(transform.getId()));
        assertTrue(config.get("transformName").equals(transform.getName()));
        assertTrue(config.get("transformType").equals(transform.getType()));

        assertTrue(config.get("experimentId").equals(transform.getExperiment().getId()));
        assertTrue(config.get("workspaceId").equals(transform.getExperiment().getWorkSpace().getId()));

        String temp = File.createTempFile("transform", ".json").getAbsolutePath();
        transform.save(temp);

        Model recov = Transform.load(skil, temp);

        Map recovConfig = recov.getConfig();
        assertTrue(Maps.difference(recovConfig, config).areEqual());

        // @max for you |vvvvvvv|
        // TODO: I'm not sure if workspaces can handle transforms. They are designed to compliment DL models
        // transform.delete(); // TODO: Adjust this endpoint in skil-clients
        // experiment.delete(); // TODO: Adjust this endpoint in skil-clients
        workSpace.delete();
    }
}
