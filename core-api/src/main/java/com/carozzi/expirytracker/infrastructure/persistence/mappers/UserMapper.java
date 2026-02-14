package com.carozzi.expirytracker.infrastructure.persistence.mappers;

import com.carozzi.expirytracker.domain.model.User;
import com.carozzi.expirytracker.infrastructure.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component // Lo marcamos como componente para que Spring lo pueda inyectar
public class UserMapper {

	// De Entidad (Base de Datos) a Dominio (Corazón)
	public User toDomain(UserEntity entity) {
		if (entity == null)
			return null;

		return new User(
				entity.getId(),
				entity.getUsername(),
				entity.getEmail(),
				entity.getPassword());
	}

	// De Dominio (Corazón) a Entidad (Base de Datos)
	public UserEntity toEntity(User domain) {
		if (domain == null)
			return null;

		return UserEntity.builder()
				.id(domain.id())
				.username(domain.username())
				.email(domain.email())
				.password(domain.password())
				.build();
	}
}