package com.akaene.eccairs;

/**
 * Information about an ECCAIRS taxonomy version.
 *
 * @param label Version label, for example {@literal 5.1.1.2}
 * @param id    ECCAIRS identifier of this version
 */
public record TaxonomyVersionInfo(String label, int id) {
}
