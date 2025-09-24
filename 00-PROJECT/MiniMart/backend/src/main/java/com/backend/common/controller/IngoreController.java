package com.backend.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class IgnoreController {
    @RequestMapping({ "/favicon.ico", "/.well-known/appspecific/**" })
    void ignore() {
    }
}