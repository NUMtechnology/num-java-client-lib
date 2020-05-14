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

/**
 * Validate Module Numbers
 */
public class NumModuleNumberValidator {


    /**
     * Validate a module number
     *
     * @param moduleNumber int
     * @return ValidationResult
     */
    public ValidationResult validate(final int moduleNumber) {
        final ValidationResult result = new ValidationResult();

        if (moduleNumber < 0) {
            result.addMessage(ValidationResult.ErrorCode.NEGATIVE_MODULE_NUMBER, Integer.toString(moduleNumber));
        }

        return result;
    }

    /**
     * Validate a module number
     *
     * @param moduleNumber String
     * @return ValidationResult
     */
    public ValidationResult validate(final String moduleNumber) {
        final ValidationResult result = new ValidationResult();

        int number = 0;
        try {
            number = Integer.parseInt(moduleNumber);
        } catch (final Throwable e) {
            result.addMessage(ValidationResult.ErrorCode.INVALID_MODULE_NUMBER, moduleNumber);
        }

        if (number < 0) {
            result.addMessage(ValidationResult.ErrorCode.NEGATIVE_MODULE_NUMBER, moduleNumber);
        }

        return result;
    }

}
