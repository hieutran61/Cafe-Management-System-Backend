package com.hieutran.cafe.DAO;

import com.hieutran.cafe.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDAO extends JpaRepository<Product, Integer> {
}
