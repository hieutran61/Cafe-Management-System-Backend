package com.hieutran.cafe.controller;

import com.hieutran.cafe.DTO.UserDTO;
import com.hieutran.cafe.projection.UserProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping("/user")
public interface IUserController {

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping("/get")
    public ResponseEntity<List<UserProjection>> getAllUser();

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody Map<String, String> requestMap);
}
