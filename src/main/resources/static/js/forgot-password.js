import { validateUsernameOrEmail } from './forms/form-validations.js';
import { checkUsernameOrEmail } from './forms/forms.js';

forgotPasswordForm.addEventListener('input', event => {
    const usernameOrEmailValidationResult = validateUsernameOrEmail(
        forgotPasswordForm['usernameOrEmail'].value
    );
    const invalidInputs = [usernameOrEmailValidationResult].filter(
        validationResult => !validationResult.isValid
    );

    if (invalidInputs.length > 0)
        forgotPasswordButton.setAttribute('disabled', 'disabled');
    else
        forgotPasswordButton.removeAttribute('disabled');
});

usernameOrEmail.addEventListener('input', checkUsernameOrEmail);
