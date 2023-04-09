package com.hieutran.cafe.DAO;

import com.hieutran.cafe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {

}
