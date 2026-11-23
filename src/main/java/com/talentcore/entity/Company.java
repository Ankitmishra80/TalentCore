package com.talentcore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "short_name", length = 50)
    private String shortName;

    @Column(name = "contact_person_name", length = 100)
    private String contactPersonName;

    @Column(name = "contact_person_email", length = 100)
    private String contactPersonEmail;

    @Column(name = "contact_person_phone", length = 20)
    private String contactPersonPhone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "gst_no", length = 50)
    private String gstNo;

    @Column(name = "pan_no", length = 50)
    private String panNo;

    @Column(name = "cin_no", length = 50)
    private String cinNo;

    @Column(name = "logo")
    private String logo;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Branch> branches = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Department> departments = new ArrayList<>();
}
