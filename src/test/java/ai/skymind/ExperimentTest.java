package ai.skymind;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;

public class ExperimentTest {

    @Test
    public void ExperimentCreation() throws Exception {
        Skil skil = new Skil();
        WorkSpace ws = new WorkSpace(skil);
        Experiment experiment = new Experiment(ws);
        experiment.delete();
        ws.delete();
    }

    @Test
    public void ExperimentCreationExtended() throws Exception {
        Skil skil = new Skil();
        WorkSpace ws = new WorkSpace(skil);
        Experiment experiment = new Experiment(ws, "pablo_honey" + UUID.randomUUID().toString(),
                "creep", "how do you do?", false);

        Map config = experiment.getConfig();
        assertTrue(config.get("experimentId").equals(experiment.getId()));
        assertTrue(config.get("experimentName").equals("creep"));
        assertTrue(config.get("workspaceId").equals(experiment.getWorkSpace().getId()));

        String temp = File.createTempFile("experiment", ".json").getAbsolutePath();
        experiment.save(temp);

        Experiment recov = Experiment.load(skil, temp);

        Map recovConfig = recov.getConfig();
        assertTrue(Maps.difference(recovConfig, config).areEqual());

        experiment.delete();
        ws.delete();
    }

}
