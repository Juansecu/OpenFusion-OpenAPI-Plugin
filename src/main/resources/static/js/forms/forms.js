import {
    validateEmail,
    validatePassword,
    validatePasswordConfirmation,
    validateUsername
} from './form-validations.js';

export function checkEmail(event) {
    const emailInput = event.target;
    const emailPostErrors = document.getElementById('emailPostErrors');
    const emailValidationResult = validateEmail(emailInput.value);

    if (!emailValidationResult.isValid) {
        if (emailPostErrors) emailPostErrors.textContent = '';

        emailInput.classList.remove('is-valid');
        emailInput.classList.add('is-invalid');

        emailError.textContent = emailValidationResult.errorMessage;

        return;
    }

    emailError.textContent = '';

    emailInput.classList.remove('is-invalid');
    emailInput.classList.add('is-valid');
}

export function checkPassword(event) {
    const passwordInput = event.target;
    const passwordPostErrors = document.getElementById('passwordPostErrors');
    const passwordConfirmationValidationResult = validatePasswordConfirmation(
        passwordInput.value,
        passwordConfirmation.value
    );
    const passwordValidationResult = validatePassword(passwordInput.value);

    if (!passwordConfirmationValidationResult.isValid) {
        passwordConfirmation.classList.remove('is-valid');
        passwordConfirmation.classList.add('is-invalid');

        passwordConfirmationError.textContent = passwordConfirmationValidationResult.errorMessage;
    }

    if (!passwordValidationResult.isValid) {
        if (passwordPostErrors) emailPostErrors.textContent = '';

        passwordInput.classList.remove('is-valid');
        passwordInput.classList.add('is-invalid');

        passwordError.textContent = passwordValidationResult.errorMessage;

        return;
    }

    passwordError.textContent = '';

    event.target.classList.remove('is-invalid');
    event.target.classList.add('is-valid');
}

export function checkPasswordConfirmation(event) {
    const passwordConfirmationInput = event.target;
    const passwordConfirmationValidationResult = validatePasswordConfirmation(
        password.value,
        passwordConfirmationInput.value
    );

    if (!passwordConfirmationValidationResult.isValid) {
        passwordConfirmationInput.classList.remove('is-valid');
        passwordConfirmationInput.classList.add('is-invalid');

        passwordConfirmationError.textContent = passwordConfirmationValidationResult.errorMessage;

        return;
    }

    passwordConfirmationError.textContent = '';

    passwordConfirmationInput.classList.remove('is-invalid');
    passwordConfirmationInput.classList.add('is-valid');
}

export function checkUsername(event) {
    const usernameInput = event.target;
    const usernamePostErrors = document.getElementById('usernamePostErrors');
    const usernameValidationResult = validateUsername(usernameInput.value);

    if (!usernameValidationResult.isValid) {
        if (usernamePostErrors) usernamePostErrors.textContent = '';

        usernameInput.classList.remove('is-valid');
        usernameInput.classList.add('is-invalid');

        usernameError.textContent = usernameValidationResult.errorMessage;

        return;
    }

    usernameError.textContent = '';

    usernameInput.classList.remove('is-invalid');
    usernameInput.classList.add('is-valid');
}
