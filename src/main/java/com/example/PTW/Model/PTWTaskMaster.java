package com.example.PTW.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "PTWTaskMaster")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PTWTaskMaster {

    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PTWId", precision = 10, scale = 0)
    private BigDecimal ptwId;

    @Column(name = "PTWCode", length = 50)
    private String ptwCode;

    @CreationTimestamp
    @Column(name = "PTWStartDate")
    private LocalDateTime ptwStartDate;

    @Column(name = "TemplateCode", length = 50)
    private String templateCode;

    @Column(name = "AcivityCompleted", length = 20)
    private String acivityCompleted;

    @Column(name = "ActualEndDate")
    private Date actualEndDate;

    @Column(name = "Duration", length = 10)
    private String duration;

    @Column(name = "Remark", length = 100)
    private String remark;

    @Column(name = "PTWDate")
    private Date ptwDate;

    @Column(name = "Approved", length = 20)
    private String approved;

    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @Column(name = "CreatedOn")
    private LocalDateTime createdOn;

    @Column(name = "ModifiedBy", length = 50)
    private String modifiedBy;

    @Column(name = "ModifiedOn")
    private LocalDateTime modifiedOn;

    @Column(name = "DocStatus", length = 100)
    private String docStatus;

    @Column(name = "PendingWith", length = 100)
    private String pendingWith;

    @Column(name = "LongDescription", length = 100)
    private String longDescription;

    @Column(name = "PODate")
    private Date poDate;

    @Column(name = "TemplateTask1", length = 250)
    private String templateTask1;

    @Column(name = "TemplateTask2", length = 250)
    private String templateTask2;

    @Column(name = "TemplateTask3", length = 250)
    private String templateTask3;

    @Column(name = "TemplateTask4", length = 250)
    private String templateTask4;

    @Column(name = "TemplateTask5", length = 250)
    private String templateTask5;

    @Column(name = "TemplateTask6", length = 250)
    private String templateTask6;

    @Column(name = "TemplateTask7", length = 250)
    private String templateTask7;

    @Column(name = "TemplateTask8", length = 250)
    private String templateTask8;

    @Column(name = "TemplateTask9", length = 250)
    private String templateTask9;

    @Column(name = "TemplateTask10", length = 250)
    private String templateTask10;

    @Column(name = "Duration1", length = 250)
    private String duration1;

    @Column(name = "Duration2", length = 250)
    private String duration2;

    @Column(name = "Duration3", length = 250)
    private String duration3;

    @Column(name = "Duration4", length = 250)
    private String duration4;

    @Column(name = "Duration5", length = 250)
    private String duration5;

    @Column(name = "Duration6", length = 250)
    private String duration6;

    @Column(name = "Duration7", length = 250)
    private String duration7;

    @Column(name = "Duration8", length = 250)
    private String duration8;

    @Column(name = "Duration9", length = 250)
    private String duration9;

    @Column(name = "Duration10", length = 250)
    private String duration10;

    @Column(name = "Duration11", length = 10)
    private String duration11;

    @Column(name = "Duration12", length = 10)
    private String duration12;

    @Column(name = "Duration13", length = 10)
    private String duration13;

    @Column(name = "Duration14", length = 10)
    private String duration14;

    @Column(name = "Duration15", length = 10)
    private String duration15;

    @Column(name = "Duration16", length = 10)
    private String duration16;

    @Column(name = "Duration17", length = 10)
    private String duration17;

    @Column(name = "Duration18", length = 250)
    private String duration18;

    @Column(name = "Duration19", length = 250)
    private String duration19;

    @Column(name = "Duration20", length = 250)
    private String duration20;

    @Column(name = "Duration21", length = 250)
    private String duration21;

    @Column(name = "Duration22", length = 250)
    private String duration22;

    @Column(name = "Duration23", length = 250)
    private String duration23;

    @Column(name = "Duration24", length = 250)
    private String duration24;

    @Column(name = "Duration25", length = 250)
    private String duration25;

    @Column(name = "LoadToBeLifted", length = 50)
    private String loadToBeLifted;

    @Column(name = "NameOfEquipment", length = 50)
    private String nameOfEquipment;

    @Column(name = "CapacityOfEquipment", length = 50)
    private String capacityOfEquipment;

    @Column(name = "CertificateOfEquipment", length = 50)
    private String certificateOfEquipment;

    @Column(name = "Remarks1", length = 300)
    private String remarks1;

    @Column(name = "Remarks2", length = 300)
    private String remarks2;

    @Column(name = "Remarks3", length = 300)
    private String remarks3;

    @Column(name = "Remarks4", length = 300)
    private String remarks4;

    @Column(name = "Remarks5", length = 300)
    private String remarks5;

    @Column(name = "Remarks6", length = 300)
    private String remarks6;

    @Column(name = "Remarks7", length = 300)
    private String remarks7;

    @Column(name = "Remarks8", length = 300)
    private String remarks8;

    @Column(name = "Remarks9", length = 300)
    private String remarks9;

    @Column(name = "Remarks10", length = 300)
    private String remarks10;

    @Column(name = "Remarks11", length = 300)
    private String remarks11;

    @Column(name = "Remarks12", length = 300)
    private String remarks12;

    @Column(name = "Remarks13", length = 300)
    private String remarks13;

    @Column(name = "Remarks14", length = 300)
    private String remarks14;

    @Column(name = "Remarks15", length = 300)
    private String remarks15;

    @Column(name = "Remarks16", length = 300)
    private String remarks16;

    @Column(name = "Remarks17", length = 300)
    private String remarks17;

    @Column(name = "Remarks18", length = 300)
    private String remarks18;

    @Column(name = "Remarks19", length = 300)
    private String remarks19;

    @Column(name = "Remarks20", length = 300)
    private String remarks20;

    @Column(name = "Remarks21", length = 300)
    private String remarks21;

    @Column(name = "Remarks22", length = 300)
    private String remarks22;

    @Column(name = "Remarks23", length = 300)
    private String remarks23;

    @Column(name = "Remarks24", length = 300)
    private String remarks24;

    @Column(name = "Remarks25", length = 300)
    private String remarks25;

    @Column(name = "NameOfCompanyPerson", length = 100)
    private String nameOfCompanyPerson;

    @Column(name = "Location", length = 100)
    private String location;

    @Column(name = "FromHours", length = 50)
    private String fromHours;

    @Column(name = "ToHours", length = 50)
    private String toHours;

    @Column(name = "ToUndertakeJob", length = 100)
    private String toUndertakeJob;

    @Column(name = "PermitIssueTo", length = 100)
    private String permitIssueTo;

    @Column(name = "TotalHrs")
    private Integer totalHrs;

    @Column(name = "SelectWork", length = 200)
    private String selectWork;

    @Column(name = "PermitNo", length = 50)
    private String permitNo;

    @Column(name = "Voltage", length = 50)
    private String voltage;

    @Column(name = "SafetyClearance", length = 50)
    private String safetyClearance;

    @Column(name = "EXCheck1", length = 50)
    private String exCheck1;

    @Column(name = "EXCheck2", length = 50)
    private String exCheck2;

    @Column(name = "EXCheck3", length = 50)
    private String exCheck3;

    @Column(name = "EXCheck4", length = 50)
    private String exCheck4;

    @Column(name = "EXCheck5", length = 50)
    private String exCheck5;

    @Column(name = "EXCheck6", length = 50)
    private String exCheck6;

    @Column(name = "EXCheck7", length = 50)
    private String exCheck7;

    @Column(name = "EXCheck8", length = 50)
    private String exCheck8;

    @Column(name = "EXCheck9", length = 50)
    private String exCheck9;

    @Column(name = "EXCheck10", length = 50)
    private String exCheck10;

    @Column(name = "EXCheck11", length = 50)
    private String exCheck11;

    @Column(name = "ExtensionP1", length = 500)
    private String extensionP1;

    @Column(name = "ExtensionP2", length = 500)
    private String extensionP2;

    @Column(name = "ExtensionP3", length = 500)
    private String extensionP3;

    @Column(name = "ExtensionP4", length = 500)
    private String extensionP4;

    @Column(name = "ExtensionP5", length = 500)
    private String extensionP5;

    @Column(name = "ExtensionP6", length = 500)
    private String extensionP6;

    @Column(name = "ExtensionP7", length = 500)
    private String extensionP7;

    @Column(name = "ExtensionP8", length = 500)
    private String extensionP8;

    @Column(name = "ExtensionP9", length = 500)
    private String extensionP9;

    @Column(name = "ExtensionP10", length = 500)
    private String extensionP10;

    @Column(name = "ExtensionP11", length = 500)
    private String extensionP11;

    @Column(name = "ExtensionFrom")
    private LocalDateTime extensionFrom;

    @Column(name = "ExtensionTo")
    private LocalDateTime extensionTo;

    @Column(name = "SupervisorName", length = 150)
    private String supervisorName;

    @Column(name = "ExtensionDate")
    private LocalDateTime extensionDate;

    @Column(name = "ExRemarks1", length = 300)
    private String exRemarks1;

    @Column(name = "ExRemarks2", length = 300)
    private String exRemarks2;

    @Column(name = "ExRemarks3", length = 300)
    private String exRemarks3;

    @Column(name = "ExRemarks4", length = 300)
    private String exRemarks4;

    @Column(name = "ExRemarks5", length = 300)
    private String exRemarks5;

    @Column(name = "ExRemarks6", length = 300)
    private String exRemarks6;

    @Column(name = "ExRemarks7", length = 300)
    private String exRemarks7;

    @Column(name = "ExRemarks8", length = 300)
    private String exRemarks8;

    @Column(name = "ExRemarks9", length = 300)
    private String exRemarks9;

    @Column(name = "ExRemarks10", length = 300)
    private String exRemarks10;

    @Column(name = "ExRemarks11", length = 300)
    private String exRemarks11;

    @CreationTimestamp
    @Column(name = "PermitDateNew")
    private Date permitDateNew;

    // add just below the Capacity/Certificate or wherever you prefer
    @Column(name = "Dim1", length = 50)
    private String dim1;

    @Column(name = "Dim2", length = 50)
    private String dim2;

    @Column(name = "Dim3", length = 50)
    private String dim3;
    @Column(name = "DetailsOfWork", length = 300)
    private String detailsOfWork;
    @Column(name = "PTWEndDate")
    private LocalDateTime ptwEndDate;


    @Column(name = "IssueDateFrom")
private LocalDateTime issueDateFrom;

@Column(name = "IssueDateTo")
private LocalDateTime issueDateTo;

// ── Simple values ────────────────────────────────────────────────
@Column(name = "NameofProject", length = 100)   // adjust length as required
private String nameOfProject;

@Column(name = "PermitDate")
private Date permitDate;                        // or LocalDate, depending on DB type

@Column(name = "ExtensionToHrs")
private Integer extensionToHrs;                 // change to String if you need HH:MM

@Column(name = "SendExtension", length = 1)     // or Boolean
private String sendExtension;


@Column(name = "extraRemarkSerial")
private Long extraRemarkSerial;


    /** Keeps PTWEndDate = PTWStartDate + 9 h */
    private void syncEndDate() {
        if (ptwStartDate != null) {
            this.ptwEndDate = ptwStartDate.plusHours(9);
        }
    }

    public void setPtwId(BigDecimal ptwId) {
        this.ptwId = ptwId;
    }

    public BigDecimal getPtwId() {
        return ptwId;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

}
