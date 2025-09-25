package com.acelerador.polo_it_acelerador.models.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.acelerador.polo_it_acelerador.models.User;
import com.acelerador.polo_it_acelerador.models.dto.request.UserRequestDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.UserResponseDTO;

public class UserMapper {

    // Convierte DTO de request -> Entidad
    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setLastname(dto.lastname());
        return user;
    }

    // Convierte Entidad -> DTO de response
    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Lista de entidades -> lista de responseDTO
    public static List<UserResponseDTO> toResponseDTOList(List<User> users) {
        return users.stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Lista de requestDTO -> lista de entidades
    public static List<User> toEntityList(List<UserRequestDTO> userDtos) {
        return userDtos.stream()
                .map(UserMapper::toEntity)
                .collect(Collectors.toList());
    }
}
