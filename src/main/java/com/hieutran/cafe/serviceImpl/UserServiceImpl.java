package com.hieutran.cafe.serviceImpl;

import com.hieutran.cafe.DAO.UserDAO;
import com.hieutran.cafe.DTO.UserDTO;
import com.hieutran.cafe.JWT.CustomUserDetailService;
import com.hieutran.cafe.JWT.JwtFilter;
import com.hieutran.cafe.JWT.JwtUtils;
import com.hieutran.cafe.constants.CafeConstants;
import com.hieutran.cafe.model.User;
import com.hieutran.cafe.projection.UserProjection;
import com.hieutran.cafe.service.IUserService;
import com.hieutran.cafe.utils.CafeUtils;
import com.hieutran.cafe.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

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
                    return new ResponseEntity<String>("{\n\"token\" : \"" +
                            jwtUtils.generateToken(user.getEmail(), user.getRole()) + "\"\n}", HttpStatus.OK);
                }
                else return new ResponseEntity<String>("{\"message\" : \"Wait for admin approval\"}", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<String>("{\"message\" : \"Bad Credentials\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserProjection>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDAO.findAllByRole("user"), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                User user =  userDAO.getById(Integer.parseInt(requestMap.get("id")));
                if (user != null){
                    log.info("User present and is User is: {}", user);
                    user.setStatus(requestMap.get("status"));
                    userDAO.save(user);
                    sendMailToAllAdmin(requestMap.get("status"), user.getEmail(), userDAO.getAllAdminEmail());
                    return CafeUtils.getResponseEntity("User update successfully", HttpStatus.OK);
                }
                else {
                    return CafeUtils.getResponseEntity("User id doensn't exist", HttpStatus.OK);

                }
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userDAO.findByEmail(jwtFilter.getCurrentUser());
            if (!user.equals(null)){
                if (user.getPassword().equals(requestMap.get("oldPassword"))){
                    user.setPassword(requestMap.get("newPassword"));
                    userDAO.save(user);
                    return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDAO.findByEmail(requestMap.get("email"));
            if (user != null && !user.getEmail().isEmpty()){
                emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management System", user.getPassword());
            }
            return CafeUtils.getResponseEntity("Check your mail for Credentials.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
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

    private void sendMailToAllAdmin(String status, String userEmail, List<String> allAdminEmail) {
        allAdminEmail.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            log.info("list email admin: {}", allAdminEmail);
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account approved", "USER: " + userEmail + "\n is approved  by \nADMIN: - " + jwtFilter.getCurrentUser(), allAdminEmail);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account disabled", "USER: " + userEmail + "\n is disabled  by \nADMIN: - " + jwtFilter.getCurrentUser(), allAdminEmail);

        }
    }
}
