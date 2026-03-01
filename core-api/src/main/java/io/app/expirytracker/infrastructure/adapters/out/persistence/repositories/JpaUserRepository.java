package io.app.expirytracker.infrastructure.adapters.out.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.app.expirytracker.infrastructure.adapters.out.persistence.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

  // Buscar por nombre de usuario (Spring escribirá el SQL por ti)
  Optional<UserEntity> findByUsername(String username);

  // Verificar si el email ya existe (devuelve true o false)
  boolean existsByEmail(String email);
}
