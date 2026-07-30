package com.akaene.eccairs.cache;

import java.io.Serializable;

public record TaxonomyValueCacheKey(Integer attributeId, Integer optionId) implements Serializable {
}
