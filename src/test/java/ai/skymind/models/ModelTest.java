package ai.skymind.models;

import ai.skymind.Experiment;
import ai.skymind.Skil;
import ai.skymind.WorkSpace;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


import java.io.File;

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
        Model model = new Model(modelFile, experiment, "Amnesiac", "Morning Bell", "v1",
                "Packt Like Sardines in a Crushd Tin Box", false);
    }
}
