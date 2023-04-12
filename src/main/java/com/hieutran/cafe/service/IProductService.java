package com.hieutran.cafe.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IProductService {
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);
}
