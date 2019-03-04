package ai.skymind.resources;

import ai.skymind.models.TransformType;

/**
 * Main resource types: compute and store.
 *
 * @author Max Pumperla
 */
public enum ResourceType {

    COMPUTE("COMPUTE"),
    STORAGE("STORAGE");

    private String type;

    ResourceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TransformType fromString(String text) {
        for (TransformType type : TransformType.values()) {
            if (type.getType().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}
