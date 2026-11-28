package com.talentcore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "branch_selections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Builder.Default
    @Column(name = "selected_at", nullable = false)
    private LocalDateTime selectedAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
