package com.hieutran.cafe.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/dashboard")
public interface IDashboardController {

    @GetMapping("/details")
    ResponseEntity<Map<String, Object>> getCount();

}
