package com.talentcore.repository;

import com.talentcore.entity.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {

    @Query("SELECT d FROM Designation d JOIN FETCH d.department dept JOIN FETCH dept.company WHERE " +
           "(:departmentId IS NULL OR d.department.id = :departmentId) AND " +
           "(:companyId IS NULL OR d.department.company.id = :companyId) AND " +
           "(:status IS NULL OR d.isActive = :status) AND " +
           "(:search IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Designation> findAllWithFilter(@Param("search") String search,
                                        @Param("companyId") Long companyId,
                                        @Param("departmentId") Long departmentId,
                                        @Param("status") Boolean status,
                                        Pageable pageable);

    @Query("SELECT d FROM Designation d JOIN FETCH d.department dept JOIN FETCH dept.company WHERE d.id = :id")
    Optional<Designation> findByIdWithDetails(@Param("id") Long id);

    boolean existsByDepartmentIdAndNameIgnoreCase(Long departmentId, String name);

    List<Designation> findByDepartmentIdAndIsActiveTrue(Long departmentId);
}
