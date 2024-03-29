import { validatePasswordConfirmation } from './forms/form-validations.js';
import {
    checkNewPassword,
    checkNewPasswordConfirmation,
    checkPassword
} from './forms/forms.js';

currentPassword.addEventListener('input', checkPassword);

newPassword.addEventListener('input', checkNewPassword);

newPasswordConfirmation.addEventListener('input', checkNewPasswordConfirmation);

updatePasswordForm.addEventListener('input', event => {
    const newPasswordConfirmationValidationResult = validatePasswordConfirmation(
        newPassword.value,
        newPasswordConfirmation.value
    );
    const invalidInputs = [
        newPasswordConfirmationValidationResult
    ].filter(validationResult => !validationResult.isValid);

    if (invalidInputs.length > 0)
        updatePasswordButton.setAttribute('disabled', 'disabled');
    else
        updatePasswordButton.removeAttribute('disabled');
});
