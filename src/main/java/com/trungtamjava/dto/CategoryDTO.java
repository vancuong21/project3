package com.trungtamjava.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryDTO {
    private Integer id;
    @NotBlank
    @Size(min = 6, max = 20)
    private String name;

}
