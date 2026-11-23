package com.talentcore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "branch_code", nullable = false, length = 50)
    private String branchCode;

    @Column(name = "branch_name", nullable = false, length = 150)
    private String branchName;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "contact_number", length = 30)
    private String contactNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
