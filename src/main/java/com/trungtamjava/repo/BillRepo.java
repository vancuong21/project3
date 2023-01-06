package com.trungtamjava.repo;

import com.trungtamjava.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BillRepo extends JpaRepository<Bill, Integer> {
    @Query("SELECT b FROM Bill b WHERE b.id = :x ")
    Page<Bill> searchById(@Param("x") int s, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Bill u WHERE u.buyDate >= :x ")
    void deleteByCreateaAt(@Param("x") Date s);

    @Query("SELECT u FROM Bill u WHERE u.buyDate >= :x ")
    List<Bill> searchByDate(@Param("x") Date s);

    ///Đếm số lượng đơn group by MONTH(buyDate)
    //- dùng custom object để build
    // SELECT id, MONTH(buyDate) from bill;
    // select count(*), MONTH(buyDate) from bill
    // group by MONTH(buyDate)
    @Query("SELECT count(b.id), month(b.buyDate) , year(b.buyDate) "
            + " FROM Bill b GROUP BY month(b.buyDate), year(b.buyDate)")
    List<Object[]> thongKeBill();

//    @Query("SELECT new BillStatisticDTO(count(b.id), month(b.buyDate)) "
//            + " FROM Bill b GROUP BY month(b.buyDate) ")
//    List<BillStatisticDTO> thongKeBill2();
}
