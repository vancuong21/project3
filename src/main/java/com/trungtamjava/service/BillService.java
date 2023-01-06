package com.trungtamjava.service;

import com.trungtamjava.dto.BillDTO;
import com.trungtamjava.dto.BillItemDTO;
import com.trungtamjava.dto.BillStatisticDTO;
import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.entity.Bill;
import com.trungtamjava.entity.BillItem;
import com.trungtamjava.entity.User;
import com.trungtamjava.repo.BillItemRepo;
import com.trungtamjava.repo.BillRepo;
import com.trungtamjava.repo.ProductRepo;
import com.trungtamjava.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillService {
    // cach 2 : ko dung Jparepo... Tạo 1 class repo riêng viết vào, cách này linh động
    @PersistenceContext
    EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Bill> searchByDate(@Param("x") Date s) {
        String jpql = "SELECT u FROM Bill u WHERE " + "u.buyDate >= :x "; // createdAt = buyDate

        return entityManager.createQuery(jpql)
                .setParameter("x", s)
                .setMaxResults(10) // so ket qua
                .setFirstResult(0) // ...
                .getResultList();
    }

    @SuppressWarnings("un")

    // cach 1:
    @Autowired
    BillRepo billRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    private BillItemRepo billItemRepo;

    /**
     * khi tạo mới Bill thì tạo luôn BillItem
     */
    @Transactional
    @CacheEvict(cacheNames = "bill-search", allEntries = true)
    public void create(BillDTO billDTO) {
        User user = userRepo.findById(billDTO.getUser().getId()).orElseThrow(NoResultException::new);
        Bill bill = new Bill();
        bill.setUser(user);

        List<BillItem> billItems = new ArrayList<>();
        for (BillItemDTO billItemDTO : billDTO.getBillItems()) {
            BillItem billItem = new BillItem();
            billItem.setBill(bill);
            billItem.setProduct(productRepo.findById(billItemDTO.getProduct().getId())
                    .orElseThrow(NoResultException::new));
            billItem.setPrice(billItemDTO.getPrice());
            billItem.setQuantity(billItemDTO.getQuantity());

            billItems.add(billItem);
        }
        bill.setBillItems(billItems);
        billRepo.save(bill);

    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "bill", key = "#billDTO.id"),
            @CacheEvict(cacheNames = "bill-search", allEntries = true)
    })
    public void update(BillDTO billDTO) {
        User user = userRepo.findById(billDTO.getUser().getId()).orElseThrow(NoResultException::new);
        Bill bill = billRepo.findById(billDTO.getId()).orElseThrow(NoResultException::new);
//        bill.getBillItems().clear(); // xoá hêt BillItem trong db
//        for (BillItemDTO billItemDTO : billDTO.getBillItems()) {
//            BillItem billItem = new BillItem();
//            billItem.setBill(bill);
//            billItem.setProduct(productRepo.findById(billItemDTO.getProduct().getId())
//                    .orElseThrow(NoResultException::new));
//            billItem.setPrice(billItemDTO.getPrice());
//            billItem.setQuantity(billItemDTO.getQuantity());
//
//            bill.getBillItems().add(billItem);
//        }
        bill.setUser(user);

        billRepo.save(bill);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "bill", key = "#id"),
            @CacheEvict(cacheNames = "bill-search", allEntries = true)
    })
    public void delete(int id) {
        billRepo.deleteById(id);
    }

    @Cacheable(cacheNames = "bill-search")
    public PageDTO<BillDTO> searchById(int billId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Bill> pageRS = billRepo.searchById(billId, pageable);

        PageDTO<BillDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(pageRS.getTotalPages());
        pageDTO.setTotalElements(pageRS.getTotalElements());
        List<BillDTO> billDTOs = pageRS.get()
                .map(bill -> new ModelMapper().map(bill, BillDTO.class))
                .collect(Collectors.toList());
        pageDTO.setContents(billDTOs);

        return pageDTO;
    }

    @Cacheable(cacheNames = "bill", key = "#id", unless = "#result == null")
    public BillDTO getById(int id) {
        Bill bill = billRepo.findById(id).orElseThrow(NoResultException::new);
        return new ModelMapper().map(bill, BillDTO.class);
    }

    // Thống Kê
    public PageDTO<BillStatisticDTO> statistic() {
        List<Object[]> list = billRepo.thongKeBill();
        PageDTO<BillStatisticDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(1);
        pageDTO.setTotalElements(list.size());

        List<BillStatisticDTO> billStatisticDTOs = new ArrayList<>();
        for (Object[] array : list) {

            BillStatisticDTO billStatisticDTO =
                    new BillStatisticDTO((long) (array[0]), array[1] + "/" + array[2]);
            billStatisticDTOs.add(billStatisticDTO);
        }
        pageDTO.setContents(billStatisticDTOs);
        return pageDTO;
    }

}
