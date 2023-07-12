import {
    validatePassword,
    validatePasswordConfirmation
} from './forms/form-validations.js';
import { checkPassword } from './forms/forms.js';

currentPassword.addEventListener('input', checkPassword);

newPassword.addEventListener('input', event => {
    const newPasswordInput = event.target;
    const newPasswordConfirmationValidationResult = validatePasswordConfirmation(
        newPasswordInput.value,
        newPasswordConfirmation.value
    );
    const newPasswordPostErrors = document.getElementById('newPasswordPostErrors');
    const newPasswordValidationResult = validatePassword(newPasswordInput.value);

    if (!newPasswordConfirmationValidationResult.isValid) {
        newPasswordConfirmation.classList.remove('is-valid');
        newPasswordConfirmation.classList.add('is-invalid');

        newPasswordConfirmationError.textContent = newPasswordConfirmationValidationResult.errorMessage;
    }

    if (!newPasswordValidationResult.isValid) {
        if (newPasswordPostErrors) newPasswordPostErrors.textContent = '';

        newPasswordInput.classList.remove('is-valid');
        newPasswordInput.classList.add('is-invalid');

        newPasswordError.textContent = newPasswordValidationResult.errorMessage;

        return;
    }

    newPasswordError.textContent = '';

    newPasswordInput.classList.remove('is-invalid');
    newPasswordInput.classList.add('is-valid');
});

newPasswordConfirmation.addEventListener('input', event => {
    const newPasswordConfirmationInput = event.target;
    const newPasswordConfirmationValidationResult = validatePasswordConfirmation(
        newPassword.value,
        newPasswordConfirmationInput.value
    );

    if (!newPasswordConfirmationValidationResult.isValid) {
        newPasswordConfirmationInput.classList.remove('is-valid');
        newPasswordConfirmationInput.classList.add('is-invalid');

        newPasswordConfirmationError.textContent = newPasswordConfirmationValidationResult.errorMessage;

        return;
    }

    newPasswordConfirmationError.textContent = '';

    newPasswordConfirmationInput.classList.remove('is-invalid');
    newPasswordConfirmationInput.classList.add('is-valid');
});

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
