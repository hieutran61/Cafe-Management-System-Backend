package com.hieutran.cafe.DAO;

import com.hieutran.cafe.DTO.UserDTO;
import com.hieutran.cafe.model.User;
import com.hieutran.cafe.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    List<UserProjection> findAllByRole(String role);

    List<String> getAllAdminEmail();
}
