package com.example.PTW.RFI.Entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode   // Lombok generates equals() & hashCode()
public class RfiRemarkId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "RFIID")
    private Long rfiId;

    @Column(name = "RemarkSerialNo")
    private Long remarkSerialNo;
}
