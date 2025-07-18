package com.example.PTW.RFI.Entity;

import jakarta.persistence.*;     // JPA 3.1+ (Jakarta EE)
import lombok.*;

/**
 * Maps to table SubContractorMaster
 */
@Entity
@Table(name = "SubContractorMaster")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubContractorMaster {

    /** Primary key: decimal(18,0) → Java Long */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCID", precision = 18, scale = 0, nullable = false)
    private Long scid;

    /** varchar(50) NOT NULL */
    @Column(name = "SubContractorCode", length = 50, nullable = false)
    private String subContractorCode;

    /** varchar(200) */
    @Column(name = "FullName", length = 200)
    private String fullName;

    /** varchar(100) */
    @Column(name = "FirstName", length = 100)
    private String firstName;

    /** varchar(100) */
    @Column(name = "MiddleName", length = 100)
    private String middleName;

    /** varchar(100) */
    @Column(name = "LastName", length = 100)
    private String lastName;

    /** varchar(100) */
    @Column(name = "DocStatus", length = 100)
    private String docStatus;

    /** varchar(100) */
    @Column(name = "PendingWith", length = 100)
    private String pendingWith;
}
