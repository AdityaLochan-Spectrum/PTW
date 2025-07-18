package com.example.PTW.RFI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "RFITransaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RFITransaction {

    /* ---------- Primary key ---------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     // remove if DB auto-increments differently
    @Column(name = "RFIID")
    private Long rfiId;

    /* ---------- Simple columns ---------- */
    @Column(name = "RFINo")
    private Long rfiNo;

    @Column(name = "RequestForInspection", length = 100)
    private String requestForInspection;

    @Column(name = "RFIPass", length = 100)
    private String rfiPass;

    @Column(name = "SubContractorName", length = 200)
    private String subContractorName;

    @CreationTimestamp
    @Column(name = "OfferedDate")
    private LocalDateTime offeredDate;

    @Column(name = "DetailsOfWork", length = 200)
    private String detailsOfWork;

    @Column(name = "LocationArea", length = 500)
    private String locationArea;

    @Column(name = "ConstructionA", length = 300)
    private String constructionA;

    @Column(name = "OfferWork", length = 150)
    private String offerWork;

    @Column(name = "Released", length = 50)
    private String released;

    @Column(name = "USEAsIS", length = 50)
    private String useAsIs;

    @Column(name = "ReworkReject", length = 50)
    private String reworkReject;

    @Column(name = "HoldRFIRelNo", length = 50)
    private String holdRfiRelNo;

    @Column(name = "OfferedTime", length = 50)
    private String offeredTime;           // kept as String because the DB column is varchar

    @Column(name = "SiteReachingTime")
    private LocalDateTime siteReachingTime;

    @Column(name = "InspectionStartTime")
    private LocalDateTime inspectionStartTime;

    @Column(name = "InspectionEndTime")
    private LocalDateTime inspectionEndTime;

    @Column(name = "ObservedDefectStatement", length = 300)
    private String observedDefectStatement;

    @Column(name = "NextStep", length = 300)
    private String nextStep;

    @Column(name = "ActionTaken", length = 300)
    private String actionTaken;

    @Column(name = "RESP", length = 300)
    private String resp;

    @Column(name = "TimeLine", length = 100)
    private String timeLine;

    @Column(name = "Remarks", length = 300)
    private String remarks;

    /* ---------- Extra L-fields ---------- */
    @Column(name = "ExtraL1", length = 100)
    private String extraL1;

    @Column(name = "ExtraL2", length = 100)
    private String extraL2;

    @Column(name = "ExtraL3", length = 100)
    private String extraL3;

    @Column(name = "ExtraL4", length = 100)
    private String extraL4;

    @Column(name = "ExtraL5", length = 100)
    private String extraL5;

    @Column(name = "ExtraL6", length = 100)
    private String extraL6;

    @Column(name = "ExtraL7", length = 100)
    private String extraL7;

    /* ---------- Workflow status ---------- */
    @Column(name = "DocStatus", length = 100)
    private String docStatus;

    @Column(name = "PendingWith", length = 100)
    private String pendingWith;

    /* ---------- Misc extras ---------- */
    @Column(name = "ExtraV1", length = 50)
    private String extraV1;

    @Column(name = "RFIHoldNo")
    private Long rfiHoldNo;

    @Column(name = "Dim1", length = 150)
    private String dim1;

    @Column(name = "Dim2", length = 150)
    private String dim2;

    @Column(name = "Dim3", length = 150)
    private String dim3;

    @Column(name = "DrawingNo")
    private Long drawingNo;

    @Column(name = "HoldDetails", length = 300)
    private String holdDetails;

    @CreationTimestamp
    @Column(name = "offerdate2")
    private LocalDate offerDate2;

    @Column(name = "extraVar3")
    private Long extraVar3;
    
}
