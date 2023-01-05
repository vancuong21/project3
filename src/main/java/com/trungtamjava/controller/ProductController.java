package com.trungtamjava.controller;

import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.dto.ProductDTO;
import com.trungtamjava.dto.ResponseDTO;
import com.trungtamjava.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<ProductDTO> add(@RequestBody @Valid ProductDTO product) {
        productService.create(product);
        return ResponseDTO.<ProductDTO>builder().status(200)
                .data(product).build();

    }

    @PutMapping("/")
    public ResponseDTO<Void> update(@RequestBody @Valid ProductDTO product) {
        productService.update(product);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/{id}") // /10
    public ResponseDTO<ProductDTO> get(@PathVariable("id") int id) {
        ProductDTO productDTO = productService.getById(id);
        return ResponseDTO.<ProductDTO>builder().status(200)
                .data(productDTO).build();
    }

    @DeleteMapping("/{id}") // /1
    public ResponseDTO<Void> delete(@PathVariable("id") int id) {
        productService.delete(id);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<ProductDTO>> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "page", required = false) Integer page
    ) {

        size = size == null ? 10 : size;
        page = page == null ? 0 : page;
        name = name == null ? "" : name;

        PageDTO<ProductDTO> pageRS =
                productService.searchByName("%" + name + "%", page, size);

        return ResponseDTO.<PageDTO<ProductDTO>>builder()
                .status(200)
                .data(pageRS).build();
    }
}

