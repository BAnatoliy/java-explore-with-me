package ru.practicum.ewm.services;

import ru.practicum.ewm.models.User;

public interface UserService {
    User getUserOrThrowException(Long userId);
}
