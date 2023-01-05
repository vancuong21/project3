package com.trungtamjava.service;

import com.trungtamjava.dto.BillItemDTO;
import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.entity.BillItem;
import com.trungtamjava.repo.BillItemRepo;
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
public class BillItemService {
    @Autowired
    BillItemRepo billItemRepo;

    @Transactional
    @CacheEvict(cacheNames = "billItem-search", allEntries = true)
    public void create(BillItemDTO billItemDTO) {
        BillItem billItem = new ModelMapper().map(billItemDTO, BillItem.class);
        billItemRepo.save(billItem);

        // nếu frontend cần lấy id thì
        billItemDTO.setId(billItem.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "billItem", key = "#billItemDTO.id"),
            @CacheEvict(cacheNames = "billItem-search", allEntries = true)
    })
    public void update(BillItemDTO billItemDTO) {
        BillItem billItem = billItemRepo.findById(billItemDTO.getId()).orElseThrow(NoResultException::new);
        billItem.setPrice(billItemDTO.getPrice());
        billItemRepo.save(billItem);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "billItem", key = "#id"),
            @CacheEvict(cacheNames = "billItem-search", allEntries = true)
    })
    public void delete(int id) {
        billItemRepo.deleteById(id);
    }

    @Cacheable(cacheNames = "billItem-search")
    public PageDTO<BillItemDTO> searchById(int billItemId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<BillItem> pageRS = billItemRepo.searchById(billItemId, pageable);

        PageDTO<BillItemDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(pageRS.getTotalPages());
        pageDTO.setTotalElements(pageRS.getTotalElements());
        List<BillItemDTO> billItemDTOs = pageRS.get()
                .map(billItem -> new ModelMapper().map(billItem, BillItemDTO.class))
                .collect(Collectors.toList());
        pageDTO.setContents(billItemDTOs);

        return pageDTO;
    }

    @Cacheable(cacheNames = "billItem", key = "#id", unless = "#result == null")
    public BillItemDTO getById(int id) {
        BillItem billItem = billItemRepo.findById(id).orElseThrow(NoResultException::new);
        return new ModelMapper().map(billItem, BillItemDTO.class);
    }
}
