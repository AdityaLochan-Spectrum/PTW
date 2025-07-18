package com.example.PTW.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

/**
 * Maps to the table that stores document metadata and content.
 * <p>
 * ── Column-to-field notes ──────────────────────────────────────────────
 * nvarchar / nchar   →  String
 * datetime           →  LocalDateTime
 * decimal(18,4) / decimal(9,4) → BigDecimal
 * bit                →  Boolean
 * varbinary(max)     →  byte[]
 * <p>
 * If your table has a different primary-key design (composite key, identity, etc.)
 * adjust the @Id section accordingly.
 */
@Entity
@Table(name = "DocumentMaster",schema = "qbo")          // ← change if the table name differs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMaster {

    /* ------------------------------------------------------------------
       Primary key
       ------------------------------------------------------------------ */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "DocumentKey", length = 100, nullable = false)
    private String documentKey;

    /* ------------------------------------------------------------------
       Textual metadata
       ------------------------------------------------------------------ */
    @Column(name = "DocumentName", length = 300, nullable = false)
    private String documentName;

    @Column(name = "DocumentKey1", length = 100)
    private String documentKey1;

    @Column(name = "DocumentKey2", length = 100)
    private String documentKey2;

    @Column(name = "DocumentKey3", length = 100)
    private String documentKey3;

    @Column(name = "DocumentKey4", length = 100)
    private String documentKey4;

    @Column(name = "DocumentDescirption", length = 300)
    private String documentDescription;       // note the original typo “Descirption”

    @Column(name = "DocumentLongDescription", length = 100)
    private String documentLongDescription;

    /* ------------------------------------------------------------------
       Dates & versioning
       ------------------------------------------------------------------ */
    @Column(name = "DocumentCreatedDate")
    private LocalDateTime documentCreatedDate;

    @Column(name = "VersionNumber", precision = 18, scale = 4)
    private BigDecimal versionNumber;

    @Column(name = "RevisionNumber", precision = 18, scale = 4)
    private BigDecimal revisionNumber;

    /* ------------------------------------------------------------------
       File-system / storage info
       ------------------------------------------------------------------ */
    @Column(name = "DocumentPath", length = 2000)
    private String documentPath;

    @Column(name = "FileNamePart1", length = 300)
    private String fileNamePart1;

    @Column(name = "FileNamePart2", length = 32)
    private String fileNamePart2;

    @Column(name = "FileNameExtension", length = 16)
    private String fileNameExtension;

    /* ------------------------------------------------------------------
       Audit & transaction info
       ------------------------------------------------------------------ */
    @Column(name = "DocumentCreatedBy", length = 100)
    private String documentCreatedBy;

    @Column(name = "DocumentPage_Field", length = 300)
    private String documentPageField;

    @Column(name = "TransactionNumber", length = 600)
    private String transactionNumber;

    @Column(name = "status")
    private Boolean status;

    /* ------------------------------------------------------------------
       File content & type
       ------------------------------------------------------------------ */
    @Lob
    @Column(name = "fileContent")
    private byte[] fileContent;             // varbinary(max)

    @Column(name = "FileType", length = 200)
    private String fileType;

    /* ------------------------------------------------------------------
       Check-in / check-out indicators
       ------------------------------------------------------------------ */
    @Column(name = "CICOIndiation", length = 2)
    private String cicoIndication;

    @Column(name = "SequenceNumber", precision = 18, scale = 4)
    private BigDecimal sequenceNumber;

    @Column(name = "DocumentType", length = 64)
    private String documentType;

    public static Object builder() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'builder'");
    }
}
