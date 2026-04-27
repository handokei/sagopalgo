package org.example.domain.user.domain.repository;

import org.example.domain.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAndIsDeletedFalse(String email);


    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    Optional<User> findByProviderAndProviderIdAndIsDeletedFalse(String provider, String providerId);
}
