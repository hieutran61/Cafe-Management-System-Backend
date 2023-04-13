package com.hieutran.cafe.serviceImpl;

import com.hieutran.cafe.DAO.BillDAO;
import com.hieutran.cafe.DAO.CategoryDAO;
import com.hieutran.cafe.DAO.ProductDAO;
import com.hieutran.cafe.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    ProductDAO productDAO;

    @Autowired
    BillDAO billDAO;


    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("category", categoryDAO.count());
        map.put("product", productDAO.count());
        map.put("bill", billDAO.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
