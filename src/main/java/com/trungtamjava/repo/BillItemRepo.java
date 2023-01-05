package com.trungtamjava.repo;

import com.trungtamjava.entity.BillItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillItemRepo extends JpaRepository<BillItem, Integer> {
    @Query("SELECT bi FROM BillItem bi WHERE bi.id = :x ")
    Page<BillItem> searchById(@Param("x") int s, Pageable pageable);


}
