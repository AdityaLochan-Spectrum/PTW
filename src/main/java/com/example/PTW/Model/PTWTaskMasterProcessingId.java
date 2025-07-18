package com.example.PTW.Model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* ----------------------------------------------------------- */
/*  1) COMPOSITE KEY                                           */
/* ----------------------------------------------------------- */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PTWTaskMasterProcessingId implements Serializable {

    @Column(name = "PTWId", nullable = false)
    private BigDecimal ptwId;               // FK → PTWTaskMaster.PTWId

    @Column(name = "RemarkSerialNo", nullable = false)
    private Long remarkSerialNo;      // 1, 2, 3… per PTW
}

