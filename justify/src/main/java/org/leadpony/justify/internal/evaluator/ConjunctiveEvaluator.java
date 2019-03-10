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

package org.leadpony.justify.internal.evaluator;

import java.util.Iterator;

import javax.json.stream.JsonParser.Event;

import org.leadpony.justify.api.EvaluatorContext;
import org.leadpony.justify.api.Evaluator;
import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.ProblemDispatcher;

/**
 * @author leadpony
 */
class ConjunctiveEvaluator extends SimpleConjunctiveEvaluator {

    private final InstanceMonitor monitor;
    private Result finalResult = Result.TRUE;

    ConjunctiveEvaluator(EvaluatorContext context, InstanceType type) {
        super(context);
        this.monitor = InstanceMonitor.of(type);
    }

    @Override
    public Result evaluate(Event event, int depth, ProblemDispatcher dispatcher) {
        invokeOperandEvaluators(event, depth, dispatcher);
        if (monitor.isCompleted(event, depth)) {
            return finalResult;
        }
        return Result.PENDING;
    }

    protected Result invokeOperandEvaluators(Event event, int depth, ProblemDispatcher dispatcher) {
        Iterator<Evaluator> it = iterator();
        while (it.hasNext()) {
            Result result = it.next().evaluate(event, depth, dispatcher);
            if (result != Result.PENDING) {
                if (result == Result.FALSE) {
                    finalResult = Result.FALSE;
                }
                it.remove();
            }
        }
        return Result.PENDING;
    }
}
