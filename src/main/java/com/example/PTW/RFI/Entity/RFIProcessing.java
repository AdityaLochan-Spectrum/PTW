package com.example.PTW.RFI.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
// @Table(name = "RFIProcessing")
@Table(name = "RFIProcessing", indexes = {
        /* 2‑column non‑clustered index, second column DESC for fast MAX() */
        @Index(name = "IX_RFIID_SerialDesc", columnList = "RFIID, RemarkSerialNo DESC")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RFIProcessing {

    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EmbeddedId
    private RfiRemarkId id;

    @CreationTimestamp
    @Column(name = "RemarkDate")
    private LocalDateTime remarkDate;

    @Column(name = "Remarks", length = 250)
    private String remarks;

    @Column(name = "RemarksBy", length = 50)
    private String remarksBy;

    @Column(name = "Approved", length = 30)
    private String approved;

    @Column(name = "Process", length = 250)
    private String process;

    @Column(name = "OfferWorkAsPer", length = 50)
    private String offerWorkAsPer;

    @Column(name = "RFIPass", length = 50)
    private String rfiPass;

    @Column(name = "OfferedTime")
    private LocalDateTime offeredTime;

    @Column(name = "SiteReachingTime", length = 50)
    private String siteReachingTime;

    @Column(name = "InspectionStart")
    private LocalDate inspectionStart;

    @Column(name = "InspectionEnd")
    private LocalDateTime inspectionEnd;

    @Column(name = "ObservedDefectState", length = 300)
    private String observedDefectState;

    @Column(name = "NextStep", length = 300)
    private String nextStep;

    @Column(name = "Action", length = 300)
    private String action;

    @Column(name = "Resp", length = 100)
    private String resp;

    @Column(name = "Timeline", length = 50)
    private String timeline;

    @Column(name = "Extra1", length = 50)
    private String extra1;

    @Column(name = "Extra2", length = 50)
    private String extra2;

    @Column(name = "HoldRFINo")
    private Integer holdRfiNo;

    @Column(name = "StartTime", length = 50)
    private String startTime;

    @Column(name = "SiteTime", length = 50)
    private String siteTime;

    @Column(name = "extraDateFromTrasncation")
    private LocalDateTime extraDateFromTrasncation;
    
    @CreationTimestamp
    @Column(name = "InspectionEndTime")
    private LocalDateTime inspectionEndTime;

    @MapsId("rfiId")
    @ManyToOne(optional = false)
    @JoinColumn(name = "RFIID") // uses same column
    private RFITransaction rfiTransaction;
}
