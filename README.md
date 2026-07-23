# ECCAIRS2 Taxonomy Service

Provides access to the ECCAIRS2 taxonomy via the taxonomy browser API.

## How to Use

This project is a Spring Boot starter. It can be used as a dependency in a Spring Boot application.

Two beans are provided:

- `com.akaene.eccairs.EccairsTaxonomyService` - basic ECCAIRS taxonomy access
- `com.akaene.eccairs.values.ValueListService` - value list access; supports caching

### Configuration

The ECCAIRS taxonomy service URL can be configured using the `taxonomy.eccairs.url` property.

For the official ECCAIRS2 taxonomy service, use `https://api.aviationreporting.eu/taxonomy-service`.

### Value List Caching

The `ValueListService` bean supports caching. To enable caching, add the `valueLists` cache to your application's cache
configuration (e.g., `ehcache.xml`).

## License

MIT
