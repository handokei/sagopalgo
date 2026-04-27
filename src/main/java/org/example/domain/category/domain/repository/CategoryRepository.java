package org.example.domain.category.domain.repository;

import org.example.domain.category.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndIsDeletedFalse(Long id);

    List<Category> findAllByIsDeletedFalseOrderByNameAsc();

    boolean existsByNameAndIsDeletedFalse(String name);

    Optional<Category> findByNameAndIsDeletedFalse(String name);
}
