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

public class ValidationResult {

    public static final ValidationResult VALID_NO_ERRORS = new ValidationResult();

    @Getter
    private final List<Result> errors;

    public ValidationResult() {
        errors = new ArrayList<>();
    }

    public void addMessage(@NonNull final ErrorCode code, @NonNull final String offendingPart) {
        errors.add(new Result(code, offendingPart));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public void merge(@NonNull final ValidationResult other) {
        errors.addAll(other.errors);
    }

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
        NEGATIVE_MODULE_NUMBER, NULL_UNACCEPTABLE
    }

    @Value
    public static class Result {

        ErrorCode code;

        String offendingPart;

    }

}

