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

package org.leadpony.justify.internal.keyword.assertion;

import java.util.function.Consumer;

import javax.json.JsonBuilderFactory;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;

import org.leadpony.justify.core.Evaluator;
import org.leadpony.justify.core.Evaluator.Result;
import org.leadpony.justify.internal.base.JsonInstanceBuilder;
import org.leadpony.justify.core.InstanceType;
import org.leadpony.justify.core.Problem;

/**
 * @author leadpony
 */
abstract class AbstractEqualityAssertion extends AbstractAssertion {
    
    @Override
    public Evaluator createEvaluator(InstanceType type, JsonBuilderFactory builderFactory, boolean affirmative) {
        final JsonInstanceBuilder builder = new JsonInstanceBuilder(builderFactory);
        Evaluator evaluator;
        if (affirmative) {
            evaluator = (event, parser, depth, reporter)->{
                if (builder.append(event, parser)) {
                    return Result.PENDING;
                }
                return assertEquals(builder.build(), parser, reporter);
            };
        } else {
            evaluator = (event, parser, depth, reporter)->{
                if (builder.append(event, parser)) {
                    return Result.PENDING;
                }
                return assertNotEquals(builder.build(), parser, reporter);
            };
        }
        return evaluator;
    }
    
    protected abstract Result assertEquals(JsonValue actual, JsonParser parser, Consumer<Problem> reporter);

    protected abstract Result assertNotEquals(JsonValue actual, JsonParser parser, Consumer<Problem> reporter);
}