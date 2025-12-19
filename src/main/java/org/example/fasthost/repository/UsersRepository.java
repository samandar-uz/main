package org.example.fasthost.repository;

import org.example.fasthost.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByKey(String key);
    boolean existsByEmail(String email);

    boolean existsByTgIdAndIdNot(Long tgId, Integer id);

    Optional<Users> findByTgId(Long tgId);
    Optional<Users> findByEmail(String email);
}