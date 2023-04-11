package com.hieutran.cafe.serviceImpl;

import com.hieutran.cafe.DAO.UserDAO;
import com.hieutran.cafe.JWT.CustomUserDetailService;
import com.hieutran.cafe.JWT.JwtUtils;
import com.hieutran.cafe.constants.CafeConstants;
import com.hieutran.cafe.model.User;
import com.hieutran.cafe.service.IUserService;
import com.hieutran.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserDAO userDAO;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signUp {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)){
                User user = userDAO.findByEmail(requestMap.get(("email")));
                if (Objects.isNull(user)){
                    userDAO.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registerd.", HttpStatus.OK);
                }
                else {
                    return CafeUtils.getResponseEntity("Email already exist.", HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            log.info("Email: {}", requestMap.get("email"));
            log.info("Password: {}", requestMap.get("password"));

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()){
                User user = customUserDetailService.getUserDetail();
                if (user.getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\" : \"" +
                            jwtUtils.generateToken(user.getEmail(), user.getRole()) + "\"}", HttpStatus.OK);
                }
                else return new ResponseEntity<String>("{\"message\" : \"Wait for admin approval\"}", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<String>("{\"message\" : \"Bad Credentials\"}", HttpStatus.BAD_REQUEST);
    }

    /*==================================================================================================
                                       PRIVATE FUNCTIONS
    ==================================================================================================*/
    private boolean validateSignUpMap(Map<String, String> requestMap){
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                    && requestMap.containsKey("email") && requestMap.containsKey("password"))
        {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
}
