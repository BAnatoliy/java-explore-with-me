package ru.practicum.ewm.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.ewm.dtos.NewUserRequest;
import ru.practicum.ewm.dtos.UserDto;
import ru.practicum.ewm.models.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    User mapToUser(NewUserRequest newUserRequest);

    UserDto mapToUserDto(User user);

    List<UserDto> mapToUserDtoList(List<User> users);
}
