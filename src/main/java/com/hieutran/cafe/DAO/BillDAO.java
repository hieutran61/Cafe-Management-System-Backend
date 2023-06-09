package com.hieutran.cafe.DAO;

import com.hieutran.cafe.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDAO extends JpaRepository<Bill, Integer> {

    List<Bill> findByCreatebyOrderByIdDesc(String username);

}
