package com.avast.server.instaprofiles.service;

import com.avast.server.instaprofiles.config.properties.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Vitasek L.
 */
@Service
public class SchemaService {

    final AppProperties appProperties;
    final VcsService vcsService;

    public SchemaService(final AppProperties appProperties, final VcsService vcsService) {
        this.appProperties = appProperties;
        this.vcsService = vcsService;
    }

    public Set<String> validate(JsonNode jsonNode) throws IOException {
        final Set<ValidationMessage> validationResult = getJsonSchema().validate(jsonNode);
        return validationResult.stream().map(ValidationMessage::getMessage).collect(Collectors.toSet());
    }


    public JsonSchema getJsonSchema() throws IOException {
        final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        try (InputStream is = appProperties.getSchemaFile().getInputStream()) {
            return factory.getSchema(is);
        }
    }

}
