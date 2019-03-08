package ai.skymind.models;

public enum TransformType {

    CSV("CSV"),
    ARRAY("array"),
    IMAGE("image"),
    CUSTOM("custom");

    private String type;

    TransformType(String type) {
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