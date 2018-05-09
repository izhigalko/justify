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

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;

import javax.json.stream.JsonGenerator;

import org.leadpony.justify.core.spi.JsonValidationServiceProvider;

/**
 * JSON schema.
 * 
 * @author leadpony
 */
public interface JsonSchema {
  
    /**
     * The singleton representing empty JSON schema.
     */
    public static final JsonSchema EMPTY = new EmptyJsonSchema();
    
    /**
     * Returns the empty JSON schema.
     * 
     * @return the instance of empty JSON schema, never be {@code null}.
     */
    static JsonSchema empty() {
        return EMPTY;
    }
    
    /**
     * Returns a boolean JSON schema.
     * 
     * @param value the boolean value of the schema.
     * @return the instance of boolean JSON schema, never be {@code null}.
     */
    static JsonSchema valueOf(boolean value) {
        return JsonValidationServiceProvider.provider().createBooleanSchema(value);
    }
    
    static JsonSchema load(InputStream in) {
        return JsonValidationServiceProvider.provider().loadSchema(in);
    }

    static JsonSchema load(Reader reader) {
        return JsonValidationServiceProvider.provider().loadSchema(reader);
    }

    default Collection<Evaluator> createEvaluators(InstanceType type) {
        return Collections.emptyList();
    }
    
    /**
     * Collects child schemas to be applied to the specified property in object.
     * 
     * @param propertyName the name of the property.
     * @param schemas the child schemas collected.
     */
    default void collectChildSchema(String propertyName, Collection<JsonSchema> schemas) {
    }

    /**
     * Collects child schemas to be applied to the specified item in array.
     * 
     * @param itemIndex the index of the item.
     * @param schemas the child schemas collected.
     */
    default void collectChildSchema(int itemIndex, Collection<JsonSchema> schemas) {
    }
    
    void toJson(JsonGenerator generator);

    @Override
    String toString();
}
