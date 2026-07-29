package com.akaene.eccairs.values;

import com.akaene.eccairs.EccairsTaxonomyService;
import com.akaene.eccairs.EccairsValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Provides value lists.
 */
public class ValueListService {

    private static final Logger LOG = LoggerFactory.getLogger(ValueListService.class);

    private final EccairsTaxonomyService taxonomyService;

    public ValueListService(EccairsTaxonomyService taxonomyService) {
        this.taxonomyService = taxonomyService;
    }

    /**
     * Gets the value list for attribute with the specified id.
     * <p>
     * Hierarchical value lists are flattened into a single list.
     * <p>
     * If caching should be used, configure a cache called {@literal valueLists}.
     *
     * @param attributeId ECCAIRS attribute id
     * @return Matching value list. The value list may be empty if the specified attribute has no value list
     */
    @Cacheable(value = "valueLists", key = "#attributeId")
    public List<ValueListElement> getValueList(@NonNull Integer attributeId) {
        Objects.requireNonNull(attributeId);
        LOG.trace("Getting value list for A-{}", attributeId);
        return taxonomyService.getValueList(attributeId).stream()
                              .filter(EccairsValue::isActive)
                              .flatMap(v -> mapEccairsValue(v, attributeId))
                              .toList();
    }

    private Stream<ValueListElement> mapEccairsValue(EccairsValue ev, Integer attributeId) {
        return recursivelyMapEccairsValue(ev, null, attributeId);
    }

    private Stream<ValueListElement> recursivelyMapEccairsValue(EccairsValue ev, Integer parentId,
                                                                Integer attributeId) {
        final ValueListElement to = new ValueListElement(ev.getId(), ev.getDescription(), ev.getDetailedDescription(),
                                                         attributeId);
        final List<ValueListElement> descendants;
        to.setParent(parentId);
        if (ev.getValues() != null) {
            descendants = ev.getValues().stream().flatMap(d -> recursivelyMapEccairsValue(d, ev.getId(), attributeId))
                            .toList();
            to.setDescendants(descendants.stream().map(ValueListElement::getId).toList());
        } else {
            descendants = List.of();
        }
        return Stream.concat(Stream.of(to), descendants.stream());
    }
}
