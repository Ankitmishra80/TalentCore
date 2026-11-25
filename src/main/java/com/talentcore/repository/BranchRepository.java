package com.talentcore.repository;

import com.talentcore.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query("SELECT b FROM Branch b JOIN FETCH b.company WHERE " +
           "(:companyId IS NULL OR b.company.id = :companyId) AND " +
           "(:status IS NULL OR b.isActive = :status) AND " +
           "(:search IS NULL OR LOWER(b.branchName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.branchCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.city) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Branch> findAllWithFilter(@Param("search") String search,
                                  @Param("companyId") Long companyId,
                                  @Param("status") Boolean status,
                                  Pageable pageable);

    @Query("SELECT b FROM Branch b JOIN FETCH b.company WHERE b.id = :id")
    Optional<Branch> findByIdWithCompany(@Param("id") Long id);

    boolean existsByCompanyIdAndBranchCode(Long companyId, String branchCode);

    List<Branch> findByCompanyIdAndIsActiveTrue(Long companyId);

    List<Branch> findByIsActiveTrue();
}
