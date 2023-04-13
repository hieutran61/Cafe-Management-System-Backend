package com.hieutran.cafe.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IBillService {


    ResponseEntity<String> generateReport(Map<String, Object> requestMap);
}
