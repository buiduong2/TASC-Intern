package com.authentication_service.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.authentication_service.dto.req.LoginReq;
import com.authentication_service.exception.AuthValidationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthMvcController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("login")
    public String login(@ModelAttribute(name = "data") LoginReq data, BindingResult bindingResult,
            HttpServletRequest request) {
        Exception ex = (Exception) request.getSession()
                .getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        if (ex instanceof AuthValidationException validationEx) {
            List<FieldError> errors = validationEx.getErrors();
            errors.forEach(bindingResult::addError);
        }

        return "login";
    }

    

}
