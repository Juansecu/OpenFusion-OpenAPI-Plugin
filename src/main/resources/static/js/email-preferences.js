import {
    validateEmail,
    validateEmailConfirmation,
    validatePassword
} from './forms/form-validations.js';
import {
    checkEmailConfirmation,
    checkEmailWithConfirmation,
    checkPassword
} from './forms/forms.js';

email.addEventListener('input', checkEmailWithConfirmation);

emailConfirmation.addEventListener('input', checkEmailConfirmation);

currentPassword.addEventListener('input', checkPassword);

updateEmailForm.addEventListener('input', (event) => {
    const emailValidationResult = validateEmail(updateEmailForm['email'].value);
    const emailConfirmationValidationResult = validateEmailConfirmation(
        updateEmailForm['email'].value,
        updateEmailForm['emailConfirmation'].value
    );
    const passwordValidationResult = validatePassword(updateEmailForm['currentPassword'].value);
    const invalidInputs = [
        emailValidationResult,
        emailConfirmationValidationResult,
        passwordValidationResult
    ].filter((validationResult) => !validationResult.isValid);

    if (invalidInputs.length > 0)
        updateEmailButton.setAttribute('disabled', 'disabled');
    else
        updateEmailButton.removeAttribute('disabled');
});
