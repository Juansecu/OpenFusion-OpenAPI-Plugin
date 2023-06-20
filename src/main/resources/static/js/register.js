import {
    validateEmail,
    validatePassword,
    validatePasswordConfirmation,
    validateUsername
} from './forms/form-validations.js';
import {
    checkEmail,
    checkPasswordConfirmation,
    checkPasswordWithConfirmation,
    checkUsername
} from './forms/forms.js';

email.addEventListener('input', checkEmail);

password.addEventListener('input', checkPasswordWithConfirmation);

passwordConfirmation.addEventListener('input', checkPasswordConfirmation);

registerForm.addEventListener('input', (event) => {
    const emailValidationResult = validateEmail(registerForm['email'].value);
    const passwordValidationResult = validatePassword(registerForm['password'].value);
    const passwordConfirmationValidationResult = validatePasswordConfirmation(
        registerForm['password'].value,
        registerForm['passwordConfirmation'].value
    );
    const usernameValidationResult = validateUsername(registerForm['username'].value);
    const invalidInputs = [
        emailValidationResult,
        passwordValidationResult,
        passwordConfirmationValidationResult,
        usernameValidationResult
    ].filter((validationResult) => !validationResult.isValid);

    if (invalidInputs.length > 0)
        registerButton.setAttribute('disabled', 'disabled');
    else
        registerButton.removeAttribute('disabled');
});

username.addEventListener('input', checkUsername);
