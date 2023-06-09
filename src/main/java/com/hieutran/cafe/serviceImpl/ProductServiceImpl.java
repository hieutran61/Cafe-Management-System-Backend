package com.hieutran.cafe.serviceImpl;

import com.hieutran.cafe.DAO.ProductDAO;
import com.hieutran.cafe.DTO.ProductDTO;
import com.hieutran.cafe.JWT.JwtFilter;
import com.hieutran.cafe.constants.CafeConstants;
import com.hieutran.cafe.model.Category;
import com.hieutran.cafe.model.Product;
import com.hieutran.cafe.service.IProductService;
import com.hieutran.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductDAO productDAO;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap, false)){
                    productDAO.save(getProductFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Product Add Successfully.", HttpStatus.OK);
                }
                else return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getAllProduct() {
        try {
            return new ResponseEntity<>(productDAO.getAllProduct(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap, false)){
                    Optional<Product> optional = productDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if (optional.isPresent()){
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(optional.get().getStatus());
                        productDAO.save(product);
                        return CafeUtils.getResponseEntity("Product update successfully.", HttpStatus.OK);
                    }
                    else return CafeUtils.getResponseEntity("Product does not exist.", HttpStatus.OK);
                }
                else return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if (jwtFilter.isAdmin()){
                Optional<Product> optional = productDAO.findById(id);
                if (optional.isPresent()){
                    productDAO.deleteById(id);
                    return CafeUtils.getResponseEntity("Product deleted successfully.", HttpStatus.OK);
                }
                else return CafeUtils.getResponseEntity("Product does not exist.", HttpStatus.OK);
            }
            else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                Optional<Product> optional = productDAO.findById(Integer.parseInt(requestMap.get("id")));
                if (optional.isPresent()){
                    productDAO.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("Update status successfully.", HttpStatus.OK);
                }
                else return CafeUtils.getResponseEntity("Product does not exist.", HttpStatus.OK);
            }
            else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getByCategory(Integer id) {
        try {
            return new ResponseEntity<>(productDAO.getProductByCategory(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR );
    }

    @Override
    public ResponseEntity<ProductDTO> getProductById(Integer id) {
        try {
            return new ResponseEntity<>(productDAO.getProductById(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ProductDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*==================================================================================================
                               PRIVATE FUNCTIONS
    ==================================================================================================*/
    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            }
            else if (!validateId)
                return true;
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Product product = new Product();
        if (isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }
        else product.setStatus("true");
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription((requestMap.get("description")));
        product.setPrice(Integer.parseInt((requestMap.get("price"))));
        return product;


    }
}
