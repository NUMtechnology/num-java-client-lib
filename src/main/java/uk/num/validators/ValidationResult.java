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

package uk.num.validators;

import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * The results of a validator
 */
public class ValidationResult {

    /**
     * Only one of these really needed.
     */
    public static final ValidationResult VALID_NO_ERRORS = new ValidationResult();

    /**
     * A List of results
     */
    @Getter
    private final List<Result> errors;

    /**
     * Default constructor
     */
    public ValidationResult() {
        errors = new ArrayList<>();
    }

    /**
     * Add an error message
     *
     * @param code          an ErrorCode
     * @param offendingPart something to indicate what contains the error. Can be null.
     * @return this
     */
    public ValidationResult addMessage(@NonNull final ErrorCode code, final String offendingPart) {
        final String part = (offendingPart == null) ? "null" : offendingPart;
        errors.add(new Result(code, part));
        return this;
    }

    /**
     * The result of the validation.
     *
     * @return false if there are any error messages
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Merge errors from another validator
     *
     * @param other a ValidationResult
     * @return this
     */
    public ValidationResult merge(@NonNull final ValidationResult other) {
        errors.addAll(other.errors);
        return this;
    }

    /**
     * Possible errors
     */
    public enum ErrorCode {
        INVALID_NUM_PROTOCOL_PREFIX,
        TOO_MANY_COLONS,
        EXCEPTION_MESSAGE,
        DOMAIN_NAME_TOO_LONG,
        ZERO_LENGTH_LABEL,
        LABEL_TOO_LONG,
        SPACES_IN_LABEL,
        FORM_FEED_IN_LABEL,
        TAB_IN_LABEL,
        CARRIAGE_RETURN_IN_LABEL,
        BACKSPACE_IN_LABEL,
        NEWLINE_IN_LABEL,
        PATTERN_MISMATCH,
        NO_AT_SYMBOL,
        TOO_MANY_AT_SYMBOLS,
        LOCAL_PART_OF_EMAIL_TOO_LONG,
        LOCAL_PART_OF_EMAIL_IS_EMPTY,
        PATH_MUST_START_WITH_SLASH,
        ZERO_LENGTH_PATH_COMPONENT,
        PATH_COMPONENT_TOO_LONG,
        NEGATIVE_MODULE_NUMBER,
        HOSTED_DOMAIN_NAME_TOO_LONG,
        INDEPENDENT_DOMAIN_NAME_TOO_LONG,
        INVALID_MODULE_NUMBER,
        PATH_COMPONENT_CONTAINS_SPACE,
        PATH_COMPONENT_CONTAINS_FORMFEED,
        PATH_COMPONENT_CONTAINS_BACKSPACE,
        PATH_COMPONENT_CONTAINS_TAB,
        PATH_COMPONENT_CONTAINS_CARRIAGE_RETURN,
        PATH_COMPONENT_CONTAINS_NEWLINE,
        LOCAL_PART_OF_EMAIL_CONTAINS_NEWLINE,
        LOCAL_PART_OF_EMAIL_CONTAINS_CARRIAGE_RETURN,
        LOCAL_PART_OF_EMAIL_CONTAINS_TAB,
        LOCAL_PART_OF_EMAIL_CONTAINS_BACKSPACE,
        LOCAL_PART_OF_EMAIL_CONTAINS_FORMFEED,
        LOCAL_PART_OF_EMAIL_CONTAINS_DOUBLE_DOT,
        LOCAL_PART_OF_EMAIL_CONTAINS_STARTS_WITH_DOT,
        LOCAL_PART_OF_EMAIL_CONTAINS_ENDS_WITH_DOT,
        HYPHEN_AT_END_OF_DOMAIN,
        HYPHEN_AT_START_OF_DOMAIN,
        LOCAL_PART_OF_EMAIL_CONTAINS_DOUBLE_QUOTE,
        NULL_UNACCEPTABLE
    }

    /**
     * Error result POJO
     */
    @Value
    public static class Result {

        @NonNull
        ErrorCode code;

        @NonNull
        String offendingPart;

    }

}

