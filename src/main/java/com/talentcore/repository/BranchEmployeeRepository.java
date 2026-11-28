package com.talentcore.repository;

import com.talentcore.entity.BranchEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchEmployeeRepository extends JpaRepository<BranchEmployee, Long> {

    @Query("SELECT be FROM BranchEmployee be " +
           "JOIN FETCH be.employee emp " +
           "JOIN FETCH be.branch b " +
           "LEFT JOIN FETCH emp.department " +
           "LEFT JOIN FETCH emp.designation " +
           "WHERE be.branch.id = :branchId " +
           "AND (:status IS NULL OR be.isActive = :status) " +
           "AND (:search IS NULL OR LOWER(emp.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(emp.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(emp.employeeCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BranchEmployee> findByBranchWithFilter(@Param("branchId") Long branchId,
                                                @Param("search") String search,
                                                @Param("status") Boolean status,
                                                Pageable pageable);

    @Query("SELECT be FROM BranchEmployee be " +
           "JOIN FETCH be.branch " +
           "WHERE be.employee.id = :employeeId " +
           "ORDER BY be.startDate DESC")
    List<BranchEmployee> findHistoryByEmployeeId(@Param("employeeId") Long employeeId);

    Optional<BranchEmployee> findByEmployeeIdAndIsActiveTrue(Long employeeId);

    List<BranchEmployee> findByBranchIdAndIsActiveTrue(Long branchId);

    long countByBranchIdAndIsActiveTrue(Long branchId);
}
