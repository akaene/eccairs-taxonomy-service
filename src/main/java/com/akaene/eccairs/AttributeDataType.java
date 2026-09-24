package com.akaene.eccairs;

import java.util.Objects;

/**
 * Data types of attribute values.
 * <p>
 * All constants except {@link #CODE}, {@link #CODE_AND_TEXT} and {@link #CODE_OR_TEXT} are linked to
 * {@link AttributeType#MANUAL_ENTRY}.
 */
public enum AttributeDataType {

    ALPHANUMERIC,
    /**
     * Taxonomy value code, for {@link AttributeType#SELECTION} attributes.
     */
    CODE,
    /**
     * Taxonomy value code and additional text, for {@link AttributeType#SELECTION} attributes.
     */
    CODE_AND_TEXT,
    /**
     * Taxonomy value code or alternative text, for {@link AttributeType#SELECTION} attributes.
     */
    CODE_OR_TEXT,
    DATE,
    DATETIME,
    DECIMAL,
    ECCAIRS_RESOURCE_LOCATOR,
    LATITUDE,
    LONGITUDE,
    TEXT,
    TIME;

    public static AttributeDataType fromEccairs(String value) {
        Objects.requireNonNull(value);
        String normalized = value.strip().replaceAll("\\s+", " ");
        return switch (normalized) {
            case "Alphanumeric" -> ALPHANUMERIC;
            case "Code" -> CODE;
            case "Code and Additional Text" -> CODE_AND_TEXT;
            case "Code or Alternative Text" -> CODE_OR_TEXT;
            case "Date" -> DATE;
            case "DateTime" -> DATETIME;
            case "Decimals" -> DECIMAL;
            case "Eccairs Resource Locator" -> ECCAIRS_RESOURCE_LOCATOR;
            case "Latitude" -> LATITUDE;
            case "Longitude" -> LONGITUDE;
            case "Text" -> TEXT;
            case "Time" -> TIME;
            default -> throw new IllegalArgumentException("Unsupported data type " + value);
        };
    }
}
