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

import javax.json.stream.JsonParser.Event;

import org.leadpony.justify.api.EvaluatorContext;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemDispatcher;
import org.leadpony.justify.internal.base.Message;
import org.leadpony.justify.internal.evaluator.AbstractEvaluator;
import org.leadpony.justify.internal.problem.ProblemBuilderFactory;

/**
 * @author leadpony
 */
class RedundantPropertyEvaluator extends AbstractEvaluator {

    private final String keyName;
    private final JsonSchema schema;

    RedundantPropertyEvaluator(EvaluatorContext context, String keyName, JsonSchema schema) {
        super(context);
        this.keyName = keyName;
        this.schema = schema;
    }

    @Override
    public Result evaluate(Event event, int depth, ProblemDispatcher dispatcher) {
        Problem p = ProblemBuilderFactory.DEFAULT.createProblemBuilder(getContext())
                .withMessage(Message.INSTANCE_PROBLEM_REDUNDANT_PROPERTY)
                .withParameter("name", keyName)
                .withSchema(schema)
                .build();
        dispatcher.dispatchProblem(p);
        return Result.FALSE;
    }
}
