import {
    validatePassword,
    validateUsername
} from './forms/form-validations.js';
import {
    checkPassword,
    checkUsername
} from './forms/forms.js';

password.addEventListener('input', checkPassword);

loginForm.addEventListener('input', (event) => {
    const passwordValidationResult = validatePassword(loginForm['password'].value);
    const usernameValidationResult = validateUsername(loginForm['username'].value);
    const invalidInputs = [
        passwordValidationResult,
        usernameValidationResult
    ].filter((validationResult) => !validationResult.isValid);

    if (invalidInputs.length > 0)
        loginButton.setAttribute('disabled', 'disabled');
    else
        loginButton.removeAttribute('disabled');

});

username.addEventListener('input', checkUsername);
