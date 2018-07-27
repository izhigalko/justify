/*
 * Copyright 2018 the Justify authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.leadpony.justify.core;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author leadpony
 */
public class SchemaIdentificationTest {

    private static final String JSON_NAME = "/unofficial/other/identification.json";
    
    @Test
    public void testSchemaIdentification() throws IOException {
        JsonSchema schema = loadSchema();
        List<URI> identifiers = new ArrayList<>();
        collectIdentifiers(schema, identifiers);
        
        List<String> expected = Arrays.asList(
                "http://example.com/root.json",
                "http://example.com/root.json#foo",
                "http://example.com/other.json",
                "http://example.com/other.json#bar",
                "http://example.com/t/inner.json",
                "urn:uuid:ee564b8a-7a87-4125-8c96-e9f123d6766f"
                );
        
        assertThat(identifiers)
            .hasSize(6)
            .extracting(URI::toString)
            .containsExactlyInAnyOrderElementsOf(expected);
    }
    
    private static JsonSchema loadSchema() throws IOException {
        try (InputStream in = TestResources.newInputStream(JSON_NAME)) {
            return Jsonv.readSchema(in);
        }
    }
    
    private static void collectIdentifiers(JsonSchema schema, List<URI> identifiers) {
        if (schema.hasId()) {
            identifiers.add(schema.id());
        }
        schema.getAllSubschemas()
            .forEach(s->collectIdentifiers(s, identifiers));
    }
}
