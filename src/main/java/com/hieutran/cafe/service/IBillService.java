package com.hieutran.cafe.service;

import com.hieutran.cafe.model.Bill;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IBillService {


    ResponseEntity<String> generateReport(Map<String, Object> requestMap);

    ResponseEntity<List<Bill>> getBills();


    ResponseEntity<byte[]> getPDf(Map<String, Object> requestMap);

    ResponseEntity<String> deleteBill(Integer id);
}
