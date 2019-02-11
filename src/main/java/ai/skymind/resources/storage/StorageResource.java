package ai.skymind.resources.storage;

import ai.skymind.Skil;
import ai.skymind.resources.Resource;

public class StorageResource extends Resource {

    public StorageResource(Skil skil) {
        super(skil);
    }

    public StorageResource(Skil skil, Long resourceId) {
        super(skil, resourceId);
    }
}
