package com.acelerador.polo_it_acelerador.models.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.acelerador.polo_it_acelerador.models.Contact;
import com.acelerador.polo_it_acelerador.models.User;
import com.acelerador.polo_it_acelerador.models.dto.request.UserRequestDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.ContactResponseDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.UserResponseDTO;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(dto.password());

        if (dto.contact() != null) {
            Contact contact = new Contact();
            contact.setName(dto.contact().name());
            contact.setLastname(dto.contact().lastname());
            contact.setEmail(dto.contact().email());
            user.setContact(contact);
        }

        return user;
    }

    public static UserResponseDTO toResponseDTO(User user) {
        ContactResponseDTO contactDTO = null;
        if (user.getContact() != null) {
            contactDTO = new ContactResponseDTO(
                    user.getContact().getName(),
                    user.getContact().getLastname(),
                    user.getContact().getEmail());
        }

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                contactDTO
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
