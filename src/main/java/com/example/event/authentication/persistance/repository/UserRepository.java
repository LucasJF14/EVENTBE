package com.example.event.authentication.persistance.repository;

import com.example.event.authentication.persistance.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    public Optional<UserEntity> findByUsername(String username);
}
