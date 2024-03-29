import {
    validateEmail,
    validateEmailConfirmation,
    validatePassword,
    validatePasswordConfirmation,
    validateUsername,
    validateUsernameOrEmail
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

export function checkEmailConfirmation(event) {
    const emailConfirmationInput = event.target;
    const emailConfirmationValidationResult = validateEmailConfirmation(
        email.value,
        emailConfirmationInput.value
    );

    if (!emailConfirmationValidationResult.isValid) {
        emailConfirmationInput.classList.remove('is-valid');
        emailConfirmationInput.classList.add('is-invalid');

        emailConfirmationError.textContent = emailConfirmationValidationResult.errorMessage;

        return;
    }

    emailConfirmationError.textContent = '';

    emailConfirmationInput.classList.remove('is-invalid');
    emailConfirmationInput.classList.add('is-valid');
}

export function checkEmailWithConfirmation(event) {
    const emailInput = event.target;
    const emailConfirmationValidationResult = validateEmailConfirmation(
        emailInput.value,
        emailConfirmation.value
    );
    const emailPostErrors = document.getElementById('emailPostErrors');
    const emailValidationResult = validateEmail(emailInput.value);

    if (!emailConfirmationValidationResult.isValid) {
        emailConfirmation.classList.remove('is-valid');
        emailConfirmation.classList.add('is-invalid');

        emailConfirmationError.textContent = emailConfirmationValidationResult.errorMessage;
    }

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

export function checkNewPassword(event) {
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
}

export function checkNewPasswordConfirmation(event) {
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
}

export function checkPassword(event) {
    const passwordInput = event.target;
    const passwordPostErrors = document.getElementById('passwordPostErrors');
    const passwordValidationResult = validatePassword(passwordInput.value);

    if (!passwordValidationResult.isValid) {
        if (passwordPostErrors) passwordPostErrors.textContent = '';

        passwordInput.classList.remove('is-valid');
        passwordInput.classList.add('is-invalid');

        passwordError.textContent = passwordValidationResult.errorMessage;

        return;
    }

    passwordError.textContent = '';

    passwordInput.classList.remove('is-invalid');
    passwordInput.classList.add('is-valid');
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

export function checkPasswordWithConfirmation(event) {
    const passwordInput = event.target;
    const passwordConfirmationValidationResult = validatePasswordConfirmation(
        passwordInput.value,
        passwordConfirmation.value
    );
    const passwordPostErrors = document.getElementById('passwordPostErrors');
    const passwordValidationResult = validatePassword(passwordInput.value);

    if (!passwordConfirmationValidationResult.isValid) {
        passwordConfirmation.classList.remove('is-valid');
        passwordConfirmation.classList.add('is-invalid');

        passwordConfirmationError.textContent = passwordConfirmationValidationResult.errorMessage;
    }

    if (!passwordValidationResult.isValid) {
        if (passwordPostErrors) passwordPostErrors.textContent = '';

        passwordInput.classList.remove('is-valid');
        passwordInput.classList.add('is-invalid');

        passwordError.textContent = passwordValidationResult.errorMessage;

        return;
    }

    passwordError.textContent = '';

    passwordInput.classList.remove('is-invalid');
    passwordInput.classList.add('is-valid');
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

export function checkUsernameOrEmail(event) {
    const usernameOrEmailInput = event.target;
    const usernameOrEmailPostErrors = document.getElementById('emailOrUsernamePostErrors');
    const usernameOrEmailValidationResult = validateUsernameOrEmail(usernameOrEmailInput.value);

    if (!usernameOrEmailValidationResult.isValid) {
        if (usernameOrEmailPostErrors) emailOrUsernamePostErrors.textContent = '';

        usernameOrEmailInput.classList.remove('is-valid');
        usernameOrEmailInput.classList.add('is-invalid');

        usernameOrEmailError.textContent = usernameOrEmailValidationResult.errorMessage;

        return;
    }

    if (usernameOrEmailInput.value.includes('@'))
        usernameOrEmailInput.setAttribute('name', 'email');
    else
        usernameOrEmailInput.setAttribute('name', 'username');

    usernameOrEmailError.textContent = '';

    usernameOrEmailInput.classList.remove('is-invalid');
    usernameOrEmailInput.classList.add('is-valid');
}
