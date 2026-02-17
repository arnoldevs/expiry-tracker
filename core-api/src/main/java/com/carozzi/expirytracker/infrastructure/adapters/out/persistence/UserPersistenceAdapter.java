package com.carozzi.expirytracker.infrastructure.adapters.out.persistence;

import com.carozzi.expirytracker.application.ports.out.UserRepositoryPort;
import com.carozzi.expirytracker.domain.model.User;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.mappers.UserMapper;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.repositories.JpaUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component // Spring lo reconoce como un frijol (bean) para usarlo luego.
@RequiredArgsConstructor // Lombok: crea el constructor con el Repositorio y el Mapper.
public class UserPersistenceAdapter implements UserRepositoryPort { // ¡EL CONTRATO!

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        // Traducimos: Dominio -> Entidad
        var entity = userMapper.toEntity(user);

        // Guardamos en Postgres usando Spring Data
        var savedEntity = jpaUserRepository.save(entity);

        // Traducimos de vuelta: Entidad -> Dominio
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        // Buscamos en la DB y el resultado lo pasamos por el Mapper usando ::
        return jpaUserRepository.findById(id)
                .map(userMapper::toDomain); // <-- ¡Aquí usamos la referencia a método!
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        // Aquí no hace falta Mapper porque un boolean es igual en todos lados
        return jpaUserRepository.existsByEmail(email);
    }
}