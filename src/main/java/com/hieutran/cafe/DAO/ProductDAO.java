package com.hieutran.cafe.DAO;

import com.hieutran.cafe.DTO.ProductDTO;
import com.hieutran.cafe.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Integer> {


    List<ProductDTO> getAllProduct();
}
