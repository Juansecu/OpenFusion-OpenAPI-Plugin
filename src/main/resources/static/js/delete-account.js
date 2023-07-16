import { validatePassword } from './forms/form-validations.js';
import { checkPassword } from './forms/forms.js';

currentPassword.addEventListener('input', checkPassword);

deleteAccountForm.addEventListener('input', (event) => {
    const currentPasswordValidationResult = validatePassword(deleteAccountForm['currentPassword'].value);
    const invalidInputs = [
        currentPasswordValidationResult
    ].filter((validationResult) => !validationResult.isValid);

    if (invalidInputs.length > 0)
        deleteAccountButton.setAttribute('disabled', 'disabled');
    else
        deleteAccountButton.removeAttribute('disabled');
});
