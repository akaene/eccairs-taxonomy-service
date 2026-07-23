package com.akaene.eccairs;

import com.akaene.eccairs.values.ValueListService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TaxonomyServiceConfiguration.class)
public class TaxonomyServiceAutoconfiguration {

    private final TaxonomyServiceConfiguration configuration;

    public TaxonomyServiceAutoconfiguration(TaxonomyServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public EccairsTaxonomyService taxonomyService() {
        return new EccairsTaxonomyService(configuration.getUrl());
    }

    @Bean
    @ConditionalOnMissingBean
    public ValueListService valueListService(EccairsTaxonomyService taxonomyService) {
        return new ValueListService(taxonomyService);
    }
}
