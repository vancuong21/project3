package com.trungtamjava.service;

import com.trungtamjava.dto.BillDTO;
import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.entity.Bill;
import com.trungtamjava.repo.BillRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillService {
    @Autowired
    BillRepo billRepo;

    @Transactional
    @CacheEvict(cacheNames = "bill-search", allEntries = true)
    public void create(BillDTO billDTO) {
        Bill bill = new ModelMapper().map(billDTO, Bill.class);
        billRepo.save(bill);

        // nếu frontend cần lấy id thì
        billDTO.setId(bill.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "bill", key = "#billDTO.id"),
            @CacheEvict(cacheNames = "bill-search", allEntries = true)
    })
    public void update(BillDTO billDTO) {
        Bill bill = billRepo.findById(billDTO.getId()).orElseThrow(NoResultException::new);
        bill.setBuyDate(billDTO.getBuyDate());
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

}
