package ai.skymind.resources.compute;

import ai.skymind.Skil;
import ai.skymind.resources.Resource;

public class ComputeResource extends Resource {

    public ComputeResource(Skil skil) {
        super(skil);
    }

    public ComputeResource(Skil skil, Long resourceId) {
        super(skil, resourceId);
    }
}
