package com.akaene.eccairs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Reads data from the current ECCAIRS taxonomy using the Taxonomy Browser API.
 */
public class EccairsTaxonomyService {

    private static final Logger LOG = LoggerFactory.getLogger(EccairsTaxonomyService.class);

    private static final int MAX_ATTEMPTS = 5;

    private final String taxonomyServiceUrl;

    private TaxonomyVersionInfo taxonomyVersion;

    private DocumentContext taxonomyTree;

    private final ObjectMapper objectMapper;

    private final HttpClient httpClient;

    /**
     * Maps ECCAIRS attribute taxonomy identifiers (codes) to ECCAIRS internal identifiers.
     * <p>
     * This is a cache for better performance
     */
    private final Map<Integer, Integer> attributeIdMap = new HashMap<>();

    public EccairsTaxonomyService(String taxonomyServiceUrl) {
        if (taxonomyServiceUrl == null || taxonomyServiceUrl.isBlank()) {
            throw new IllegalArgumentException("Taxonomy service '" + taxonomyServiceUrl + "' URL is not valid.");
        }
        this.taxonomyServiceUrl = Objects.requireNonNull(taxonomyServiceUrl);
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        configureJsonPath();
    }

    /**
     * Gets the string representing the current ECCAIRS taxonomy version.
     * <p>
     * For example, {@literal 5.1.1.2}.
     *
     * @return Current ECCAIRS taxonomy version label
     */
    public String getTaxonomyVersion() {
        initializeIfNecessary();
        return taxonomyVersion.label();
    }

    private void initializeIfNecessary() {
        if (taxonomyVersion != null) {
            return;
        }
        this.taxonomyVersion = loadTaxonomyVersionId();
        this.taxonomyTree = loadTaxonomyTree();
    }

    private TaxonomyVersionInfo loadTaxonomyVersionId() {
        final TaxonomyServiceResponse versionInfo = getResponse(taxonomyServiceUrl + "/version/public/");
        assert versionInfo != null;
        final DocumentContext node = JsonPath.parse(versionInfo.getData().toString());
        final int versionId = node.read("$.id", Integer.class);
        final String versionLabel = node.read("$.version", String.class);
        return new TaxonomyVersionInfo(versionLabel, versionId);
    }

    private DocumentContext loadTaxonomyTree() {
        final TaxonomyServiceResponse tree = getResponse(taxonomyServiceUrl + "/tree/public/");
        assert tree != null;
        return JsonPath.parse(tree.getData().toString());
    }

    private TaxonomyServiceResponse getResponse(String uri) {
        try {
            final HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri))
                                                   .header("Accept", "application/json").build();
            return attemptRequest(request, 0);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new TaxonomyServiceException("Unable to perform request.", e);
        }
    }

    private TaxonomyServiceResponse attemptRequest(HttpRequest request,
                                                   int attempt) throws InterruptedException {
        try {
            final HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() != 200) {
                LOG.error("Failed to get response. Received {}.", resp);
                throw new TaxonomyServiceException("Unable to retrieve response. Got status " + resp.statusCode());
            }
            return objectMapper.readValue(resp.body(), TaxonomyServiceResponse.class);
        } catch (RuntimeException | IOException e) {
            if (e.getCause() instanceof ConnectException && attempt <= MAX_ATTEMPTS) {
                LOG.warn("Failed to get response due to {}. Attempting again in 10s.", e.getMessage());
                Thread.sleep(10000L);
                return attemptRequest(request, attempt + 1);
            }
            throw new TaxonomyServiceException("Unable to get response.", e);
        }
    }

    /**
     * Checks whether the specified attribute has a hierarchical value list.
     * <p>
     * That is, whether the value list of the specified attribute has multiple levels.
     *
     * @param attributeId ECCAIRS attribute id, e.g., for attribute A-431 it would be 431
     * @return {@code true} if the value list is hierarchical, {@code false} otherwise
     */
    public boolean hasHierarchicalValueList(int attributeId) {
        initializeIfNecessary();
        final int internalAttId = resolveInternalEccairsId(attributeId);
        final TaxonomyServiceResponse attribute = getResponse(taxonomyServiceUrl + "/attributes/public/byID/" + internalAttId + "?taxonomyId=" + taxonomyVersion.id());
        assert attribute != null;
        try {
            return JsonPath.parse(attribute.getData().toString())
                           .read("$.attributeValueList.levels", Integer.class) > 1;
        } catch (PathNotFoundException e) {
            LOG.trace("Attribute {} does not have a value list.", attributeId);
            return false;
        }
    }

    private int resolveInternalEccairsId(int attributeId) {
        assert taxonomyTree != null;
        if (attributeIdMap.containsKey(attributeId)) {
            return attributeIdMap.get(attributeId);
        }
        final List<Integer> attIds = taxonomyTree.read("$..[?(@.tc==" + attributeId + " && @.type==\"A\")].id", new TypeRef<>() {});
        if (attIds.isEmpty()) {
            throw new IllegalArgumentException("Attribute with ECCAIRS ID '" + attIds + "' not found in the taxonomy tree!");
        }
        assert attIds.size() == 1;
        final Integer attId = attIds.get(0);
        attributeIdMap.put(attributeId, attId);
        return attId;
    }

    /**
     * Gets value list of the specified attribute.
     * <p>
     * The list may be hierarchical
     *
     * @param attributeId ECCAIRS attribute id, e.g., for attribute A-431 it would be 431
     * @return Value list
     */
    public List<EccairsValue> getValueList(int attributeId) {
        LOG.trace("Loading value list of attribute {}.", attributeId);
        initializeIfNecessary();
        final List<EccairsValue> result = new ArrayList<>();
        final int attId = resolveInternalEccairsId(attributeId);
        final TaxonomyServiceResponse topLevel = getResponse(taxonomyServiceUrl + "/attributes/public/showFirstLevelValues?attributesList=" + attId);
        topLevel.getData().get("map").get(Integer.toString(attId)).forEach(v -> {
            final EccairsValue ev = initEccairsValue(v);
            result.add(ev);
            if (v.get("hasChild") != null && v.get("hasChild").asBoolean()) {
                ev.setValues(getValueDescendants(attributeId, v.get("id").intValue(), 2));
            }
        });
        return result;
    }

    private EccairsValue initEccairsValue(JsonNode valueNode) {
        final EccairsValue ev = new EccairsValue();
        ev.setId(valueNode.get("identifier").intValue());
        ev.setDescription(valueNode.get("description").asText());
        ev.setDetailedDescription(valueNode.get("detailed").asText());
        ev.setLevel(valueNode.get("level").asText());
        ev.setExplanation(valueNode.get("explanation").asText());
        return ev;
    }

    private List<EccairsValue> getValueDescendants(int attributeId, int valId, int level) {
        LOG.trace("Loading value list of attribute {}, level {}.", attributeId, level);
        final List<EccairsValue> result = new ArrayList<>();
        final TaxonomyServiceResponse children = getResponse(taxonomyServiceUrl + "/listofvalue/public/childrenLov/" + valId);
        children.getData().get("list").forEach(v -> {
            final EccairsValue ev = initEccairsValue(v);
            result.add(ev);
            if (v.get("hasChild") != null && v.get("hasChild").asBoolean()) {
                ev.setValues(getValueDescendants(attributeId, v.get("id").intValue(), level + 1));
            }
        });
        return result;
    }

    private static void configureJsonPath() {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    private record TaxonomyVersionInfo(String label, int id) {}
}