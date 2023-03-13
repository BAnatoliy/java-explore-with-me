package ru.practicum.ewm.services;

import ru.practicum.ewm.models.User;

public interface UserService {
    User findUserById(Long userId);
}
