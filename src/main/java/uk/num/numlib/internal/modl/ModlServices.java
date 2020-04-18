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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import uk.modl.interpreter.Interpreter;
import uk.modl.modlObject.ModlObject;
import uk.modl.modlObject.ModlValue;
import uk.modl.parser.printers.JsonPrinter;
import uk.num.numlib.exc.NumBadRecordException;

import java.util.ArrayList;

/**
 * A class to act as a facade for the MODL interpreter.
 *
 * @author tonywalmsley
 */
@Log4j2
public class ModlServices {
    /**
     * A Jackson object mapper to create the ModuleConfig object from JSON.
     */
    private final ObjectMapper objectMapper;

    /**
     * Default constructor
     */
    public ModlServices() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Interpret a NUM record MODL string to a JSON String.
     *
     * @param numRecord The NUM record string.
     * @return The interpreted result as a JSON string.
     * @throws NumBadRecordException on error
     * @throws NumQueryRedirect      on error
     * @throws NumLookupRedirect     on error
     */
    public String interpretNumRecord(final String numRecord) throws NumBadRecordException, NumQueryRedirect,
                                                                    NumLookupRedirect {
        assert numRecord != null && numRecord.trim()
                .length() > 0;
        log.trace("Interpreting NUM record: {}", numRecord);

        try {
            final ModlObject modlObject = Interpreter.interpret(numRecord, new ArrayList<>());
            checkForRedirection(modlObject);
            return JsonPrinter.printModl(modlObject);
        } catch (Exception e) {
            log.error("Exception during interpretNumRecord().", e);
            throw new NumBadRecordException("Error interpreting NUM record.", e);
        }
    }

    /**
     * Look for a redirect instruction in the interpreted NUM record.
     *
     * @param modlObject the interpreted NUM record
     * @throws NumQueryRedirect  on error
     * @throws NumLookupRedirect on error
     */
    private void checkForRedirection(final ModlObject modlObject) throws NumQueryRedirect, NumLookupRedirect {
        if (modlObject.getStructures() != null) {
            for (final ModlObject.Structure structure : modlObject.getStructures()) {
                findRedirect(structure);
            }
        }
    }

    /**
     * Look for a redirect instruction in the interpreted NUM record, recursively.
     *
     * @param structure the ModlValue to check.
     * @throws NumQueryRedirect  on error
     * @throws NumLookupRedirect on error
     */
    private void findRedirect(final ModlValue structure) throws NumQueryRedirect, NumLookupRedirect {

        // If its a Pair then check whether the key indicates a redirect.
        if (structure instanceof ModlObject.Pair) {
            final ModlObject.Pair pair = (ModlObject.Pair) structure;

            // Check for q_
            if ("q_".equals(pair.getKey().string)) {
                final Object value = pair.getValue();
                if (value instanceof ModlObject.String) {
                    final ModlObject.String str = (ModlObject.String) value;
                    throw new NumQueryRedirect(str.string);
                }
            }
            // Check for l_
            if ("l_".equals(pair.getKey().string)) {
                final Object value = pair.getValue();
                if (value instanceof ModlObject.String) {
                    final ModlObject.String str = (ModlObject.String) value;
                    throw new NumLookupRedirect(str.string);
                }
            }
            findRedirect(pair.getModlValue());
        }

        // Check the pairs in a Map
        if (structure instanceof ModlObject.Map) {
            final ModlObject.Map map = (ModlObject.Map) structure;
            for (ModlObject.Pair pair : map.getPairs()) {
                findRedirect(pair);
            }
        }
        // Check the pairs in an Array
        if (structure instanceof ModlObject.Array) {
            final ModlObject.Array array = (ModlObject.Array) structure;
            for (ModlValue modlValue : array.getModlValues()) {
                findRedirect(modlValue);
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
            final ModlObject modlObject = Interpreter.interpret(numRecord, new ArrayList<>());
            final String json = JsonPrinter.printModl(modlObject);
            log.trace("Interpreted populator response: {}", json);
            final PopulatorResponse response = objectMapper.readValue(json, PopulatorResponse.class);

            if (!response.isValid() && response.getStatus_() == null) {
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
