package com.trungtamjava.service;

import com.trungtamjava.dto.PageDTO;
import com.trungtamjava.dto.UserDTO;
import com.trungtamjava.entity.User;
import com.trungtamjava.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

    @Transactional
    @CacheEvict(cacheNames = "user-search", allEntries = true)
    public void create(UserDTO userDTO) {
        User user = new ModelMapper().map(userDTO, User.class);
        userRepo.save(user);

        // nếu frontend cần lấy id thì
        userDTO.setId(user.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "user", key = "#userDTO.id"),
            @CacheEvict(cacheNames = "user-search", allEntries = true)
    })
    public void update(UserDTO userDTO) {
        User user = userRepo.findById(userDTO.getId()).orElseThrow(NoResultException::new);
        user.setName(userDTO.getName());
        user.setBirthdate(userDTO.getBirthdate());
        user.setRoles(userDTO.getRoles());
        user.setEmail(userDTO.getEmail());

        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }

        userRepo.save(user);
    }

    // cách 2 dùng modelmapper
    public void update2(UserDTO userDTO) {
        User user = userRepo.findById(userDTO.getId()).orElseThrow(NoResultException::new);

        ModelMapper mapper = new ModelMapper();
        mapper.createTypeMap(UserDTO.class, User.class)
                .addMappings(map -> {
                    map.skip(User::setPassword);
                    if (userDTO.getAvatar() == null) {
                        map.skip(User::setAvatar);
                    }
                })
                .setProvider(p -> user);

        User saveUser = mapper.map(userDTO, User.class);

        userRepo.save(saveUser);
    }

    public void updatePassword(UserDTO userDTO) {
        User user = userRepo.findById(userDTO.getId()).orElseThrow(NoResultException::new);
        user.setPassword(userDTO.getPassword());

        userRepo.save(user);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "user", key = "#id"),
            @CacheEvict(cacheNames = "user-search", allEntries = true)
    })
    public void delete(int id) {
        userRepo.deleteById(id);
    }

    @Cacheable(cacheNames = "user-search")
    public PageDTO<UserDTO> searchByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> pageRS = userRepo.searchByName("%" + name + "%", pageable);

        PageDTO<UserDTO> pageDTO = new PageDTO<>();

        pageDTO.setTotalPages(pageRS.getTotalPages());
        pageDTO.setTotalElements(pageRS.getTotalElements());

        // java 8: lambda, stream
        List<UserDTO> userDTOs = pageRS.get()
                .map(user -> new ModelMapper().map(user, UserDTO.class))
                .collect(Collectors.toList());
        pageDTO.setContents(userDTOs);

        return pageDTO;
    }

    @Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
    public UserDTO getById(int id) {
        User user = userRepo.findById(id).orElseThrow(NoResultException::new);
        return new ModelMapper().map(user, UserDTO.class);
    }

}
