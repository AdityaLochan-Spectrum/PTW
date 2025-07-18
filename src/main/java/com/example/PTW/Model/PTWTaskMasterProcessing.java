package com.example.PTW.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "PTWTaskMasterProcessing")
@Data                       // getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PTWTaskMasterProcessing {

    /* ——————— Keys ——————— */

    /** Primary key — running serial number for each remark row */
    @EmbeddedId
    private PTWTaskMasterProcessingId id;

    /** FK → PTW header; swap to simple Long if you don't have the parent entity */
    @MapsId("ptwId")                               // links PK field to FK column
    @ManyToOne(optional = false)
    @JoinColumn(name = "PTWId", nullable = false)
    private PTWTaskMaster ptwTaskMaster;  // or: private Long ptwId;

    /* ——————— Core fields ——————— */
    @CreationTimestamp
    @Column(name = "RemarkDate")
    private LocalDate remarkDate;          // store just the date; use LocalDateTime if time also matters

    
    @Column(name = "Remarks", length = 4000)   // raise length or mark @Lob if it can be huge
    private String remarks;

    @Column(name = "RemarksBy", length = 100)
    private String remarksBy;

    @Column(name = "Approved")
    private String approved;              // true = yes, false = no, null = not decided

    @Column(name = "ImgString",length = 8000)
    private String imgString;

    @Column(name = "Process", length = 100)
    private String process;
}
