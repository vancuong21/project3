package com.trungtamjava.controller;

import com.trungtamjava.dto.BillDTO;
import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.dto.ResponseDTO;
import com.trungtamjava.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/bill")
public class BillController {
    @Autowired
    BillService billService;


    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<BillDTO> add(@RequestBody @Valid BillDTO bill) {
        billService.create(bill);
        return ResponseDTO.<BillDTO>builder().status(200)
                .data(bill).build();

    }

    @PutMapping("/")
    public ResponseDTO<Void> update(@RequestBody @Valid BillDTO bill) {
        billService.update(bill);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/{id}") // /10
    public ResponseDTO<BillDTO> get(@PathVariable("id") int id) {
        BillDTO billDTO = billService.getById(id);
        return ResponseDTO.<BillDTO>builder().status(200)
                .data(billDTO).build();
    }

    @DeleteMapping("/{id}") // /1
    public ResponseDTO<Void> delete(@PathVariable("id") int id) {
        billService.delete(id);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<BillDTO>> search(
            @RequestParam(name = "billId", required = false) Integer billId,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "page", required = false) Integer page
    ) {

        size = size == null ? 10 : size;
        page = page == null ? 0 : page;
//        billId = billId == null ? "" : billId;

        PageDTO<BillDTO> pageRS =
                billService.searchById(billId, page, size);

        return ResponseDTO.<PageDTO<BillDTO>>builder()
                .status(200)
                .data(pageRS).build();
    }

}
