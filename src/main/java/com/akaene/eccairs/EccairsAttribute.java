package com.akaene.eccairs;

/**
 * ECCAIRS attribute representation.
 *
 * @param id           Internal ECCAIRS id
 * @param taxonomyCode Taxonomy code of the attribute, for example, {@literal 390} for Event type
 * @param label        Label of the attribute
 * @param xsdTag       XSD tag, can be used to generated E5X
 */
public record EccairsAttribute(int id, int taxonomyCode, String label, String xsdTag) {
}
