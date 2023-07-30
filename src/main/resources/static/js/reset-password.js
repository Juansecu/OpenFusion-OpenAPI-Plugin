import { validatePasswordConfirmation } from './forms/form-validations.js';
import {
    checkNewPassword,
    checkNewPasswordConfirmation
} from './forms/forms.js';

newPassword.addEventListener('input', checkNewPassword);

newPasswordConfirmation.addEventListener('input', checkNewPasswordConfirmation);

resetPasswordForm.addEventListener('input', event => {
    const newPasswordConfirmationValidationResult = validatePasswordConfirmation(
        newPassword.value,
        newPasswordConfirmation.value
    );
    const invalidInputs = [
        newPasswordConfirmationValidationResult
    ].filter(validationResult => !validationResult.isValid);

    if (invalidInputs.length > 0)
        resetPasswordButton.setAttribute('disabled', 'disabled');
    else
        resetPasswordButton.removeAttribute('disabled');
});
