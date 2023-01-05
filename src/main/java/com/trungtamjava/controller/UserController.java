package com.trungtamjava.controller;

import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.dto.ResponseDTO;
import com.trungtamjava.dto.UserDTO;
import com.trungtamjava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<UserDTO> add(@ModelAttribute @Valid UserDTO user) throws IOException { // ModelAttribute: dùng upload file
        if (!user.getFile().isEmpty()) {
            final String UPLOAD_FOLDER = "C:/Users/cuong/Downloads/file2/";

            String filename = user.getFile().getOriginalFilename();
            // lay dinh dang file
            String extension = filename.substring(filename.lastIndexOf(".")); // lay duoi file
            // tao ten moi
            String newFilename = UUID.randomUUID().toString() + extension;

            File newFile = new File(UPLOAD_FOLDER + newFilename);

            user.getFile().transferTo(newFile);
            user.setAvatar(filename); // save to db
        }
        // bao mat password

        userService.create(user);
        return ResponseDTO.<UserDTO>builder().status(200)
                .data(user).build();

    }

    @PutMapping("/")
    public ResponseDTO<Void> update(@ModelAttribute @Valid UserDTO user) throws IOException {
        if (!user.getFile().isEmpty()) {
            final String UPLOAD_FOLDER = "C:/Users/cuong/Downloads/file2/";

            String filename = user.getFile().getOriginalFilename();
            // lay dinh dang file
            String extension = filename.substring(filename.lastIndexOf(".")); // lay duoi file
            // tao ten moi
            String newFilename = UUID.randomUUID().toString() + extension;

            File newFile = new File(UPLOAD_FOLDER + newFilename);

            user.getFile().transferTo(newFile);
            user.setAvatar(filename); // save to db
        }
        // bao mat password

        userService.update(user);
        return ResponseDTO.<Void>builder().status(200).build();
    }
    @PutMapping("/password")
    public ResponseDTO<Void> updatePassword(@RequestBody @Valid UserDTO user) {
        userService.updatePassword(user);
        // bao mat password

        userService.update(user);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/{id}") // /10
    public ResponseDTO<UserDTO> get(@PathVariable("id") int id) {
        UserDTO userDTO = userService.getById(id);
        return ResponseDTO.<UserDTO>builder().status(200)
                .data(userDTO).build();
    }

    @DeleteMapping("/{id}") // /1
    public ResponseDTO<Void> delete(@PathVariable("id") int id) {
        userService.delete(id);
        return ResponseDTO.<Void>builder().status(200).build();
    }

    @GetMapping("/download/{filename}")
    public void download(@PathVariable("filename") String filename,
                         HttpServletResponse response) throws IOException {
        final String UPLOAD_FOLDER = "C:/Users/cuong/Downloads/file2/";
        File file = new File(UPLOAD_FOLDER + filename);
        // copy file vaof response de dowload
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @GetMapping("/search")
    public ResponseDTO<PageDTO<UserDTO>> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "start", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") Date start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") Date end,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "page", required = false) Integer page
    ) {

        size = size == null ? 10 : size;
        page = page == null ? 0 : page;
        name = name == null ? "" : name;

        PageDTO<UserDTO> pageRS = userService.searchByName("%" + name + "%", page, size);

        return ResponseDTO.<PageDTO<UserDTO>>builder()
                .status(200)
                .data(pageRS).build();
    }

}
