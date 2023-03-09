package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.NewUserRequest;
import ru.practicum.ewm.dtos.UserDto;

import java.util.List;

public interface AdminUserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
