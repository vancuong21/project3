package com.trungtamjava.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    private int status;
    private String msg;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
}
