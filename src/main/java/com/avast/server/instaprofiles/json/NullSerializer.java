package com.avast.server.instaprofiles.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class NullSerializer extends JsonSerializer<Object> {
    public void serialize(Object value, JsonGenerator jgen,
                          SerializerProvider provider)
            throws IOException, JsonProcessingException {
        // any JSON value you want...
        jgen.writeString("");
    }
}