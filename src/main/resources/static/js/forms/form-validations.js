/**
 * @param {string} email
 * @returns {{
 *   errorMessage: string,
 *   isValid: boolean
 * }}
 */
export function validateEmail(email) {
    const result = {
        errorMessage: '',
        isValid: false
    };

    if (!email) {
        result.errorMessage = 'Email cannot be empty';
        return result;
    }

    if (email.length > 50) {
        result.errorMessage = 'Email cannot be longer than 50 characters';
        return result;
    }

    if (!/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email)) {
        result.errorMessage = 'Email address is invalid';
        return result;
    }

    result.isValid = result.errorMessage === '';

    return result;
}

/**
 * @param {string} email
 * @param {string} emailConfirmation
 * @returns {{
 *   errorMessage: string,
 *   isValid: boolean
 * }}
 */
export function validateEmailConfirmation(email, emailConfirmation) {
    const result = {
        errorMessage: '',
        isValid: false
    };

    if (!emailConfirmation) {
        result.errorMessage = 'Email confirmation cannot be empty';
        return result;
    }

    if (email !== emailConfirmation) {
        result.errorMessage = 'Email and email confirmation do not match';
        return result;
    }

    result.isValid = result.errorMessage === '';

    return result;
}

/**
 * @param {string} password
 * @returns {{
 *   errorMessage: string,
 *   isValid: boolean
 * }}
 */
export function validatePassword(password) {
    const result = {
        errorMessage: '',
        isValid: false
    };

    if (!password) {
        result.errorMessage = 'Password cannot be empty';
        return result;
    }

    if (password.length < 8) {
        result.errorMessage = 'Password must be at least 8 characters long';
        return result;
    }

    if (password.length > 32) {
        result.errorMessage = 'Password cannot be longer than 32 characters';
        return result;
    }

    result.isValid = result.errorMessage === '';

    return result;
}

/**
 * @param {string} password
 * @param {string} passwordConfirmation
 * @returns {{
 *   errorMessage: string,
 *   isValid: boolean
 * }}
 */
export function validatePasswordConfirmation(password, passwordConfirmation) {
    const result = {
        errorMessage: '',
        isValid: false
    };

    if (!passwordConfirmation) {
        result.errorMessage = 'Password confirmation cannot be empty';
        return result;
    }

    if (password !== passwordConfirmation) {
        result.errorMessage = 'Password and password confirmation do not match';
        return result;
    }

    result.isValid = result.errorMessage === '';

    return result;
}

/**
 * @param {string} username
 * @returns {{
 *   errorMessage: string,
 *   isValid: boolean
 * }}
 */
export function validateUsername(username) {
    const result = {
        errorMessage: '',
        isValid: false
    }

    if (!username) {
        result.errorMessage = 'Username cannot be empty';
        return result;
    }

    if (username.length < 4) {
        result.errorMessage = 'Username must be at least 4 characters long';
        return result;
    }

    if (username.length > 32) {
        result.errorMessage = 'Username cannot be longer than 32 characters';
        return result;
    }

    if (!/^[0-9a-z_-]+$/i.test(username)) {
        result.errorMessage = 'Username can only contain alphanumeric characters, underscores and dashes';
        return result;
    }

    result.isValid = result.errorMessage === '';

    return result;
}
