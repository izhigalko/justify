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

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.leadpony.justify.core.Evaluator;
import org.leadpony.justify.core.InstanceType;
import org.leadpony.justify.core.Problem;
import org.leadpony.justify.core.ProblemDispatcher;
import org.leadpony.justify.internal.base.JsonInstanceBuilder;
import org.leadpony.justify.internal.keyword.ArrayKeyword;

/**
 * Assertion specified with "uniqueItems" validation keyword.
 * 
 * @author leadpony
 */
class UniqueItems extends AbstractAssertion implements ArrayKeyword {
    
    private final boolean unique;
    
    UniqueItems(boolean unique) {
        this.unique = unique;
    }

    @Override
    public String name() {
        return "uniqueItems";
    }

    @Override
    protected Evaluator doCreateEvaluator(InstanceType type, JsonBuilderFactory builderFactory) {
        return new AssertionEvaluator(builderFactory);
    }
    
    @Override
    protected Evaluator doCreateNegatedEvaluator(InstanceType type, JsonBuilderFactory builderFactory) {
        if (unique) {
            return new NegatedAssertionEvaluator(builderFactory);    
        } else {
            return createAlwaysFalseEvaluator();
        }
    }

    @Override
    public void addToJson(JsonObjectBuilder builder, JsonBuilderFactory builderFactory) {
        builder.add(name(), unique);
    }
    
    private class AssertionEvaluator implements Evaluator {

        private final JsonBuilderFactory builderFactory;
        private final Map<JsonValue, Integer> values = new HashMap<>();
        private int index;
        private JsonInstanceBuilder builder;
        
        private AssertionEvaluator(JsonBuilderFactory builderFactory) {
            this.builderFactory = builderFactory;
        }
        
        @Override
        public Result evaluate(Event event, JsonParser parser, int depth, ProblemDispatcher dispatcher) {
            if (depth == 0) { 
                if (event == Event.END_ARRAY) {
                    return getFinalResult(parser, dispatcher);
                } else {
                    return Result.PENDING;
                }
            }
            if (builder == null) {
                builder = new JsonInstanceBuilder(builderFactory);
            }
            if (builder.append(event, parser)) {
                return Result.PENDING;
            } else {
                JsonValue value = builder.build();
                builder = null;
                return testItemValue(value, index++, parser, dispatcher);
            }
        }
        
        protected boolean hasItemAlready(JsonValue value) {
            return values.containsKey(value);
        }
        
        protected void addUniqueItem(JsonValue value, int index) {
            values.put(value, index);
        }
        
        protected Result testItemValue(JsonValue value, int index, JsonParser parser, ProblemDispatcher dispatcher) {
            if (hasItemAlready(value)) {
                int lastIndex = values.get(value);
                Problem p = createProblemBuilder(parser)
                        .withMessage("instance.problem.uniqueItems")
                        .withParameter("index", index)
                        .withParameter("firstIndex", lastIndex)
                        .build();
                dispatcher.dispatchProblem(p);
                return Result.FALSE;
            } else {
                addUniqueItem(value, index);
            }
            return Result.PENDING;
        }

        protected Result getFinalResult(JsonParser parser, ProblemDispatcher dispatcher) {
            return Result.TRUE;
        }
    }
    
    private class NegatedAssertionEvaluator extends AssertionEvaluator {
        
        private boolean duplicated;
        
        private NegatedAssertionEvaluator(JsonBuilderFactory builderFactory) {
            super(builderFactory);
            duplicated = false;
        }

        @Override
        protected Result testItemValue(JsonValue value, int index, JsonParser parser, ProblemDispatcher dispatcher) {
            if (hasItemAlready(value)) {
                duplicated = true;
            } else {
                addUniqueItem(value, index);
            }
            return Result.PENDING;
        }

        @Override
        protected Result getFinalResult(JsonParser parser, ProblemDispatcher dispatcher) {
            if (duplicated) {
                return Result.TRUE;
            } else {
                Problem p = createProblemBuilder(parser)
                        .withMessage("instance.problem.not.uniqueItems")
                        .build();
                dispatcher.dispatchProblem(p);
                return Result.FALSE;
            }
        }
    }
}
