package com.juansecu.openfusion.openfusionopenapiplugin.auth.handlers;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AuthenticationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    protected ModelAndView handleAuthenticationException(
        final AuthenticationException authenticationException
    ) {
        final ModelAndView modelAndView = new ModelAndView();

        modelAndView.getModelMap().addAttribute("error", "Username or password incorrect");

        modelAndView.setViewName("redirect:/auth/login");

        return modelAndView;
    }
}
