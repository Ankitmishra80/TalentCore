package com.talentcore.repository;

import com.talentcore.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e " +
           "LEFT JOIN FETCH e.company " +
           "LEFT JOIN FETCH e.department " +
           "LEFT JOIN FETCH e.designation " +
           "LEFT JOIN FETCH e.reportingManager " +
           "WHERE (:companyId IS NULL OR e.company.id = :companyId) " +
           "AND (:departmentId IS NULL OR e.department.id = :departmentId) " +
           "AND (:designationId IS NULL OR e.designation.id = :designationId) " +
           "AND (:status IS NULL OR e.status = :status) " +
           "AND (:search IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.officialEmail) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Employee> findAllWithFilter(@Param("search") String search,
                                     @Param("companyId") Long companyId,
                                     @Param("departmentId") Long departmentId,
                                     @Param("designationId") Long designationId,
                                     @Param("status") String status,
                                     Pageable pageable);

    @Query("SELECT e FROM Employee e " +
           "LEFT JOIN FETCH e.company " +
           "LEFT JOIN FETCH e.department " +
           "LEFT JOIN FETCH e.designation " +
           "LEFT JOIN FETCH e.reportingManager " +
           "WHERE e.id = :id")
    Optional<Employee> findByIdWithDetails(@Param("id") Long id);

    Optional<Employee> findByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCode(String employeeCode);
    Optional<Employee> findByUserId(Long userId);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    List<Employee> findByCompanyIdAndStatus(Long companyId, String status);

    long countByDepartmentId(Long departmentId);
}
