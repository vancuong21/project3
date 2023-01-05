package com.trungtamjava.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
public class UserDTO {
    private Integer id;
    @NotBlank
    private String name;
    private String avatar; // url
    private String username;
    private String password;
    private String email;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date birthdate;
    @JsonIgnore
    private MultipartFile file;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createdAt;

    // cach 2: tao UserRole
    private List<String> roles; // ADMIN, MEMBER
}
