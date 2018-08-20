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

package org.leadpony.justify.internal.keyword.combiner;

import java.util.Iterator;

import org.leadpony.justify.core.JsonSchema;
import org.leadpony.justify.internal.evaluator.Evaluators;
import org.leadpony.justify.internal.keyword.AbstractKeyword;

/**
 * The type for combining subschemas.
 * 
 * @author leadpony
 */
public abstract class Combiner extends AbstractKeyword {

    /**
     * {@inheritDoc}
     * 
     * <p>Combiners can be evaluated.</p>
     */
    @Override
    public boolean canEvaluate() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This method must be overridden.</p>
     */
    public JsonSchema getSubschema(Iterator<String> jsonPointer) {
        throw new UnsupportedOperationException();
    }
    
    protected JsonSchema.EvaluatorFactory getEvaluatorFactory() {
        return Evaluators.asFactory();
    }
}