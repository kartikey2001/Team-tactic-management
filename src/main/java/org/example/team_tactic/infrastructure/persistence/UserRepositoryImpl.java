package org.example.team_tactic.infrastructure.persistence;

import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.User;
import org.example.team_tactic.infrastructure.persistence.entity.UserEntity;
import org.example.team_tactic.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private User toDomain(UserEntity e) {
        User u = new User(e.getId(), e.getEmail(), e.getPasswordHash(), e.getDisplayName(), e.getCreatedAt());
        return u;
    }

    private UserEntity toEntity(User u) {
        UserEntity e = new UserEntity();
        if (u.getId() != null) {
            e.setId(u.getId());
        }
        e.setEmail(u.getEmail());
        e.setPasswordHash(u.getPasswordHash());
        e.setDisplayName(u.getDisplayName());
        e.setCreatedAt(u.getCreatedAt());
        return e;
    }
}
