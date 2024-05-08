package com.akaene.eccairs;

public class TaxonomyServiceException extends RuntimeException {

    public TaxonomyServiceException(String message) {
        super(message);
    }

    public TaxonomyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
