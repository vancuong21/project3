package com.trungtamjava.service;

import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.dto.ProductDTO;
import com.trungtamjava.entity.Category;
import com.trungtamjava.entity.Product;
import com.trungtamjava.repo.CategoryRepo;
import com.trungtamjava.repo.ProductRepo;
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
public class ProductService {
    @Autowired
    ProductRepo productRepo;
    @Autowired
    CategoryRepo categoryRepo;

    @Transactional
    @CacheEvict(cacheNames = "product-search", allEntries = true)
    public void create(ProductDTO productDTO) {
        Category category = categoryRepo.findById(productDTO.getCategory().getId()).orElseThrow(NoResultException::new);

        Product product = new ModelMapper().map(productDTO, Product.class);
        //  product.setCategory(category);
        productRepo.save(product);

        // nếu frontend cần lấy id thì
        productDTO.setId(product.getId());

    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "product", key = "#productDTO.id"),
            @CacheEvict(cacheNames = "product-search", allEntries = true)
    })
    public void update(ProductDTO productDTO) {
        Product product = productRepo.findById(productDTO.getId()).orElseThrow(NoResultException::new);
        product.setName(productDTO.getName());
        if (productDTO.getImage() != null) {
            product.setImage(productDTO.getImage());
        }
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        Category category = categoryRepo.findById(productDTO.getCategory().getId()).orElseThrow(NoResultException::new);
        product.setCategory(category);

        productRepo.save(product);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "product", key = "#id"),
            @CacheEvict(cacheNames = "product-search", allEntries = true)
    })
    public void delete(int id) {
        productRepo.deleteById(id);
    }

    @Cacheable(cacheNames = "product-search")
    public PageDTO<ProductDTO> searchByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> pageRS = productRepo.searchByName("%" + name + "%", pageable);

        PageDTO<ProductDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(pageRS.getTotalPages());
        pageDTO.setTotalElements(pageRS.getTotalElements());
        List<ProductDTO> productDTOs = pageRS.get()
                .map(product -> new ModelMapper().map(product, ProductDTO.class))
                .collect(Collectors.toList());
        pageDTO.setContents(productDTOs);

        return pageDTO;
    }

    @Cacheable(cacheNames = "product", key = "#id", unless = "#result == null")
    public ProductDTO getById(int id) {
        Product product = productRepo.findById(id).orElseThrow(NoResultException::new);
        return new ModelMapper().map(product, ProductDTO.class);
    }

}
