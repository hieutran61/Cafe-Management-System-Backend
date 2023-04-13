package com.hieutran.cafe.controllerImpl;


import com.hieutran.cafe.controller.IDashboardController;
import com.hieutran.cafe.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardControllerImpl implements IDashboardController {

    @Autowired
    IDashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }
}
