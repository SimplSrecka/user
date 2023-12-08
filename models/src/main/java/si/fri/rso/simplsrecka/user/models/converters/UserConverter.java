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
        dto.setAccountBalance(entity.getAccountBalance());

        return dto;
    }

    public static UserEntity toEntity(User dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setAccountBalance(dto.getAccountBalance());

        return entity;
    }
}
