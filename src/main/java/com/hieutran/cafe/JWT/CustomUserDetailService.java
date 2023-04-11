package com.hieutran.cafe.JWT;

import com.hieutran.cafe.DAO.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserDAO userDAO;

    private com.hieutran.cafe.model.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername: {}", username);
        userDetail = userDAO.findByEmail(username);
        if (!Objects.isNull(userDetail)) {
            return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        }
        else{
            log.error("User not found");
            throw new UsernameNotFoundException("User not found.");
        }
    }

    public com.hieutran.cafe.model.User getUserDetail(){
        return userDetail;
    }
}
