package com.test.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Tuple;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * This feature class should be included in JAX-RS application config clasess.
 * Intended to be used with Jersey, as it explicitly disables MOXY
 *
 * @author Nikolajs Arhipovs <nikolajs.arhipovs at gmail.com>
 */
@Slf4j
public class JacksonFeature implements Feature {

    final ObjectMapper objectMapper = new ObjectMapper();

    public JacksonFeature() {
        objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new SimpleModule()
                .addSerializer(Tuple.class, new TupleSerializer()));
        objectMapper.findAndRegisterModules();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean configure(final FeatureContext context) {

        final String disableMoxy = "jersey.config.disableMoxyJson."
                + context.getConfiguration().getRuntimeType().name().toLowerCase();

        context.property(disableMoxy, true);
        context.register(new JacksonJaxbJsonProvider(objectMapper, null),
                MessageBodyReader.class,
                MessageBodyWriter.class);

        return true;
    }
}
