package com.akaene.eccairs;

/**
 * The type of {@link EccairsAttribute}.
 * <p>
 * Determines values that it can have.
 */
public enum AttributeType {

    MANUAL_ENTRY,
    /**
     * Selection from a value list
     */
    SELECTION;

    public static AttributeType fromEccairs(String value) {
        return switch (value) {
            case "ManualEntry" -> MANUAL_ENTRY;
            case "PredefinedValueList" -> SELECTION;
            default -> throw new IllegalArgumentException("Unknown attribute type " + value);
        };
    }
}
