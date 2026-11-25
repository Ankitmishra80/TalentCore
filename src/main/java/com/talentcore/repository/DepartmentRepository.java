package com.talentcore.repository;

import com.talentcore.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT d FROM Department d JOIN FETCH d.company WHERE " +
           "(:companyId IS NULL OR d.company.id = :companyId) AND " +
           "(:status IS NULL OR d.isActive = :status) AND " +
           "(:search IS NULL OR LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.departmentCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Department> findAllWithFilter(@Param("search") String search,
                                       @Param("companyId") Long companyId,
                                       @Param("status") Boolean status,
                                       Pageable pageable);

    @Query("SELECT d FROM Department d JOIN FETCH d.company WHERE d.id = :id")
    Optional<Department> findByIdWithCompany(@Param("id") Long id);

    boolean existsByCompanyIdAndDepartmentNameIgnoreCase(Long companyId, String departmentName);

    List<Department> findByCompanyIdAndIsActiveTrue(Long companyId);
}
