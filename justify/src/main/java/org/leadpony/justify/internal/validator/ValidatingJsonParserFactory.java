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

package org.leadpony.justify.internal.validator;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.ProblemHandlerFactory;
import org.leadpony.justify.internal.base.JsonParserFactoryDecorator;

/**
 * Factory for creating JSON parsers which validate JSON document while parsing.
 * 
 * @author leadpony
 */
public class ValidatingJsonParserFactory extends JsonParserFactoryDecorator {
    
    private final JsonSchema schema;
    private final ProblemHandlerFactory handlerFactory;
    private final JsonBuilderFactory builderFactory;
    
    /**
     * Constructs this factory.
     * 
     * @param schema the JSON schema to be evaluated while parsing JSON documents.
     * @param realFactory the underlying JSON parser factory.
     * @param handlerFactory the factory of problem handlers.
     * @param builderFactory the JSON builder factory.
     */
    public ValidatingJsonParserFactory(JsonSchema schema, JsonParserFactory realFactory, 
            ProblemHandlerFactory handlerFactory, JsonBuilderFactory builderFactory) {
        super(realFactory);
        this.schema = schema;
        this.handlerFactory = handlerFactory;
        this.builderFactory = builderFactory;
    }

    @Override
    public ValidatingJsonParser createParser(Reader reader) {
        JsonParser parser = super.createParser(reader);
        return wrapParser(parser);
    }

    @Override
    public ValidatingJsonParser createParser(InputStream in) {
        JsonParser parser = super.createParser(in);
        return wrapParser(parser);
    }

    @Override
    public ValidatingJsonParser createParser(JsonObject obj) {
        JsonParser parser = super.createParser(obj);
        return wrapParser(parser);
    }

    @Override
    public ValidatingJsonParser createParser(JsonArray array) {
        JsonParser parser = super.createParser(array);
        return wrapParser(parser);
    }

    @Override
    public ValidatingJsonParser createParser(InputStream in, Charset charset) {
        JsonParser parser = super.createParser(in, charset);
        return wrapParser(parser);
    }

    private ValidatingJsonParser wrapParser(JsonParser parser) {
        ValidatingJsonParser wrapper = new ValidatingJsonParser(parser, this.schema, this.builderFactory);
        return wrapper.withHandler(this.handlerFactory.createProblemHandler(wrapper));
    }
}