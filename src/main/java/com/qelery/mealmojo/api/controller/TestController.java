package com.qelery.mealmojo.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello world";
    }
}
