package com.hieutran.cafe.DAO;

import com.hieutran.cafe.DTO.ProductDTO;
import com.hieutran.cafe.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import javax.transaction.Transactional;
import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Integer> {


    List<ProductDTO> getAllProduct();

    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status") String status,@Param("id") Integer id);

    List<ProductDTO> getProductByCategory(@Param("id") Integer id);

    ProductDTO getProductById(@Param("id") Integer id);
}
