package si.fri.rso.simplsrecka.user.models.converters;

import si.fri.rso.simplsrecka.user.models.entities.UserEntity;
import si.fri.rso.simplsrecka.user.lib.User;

public class UserConverter {

    public static User toDto(UserEntity entity) {
        User dto = new User();
        dto.setUserId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setAddress(entity.getAddress());
        dto.setPostCode(entity.getPostCode());
        dto.setAccountBalance(entity.getAccountBalance());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLastLogin(entity.getLastLogin());

        return dto;
    }

    public static UserEntity toEntity(User dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setAddress(dto.getAddress());
        entity.setPostCode(dto.getPostCode());
        entity.setAccountBalance(dto.getAccountBalance());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setLastLogin(dto.getLastLogin());

        return entity;
    }
}
