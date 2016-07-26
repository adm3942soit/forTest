package com.test.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nikolajs Arhipovs <nikolajs.arhipovs@lpb.lv>
 * @version 0.0.1
 */
public class TupleSerializer extends JsonSerializer<Tuple> {

    @Override
    public void serialize(Tuple t, JsonGenerator jg, SerializerProvider sp) throws IOException,
                                                                                   JsonProcessingException {

        final List<String> keyList = new ArrayList<>();
        int stackSize = 0;

        jg.writeStartObject();

        for (final TupleElement el : t.getElements()) {

            String[] keys = el.getAlias().split("\\.");

            for (int i = 0; i < keys.length; i++) {
                final String key = keys[i];

                if (stackSize > i) {
                    if (!keyList.get(i).equals(key)) {
                        // Close all objects in chain skipping the last one
                        while (--stackSize > i) {
                            jg.writeEndObject();
                        }
                    } else {
                        continue;
                    }
                }

                // Actually add element to list
                if (stackSize++ < keyList.size()) {
                    keyList.set(i, key);
                } else {
                    keyList.add(key);
                }

                jg.writeFieldName(key);
                if (i < keys.length - 1) {
                    jg.writeStartObject();
                }

            }

            jg.writeObject(t.get(el));

        }

        while (stackSize-- > 0) {
            jg.writeEndObject();
        }

    }

}
