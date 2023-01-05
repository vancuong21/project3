package com.trungtamjava.controller;

import com.trungtamjava.dto.CategoryDTO;
import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.dto.ResponseDTO;
import com.trungtamjava.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<CategoryDTO> add(@RequestBody @Valid CategoryDTO category) {
        categoryService.create(category);
        return ResponseDTO.<CategoryDTO>builder().status(200)
                .data(category).build();

    }

    @PutMapping("/")
    public ResponseDTO<Void> update(@RequestBody @Valid CategoryDTO category) {
        categoryService.update(category);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/{id}") // /10
    public ResponseDTO<CategoryDTO> get(@PathVariable("id") int id) {
        CategoryDTO categoryDTO = categoryService.getById(id);
        return ResponseDTO.<CategoryDTO>builder().status(200)
                .data(categoryDTO).build();
    }

    @DeleteMapping("/{id}") // /1
    public ResponseDTO<Void> delete(@PathVariable("id") int id) {
        categoryService.delete(id);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<CategoryDTO>> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "page", required = false) Integer page
    ) {

        size = size == null ? 10 : size;
        page = page == null ? 0 : page;
        name = name == null ? "" : name;

        PageDTO<CategoryDTO> pageRS =
                categoryService.searchByName("%" + name + "%", page, size);

        return ResponseDTO.<PageDTO<CategoryDTO>>builder()
                .status(200)
                .data(pageRS).build();
    }


}
