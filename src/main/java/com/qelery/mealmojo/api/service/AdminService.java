package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.UserNotFoundException;
import com.qelery.mealmojo.api.model.dto.UserDtoOut;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final MapperUtils mapperUtils;

    @Autowired
    public AdminService(UserRepository userRepository, MapperUtils mapperUtils) {
        this.userRepository = userRepository;
        this.mapperUtils = mapperUtils;
    }

    public UserDtoOut changeUserActiveState(Long userId, Boolean active) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException(userId));
        user.setIsActive(active);
        userRepository.save(user);
        return mapperUtils.map(user, UserDtoOut.class);
    }

}
