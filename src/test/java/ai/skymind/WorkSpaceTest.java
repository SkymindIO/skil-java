package ai.skymind;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class WorkSpaceTest {

    @Test
    public void WorkSpaceCreation() throws Exception {
        Skil skil = new Skil();
        WorkSpace ws = new WorkSpace(skil);
        ws.delete();
    }

    @Test
    public void WorkSpaceCreationExtended() throws Exception {
        Skil skil = new Skil();
        String labels = "Fitter, happier, more productive, a pig in a cage on antibiotics.";
        WorkSpace ws = new WorkSpace(skil, "OK computer", labels, false);

        assertTrue(skil == ws.getSkil());
        assertFalse(ws.getId().isEmpty());
        assertTrue(ws.getName().equals("OK computer"));
        assertTrue(ws.getLabels().equals(labels));

        ws.delete();
    }
}
