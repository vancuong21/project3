package com.trungtamjava.repo;

import com.trungtamjava.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillRepo extends JpaRepository<Bill, Integer> {
    @Query("SELECT b FROM Bill b WHERE b.id = :x ")
    Page<Bill> searchById(@Param("x") int s, Pageable pageable);

}
