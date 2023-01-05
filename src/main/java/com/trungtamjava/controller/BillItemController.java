package com.trungtamjava.controller;

import com.trungtamjava.dto.BillItemDTO;
import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.dto.ResponseDTO;
import com.trungtamjava.service.BillItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/bill-item")
public class BillItemController {
    @Autowired
    BillItemService billItemService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<BillItemDTO> add(@RequestBody @Valid BillItemDTO billItem) {
        billItemService.create(billItem);
        return ResponseDTO.<BillItemDTO>builder().status(200)
                .data(billItem).build();

    }

    @PutMapping("/")
    public ResponseDTO<Void> update(@RequestBody @Valid BillItemDTO billItem) {
        billItemService.update(billItem);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/{id}") // /10
    public ResponseDTO<BillItemDTO> get(@PathVariable("id") int id) {
        BillItemDTO billItemDTO = billItemService.getById(id);
        return ResponseDTO.<BillItemDTO>builder().status(200)
                .data(billItemDTO).build();
    }

    @DeleteMapping("/{id}") // /1
    public ResponseDTO<Void> delete(@PathVariable("id") int id) {
        billItemService.delete(id);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<BillItemDTO>> search(
            @RequestParam(name = "billItemId", required = false) Integer billItemId,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "page", required = false) Integer page
    ) {

        size = size == null ? 10 : size;
        page = page == null ? 0 : page;
//        billId = billId == null ? "" : billId;

        PageDTO<BillItemDTO> pageRS =
                billItemService.searchById(billItemId, page, size);

        return ResponseDTO.<PageDTO<BillItemDTO>>builder()
                .status(200)
                .data(pageRS).build();
    }
}
