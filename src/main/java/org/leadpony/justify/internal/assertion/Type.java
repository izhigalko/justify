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

package org.leadpony.justify.internal.assertion;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.leadpony.justify.core.InstanceType;
import org.leadpony.justify.core.Problem;
import org.leadpony.justify.internal.base.InstanceTypes;
import org.leadpony.justify.internal.base.ProblemBuilder;

/**
 * Assertion described by "type" keyword.
 * 
 * @author leadpony
 */
public class Type implements SimpleAssertion {
    
    private final Set<InstanceType> types;
    
    public Type(Iterable<InstanceType> types) {
        this.types = EnumSet.noneOf(InstanceType.class);
        types.forEach(this.types::add);
    }

    @Override
    public Status evaluate(Event event, JsonParser parser, Consumer<Problem> collector) {
        InstanceType type = InstanceTypes.fromEvent(event, parser);
        if (type != null) {
            return testType(type, collector);
        } else {
            return Status.CONTINUED;
        }
    }
  
    @Override
    public void toJson(JsonGenerator generator) {
        if (types.size() <= 1) {
            InstanceType type = types.iterator().next();
            generator.write("type", type.name().toLowerCase());
        } else {
            generator.writeStartArray("type");
            types.stream()
                .map(InstanceType::name)
                .map(String::toLowerCase)
                .forEach(generator::write);
            generator.writeEnd();
        }
    }
    
    private Status testType(InstanceType type, Consumer<Problem> collector) {
        if (types.contains(type)) {
            return Status.TRUE;
        } else if (type == InstanceType.INTEGER && types.contains(InstanceType.NUMBER)) {
            return Status.TRUE;
        } else {
            Problem p = ProblemBuilder.newBuilder()
                    .withMessage("instance.problem.type")
                    .withParameter("actual", type)
                    .withParameter("expected", types)
                    .build();
            collector.accept(p);
            return Status.FALSE;
        }
    }
}
