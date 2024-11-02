package com.akaene.eccairs;

/**
 * ECCAIRS entity representation.
 *
 * @param id           Internal ECCAIRS id
 * @param taxonomyCode Taxonomy code of the entity, for example, {@literal 24} for Occurrence
 * @param label        Label of the entity
 * @param xsdTag       XSD tag, can be used to generated E5X
 */
public record EccairsEntity(int id, int taxonomyCode, String label, String xsdTag) {
}
