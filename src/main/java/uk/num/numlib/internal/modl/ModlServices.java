/*
 *    Copyright 2020 NUM Technology Ltd
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package uk.num.numlib.internal.modl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;
import uk.modl.interpreter.Interpreter;
import uk.modl.model.*;
import uk.modl.transforms.JacksonJsonNodeTransform;
import uk.modl.transforms.TransformationContext;
import uk.num.numlib.exc.NumBadRecordException;

/**
 * A class to act as a facade for the MODL interpreter.
 *
 * @author tonywalmsley
 */
@Log4j2
public final class ModlServices {

    /**
     * A Jackson object mapper to create the PopulatorResponse object from JSON.
     */
    private final ObjectMapper objectMapper;

    private final Interpreter interpreter;

    /**
     * Default constructor
     */
    public ModlServices() {
        objectMapper = new ObjectMapper();
        interpreter = new Interpreter();
    }

    /**
     * Interpret a NUM record MODL string to a JSON String.
     *
     * @param numRecord The NUM record string.
     * @return The interpreted result as a JSON string.
     * @throws NumBadRecordException on error
     * @throws NumLookupRedirect     on error
     */
    public String interpretNumRecord(final String numRecord) throws NumBadRecordException, NumLookupRedirect {
        assert numRecord != null && numRecord.trim()
                .length() > 0;
        log.trace("Interpreting NUM record: {}", numRecord);

        try {
            final TransformationContext ctx = TransformationContext.emptyCtx();
            final Tuple2<TransformationContext, Modl> interpreted = interpreter.apply(ctx, numRecord);

            checkForRedirection(interpreted._2);

            final JsonNode jsonNode = new JacksonJsonNodeTransform(ctx).apply(interpreted._2);
            return new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonNode);
        } catch (Exception e) {
            log.error("Exception during interpretNumRecord().", e);
            throw new NumBadRecordException("Error interpreting NUM record.", e);
        }
    }

    /**
     * Look for a redirect instruction in the interpreted NUM record.
     *
     * @param modlObject the interpreted NUM record
     * @throws NumLookupRedirect on error
     */
    private void checkForRedirection(final Modl modlObject) throws NumLookupRedirect {
        if (modlObject.getStructures() != null) {
            for (final Structure structure : modlObject.getStructures()) {
                findRedirect(structure);
            }
        }
    }

    /**
     * Look for a redirect instruction in the interpreted NUM record, recursively.
     *
     * @param structure the ModlValue to check.
     * @throws NumLookupRedirect on error
     */
    private void findRedirect(final Object structure) throws NumLookupRedirect {

        // If its a Pair then check whether the key indicates a redirect.
        if (structure instanceof Pair) {
            final Pair pair = (Pair) structure;

            // Check for @R
            if ("@R".equals(pair.getKey())) {
                final Object value = pair.getValue();
                if (value instanceof StringPrimitive) {
                    final StringPrimitive str = (StringPrimitive) value;
                    throw new NumLookupRedirect(str.getValue());
                }
            }
            findRedirect(pair.getValue());
        }

        // Check the pairs in a Map
        if (structure instanceof Map) {
            final Map map = (Map) structure;
            for (MapItem mi : map.getMapItems()) {
                findRedirect(mi);
            }
        }
        // Check the pairs in an Array
        if (structure instanceof Array) {
            final Array array = (Array) structure;
            for (ArrayItem item : array.getArrayItems()) {
                findRedirect(item);
            }
        }
    }

    /**
     * Interpret a NUM record response from the populator.
     *
     * @param numRecord The NUM record string.
     * @return The interpreted result as a PopulatorResponse object.
     * @throws NumBadRecordException on error
     */
    public PopulatorResponse interpretPopulatorResponse(final String numRecord) throws NumBadRecordException {
        assert numRecord != null && numRecord.trim()
                .length() > 0;
        log.trace("Interpreting populator response record: {}", numRecord);

        try {
            final String json = interpreter.interpretToPrettyJsonString(numRecord);
            log.trace("Interpreted populator response: {}", json);
            final PopulatorResponse response = objectMapper.readValue(json, PopulatorResponse.class);

            if (response.isValid() && response.getStatus_() == null) {
                // We have a valid MODL record because we didn't get any errors from the interpreter, so it must be a
                // TXT record. Set the status to indicate this and return the MODL record in the response object.
                final PopulatorResponseRecord status_ = new PopulatorResponseRecord();
                status_.setCode(PopulatorResponse.VALID_TXT_RECORD_CODE);
                status_.setDescription("TXT Record");
                response.setStatus_(status_);
                response.setNumRecord(numRecord);
            }
            return response;
        } catch (Exception e) {
            log.error("Exception during interpretPopulatorResponse().", e);
            throw new NumBadRecordException("Error interpreting populator response record.", e);
        }
    }

}
