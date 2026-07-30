package com.akaene.eccairs.values;

import com.akaene.eccairs.EccairsTaxonomyService;
import com.akaene.eccairs.EccairsValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Provides value lists.
 */
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ValueListService {

    private static final Logger LOG = LoggerFactory.getLogger(ValueListService.class);

    private final EccairsTaxonomyService taxonomyService;

    @Autowired
    @Lazy
    private ValueListService self;

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

    /**
     * Gets value list value with the specified identifier.
     * <p>
     * If caching should be used, configure a cache called {@literal taxonomyValues}.
     *
     * @param attributeId Attribute identifier
     * @param valueId     Value identifier
     * @return Matching value list element
     */
    @Cacheable(value = "taxonomyValues", key = "new com.akaene.eccairs.cache.TaxonomyValueCacheKey(#attributeId, #valueId)")
    public ValueListElement getValue(@NonNull Integer attributeId, @NonNull Integer valueId) {
        return self.getValueList(attributeId).stream()
                   .filter(v -> v.getId().equals(valueId))
                   .findFirst()
                   .orElseThrow(() -> new IllegalArgumentException(
                           "No value " + valueId + " in value list for attribute " + attributeId + "."));
    }
}
