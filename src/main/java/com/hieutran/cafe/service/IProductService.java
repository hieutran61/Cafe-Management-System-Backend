package com.hieutran.cafe.service;

import com.hieutran.cafe.DTO.ProductDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IProductService {
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductDTO>> getAllProduct();

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);
}
