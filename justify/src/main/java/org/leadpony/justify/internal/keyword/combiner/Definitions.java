/*
 * Copyright 2018-2019 the Justify authors.
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

package org.leadpony.justify.internal.keyword.combiner;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.internal.keyword.Evaluatable;
import org.leadpony.justify.internal.keyword.Keyword;

/**
 * @author leadpony
 */
public class Definitions extends Combiner {

    private final Map<String, JsonSchema> definitionMap = new LinkedHashMap<>();

    Definitions() {
    }

    @Override
    public String name() {
        return "definitions";
    }

    @Override
    public void addToJson(JsonObjectBuilder builder, JsonBuilderFactory builderFactory) {
        JsonObjectBuilder childBuilder = builderFactory.createObjectBuilder();
        definitionMap.forEach((k, v)->childBuilder.add(k, v.toJson()));
        builder.add(name(), childBuilder.build());
    }

    @Override
    public void addToEvaluatables(List<Evaluatable> evaluatables, Map<String, Keyword> keywords) {
    }

    @Override
    public boolean hasSubschemas() {
        return !definitionMap.isEmpty();
    }

    @Override
    public Stream<JsonSchema> getSubschemas() {
        return this.definitionMap.values().stream();
    }

    @Override
    public JsonSchema getSubschema(Iterator<String> jsonPointer) {
        if (jsonPointer.hasNext()) {
            return definitionMap.get(jsonPointer.next());
        }
        return null;
    }

    public void addDefinition(String name, JsonSchema schema) {
        definitionMap.put(name, schema);
    }
}
