package com.trungtamjava.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class BillItemDTO {
    private Integer id;
    @JsonBackReference
    private BillDTO billDTO;
    private ProductDTO product;
    @Min(0)
    private int quantity;
    @Min(0)
    private double price;

}
