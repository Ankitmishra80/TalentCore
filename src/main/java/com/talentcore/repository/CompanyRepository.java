package com.talentcore.repository;

import com.talentcore.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCode(String code);
    boolean existsByCode(String code);

    @Query("SELECT c FROM Company c WHERE " +
           "(:status IS NULL OR c.isActive = :status) AND " +
           "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.shortName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Company> findAllWithFilter(@Param("search") String search,
                                   @Param("status") Boolean status,
                                   Pageable pageable);

    List<Company> findByIsActiveTrue();
}
