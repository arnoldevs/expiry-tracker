package com.carozzi.expirytracker.infrastructure.persistence.repositories;

import com.carozzi.expirytracker.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

  // Buscar por nombre de usuario (Spring escribirá el SQL por ti)
  Optional<UserEntity> findByUsername(String username);

  // Verificar si el email ya existe (devuelve true o false)
  boolean existsByEmail(String email);
}
