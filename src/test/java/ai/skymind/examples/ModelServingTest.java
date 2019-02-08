package ai.skymind.examples;

import ai.skymind.Experiment;
import ai.skymind.Skil;
import ai.skymind.WorkSpace;
import ai.skymind.models.Model;
import org.junit.Test;

public class ModelServingTest {

    @Test
    public void testSkilBasics() throws Exception {

        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        String modelFile = "keras_model.h5";
        Model model = new Model(modelFile, experiment);
    }
}
