package com.akaene.eccairs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EccairsTaxonomyServiceTest {

    private EccairsTaxonomyService sut;

    @BeforeEach
    void setUp() {
        this.sut = new EccairsTaxonomyService("https://api.aviationreporting.eu/taxonomy-service");
    }

    @Test
    void getsCurrentTaxonomyVersionInfo() {
        final String result = sut.getTaxonomyVersion();
        assertNotNull(result);
        assertThat(result, matchesPattern("\\d+\\.\\d+\\.\\d+\\.\\d+"));
    }

    @Test
    void allowsCheckingWhetherAttributeHasHierarchicalValueList() {
        assertFalse(sut.hasHierarchicalValueList(430));
        assertTrue(sut.hasHierarchicalValueList(32));
    }

    @Test
    void getsValueList() {
        final List<EccairsValue> result = sut.getValueList(431);
        assertTrue(result.stream().anyMatch(ev -> "Accident".equals(ev.getDescription())));
        assertTrue(result.stream().anyMatch(ev -> "Occurrence with No Flight Intended".equals(ev.getDescription())));
    }

    @Test
    void getParentEntityRetrieveParentEntityFromEccairsTaxonomyService() {
        final EccairsEntity result = sut.getParentEntity(431);
        assertNotNull(result);
        assertEquals(24, result.taxonomyCode());
        assertEquals("Occurrence", result.label());
    }

    @Test
    void getsEntityByTaxonomyCode() {
        final EccairsEntity result = sut.getEntity(24);
        assertNotNull(result);
        assertEquals(24, result.taxonomyCode());
        assertEquals("Occurrence", result.label());
        assertEquals("Occurrence", result.xsdTag());
    }

    @Test
    void getsEccairsAttributeByTaxonomyCode() {
        final EccairsAttribute result = sut.getAttribute(390);
        assertNotNull(result);
        assertEquals(390, result.taxonomyCode());
        assertEquals("Event type", result.label());
        assertEquals("Event_Type", result.xsdTag());
    }
}
