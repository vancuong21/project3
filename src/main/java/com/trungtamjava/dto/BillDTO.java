package com.trungtamjava.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BillDTO {
    private Integer id;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private Date buyDate;
    private UserDTO user;
    @JsonManagedReference
    private List<BillItemDTO> billItems;
}
