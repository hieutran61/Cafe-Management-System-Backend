package com.hieutran.cafe.DAO;

import com.hieutran.cafe.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillDAO extends JpaRepository<Bill, Integer> {
}
