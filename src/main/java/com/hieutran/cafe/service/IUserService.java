package com.hieutran.cafe.service;

import com.hieutran.cafe.DTO.UserDTO;
import com.hieutran.cafe.projection.UserProjection;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IUserService {

    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requestMap);

    ResponseEntity<List<UserProjection>> getAllUser();

    ResponseEntity<String> update(Map<String, String> requestMap);

}
