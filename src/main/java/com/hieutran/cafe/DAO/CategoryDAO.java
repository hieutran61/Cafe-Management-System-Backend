package com.hieutran.cafe.DAO;

import com.hieutran.cafe.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();
}
