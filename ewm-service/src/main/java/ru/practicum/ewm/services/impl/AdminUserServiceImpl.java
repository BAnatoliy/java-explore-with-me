package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.NewUserRequest;
import ru.practicum.ewm.dtos.UserDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.models.User;
import ru.practicum.ewm.repositories.UserRepository;
import ru.practicum.ewm.services.AdminUserService;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AdminUserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users = userRepository.getUserByParameters(ids, from, size);
        log.debug("Get users list");
        return userMapper.mapToUserDtoList(users);
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = userMapper.mapToUser(newUserRequest);
        User savedUser = userRepository.save(user);
        log.debug("User is saved");
        return userMapper.mapToUserDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id=%s was not found", userId)));
        userRepository.deleteById(userId);
        log.debug("User with ID = {} is deleted", userId);
    }
}
