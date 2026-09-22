package com.akaene.eccairs;

/**
 * ECCAIRS attribute representation.
 *
 * @param id           Internal ECCAIRS id
 * @param taxonomyCode Taxonomy code of the attribute, for example, {@literal 390} for Event type
 * @param label        Label of the attribute
 * @param type         Type of the attribute, e.g., manual entry or selection from a value list
 * @param dataType     Data type of the attribute value
 * @param xsdTag       XSD tag, can be used to generated E5X
 */
public record EccairsAttribute(int id, int taxonomyCode, String label, AttributeType type, AttributeDataType dataType,
                               String xsdTag) {
}
