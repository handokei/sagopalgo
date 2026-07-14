package org.example.domain.look.domain.repository;

import org.example.domain.look.domain.model.Look;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LookRepository extends JpaRepository<Look, Long> {

    Optional<Look> findFirstByIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc();

    Optional<Look> findByIdAndIsDeletedFalse(Long id);
}
