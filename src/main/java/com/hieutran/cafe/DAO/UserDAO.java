package com.hieutran.cafe.DAO;

import com.hieutran.cafe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
    User findByEmail(String email);

}
