package com.trungtamjava.controller;

import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.dto.ProductDTO;
import com.trungtamjava.dto.ResponseDTO;
import com.trungtamjava.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    // Nghiên cứu GRPC, GRAPHQL
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<ProductDTO> add(@ModelAttribute @Valid ProductDTO product) throws IOException {
        if (product.getFile() != null && !product.getFile().isEmpty()) {
            final String UPLOAD_FOLDER_PRODUCT = "C:/Users/cuong/Downloads/project3/product/";
            if (!(new File(UPLOAD_FOLDER_PRODUCT).exists())) {
                new File(UPLOAD_FOLDER_PRODUCT).mkdirs();
            }

            String filename = product.getFile().getOriginalFilename();
            // lay duoi file
            String extension = filename.substring(filename.lastIndexOf("."));
            // tao ten moi
            String newFilename = UUID.randomUUID() + extension;

            File newFile = new File(UPLOAD_FOLDER_PRODUCT + newFilename);

            product.getFile().transferTo(newFile);
            product.setImage(filename); // save url to db
        }

        productService.create(product);
        return ResponseDTO.<ProductDTO>builder().status(200)
                .data(product).build();

    }

    @PutMapping("/")
    public ResponseDTO<Void> update(@ModelAttribute @Valid ProductDTO product) throws IOException {
        if (!product.getFile().isEmpty()) {
            final String UPLOAD_FOLDER_PRODUCT = "C:/Users/cuong/Downloads/project3/product/";
            String filename = product.getFile().getOriginalFilename();
            // lay duoi file
            String extension = filename.substring(filename.lastIndexOf("."));
            // tao ten moi
            String newFilename = UUID.randomUUID() + extension;

            File newFile = new File(UPLOAD_FOLDER_PRODUCT + newFilename);

            product.getFile().transferTo(newFile);
            product.setImage(filename); // save url to db
        }

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

