package com.akaene.eccairs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
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
}
