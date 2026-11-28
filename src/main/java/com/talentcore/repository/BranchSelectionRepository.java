package com.talentcore.repository;

import com.talentcore.entity.BranchSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchSelectionRepository extends JpaRepository<BranchSelection, Long> {

    @Query("SELECT bs FROM BranchSelection bs " +
           "JOIN FETCH bs.branch b " +
           "JOIN FETCH b.company " +
           "WHERE bs.user.id = :userId AND bs.isActive = true")
    Optional<BranchSelection> findActiveSelectionByUser(@Param("userId") Long userId);

    Optional<BranchSelection> findByUserIdAndBranchId(Long userId, Long branchId);
}
