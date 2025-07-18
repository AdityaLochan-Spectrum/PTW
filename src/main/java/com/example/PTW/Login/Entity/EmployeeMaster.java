package com.example.PTW.Login.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employeemaster")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMaster {

    @Id
    @Column(name = "em_emp_code", length = 500, nullable = false)
    private String empCode;

    @Column(name = "em_password", length = 100)
    private String password;

    @Column(name = "em_email_id", length = 50)
    private String emailId;

    @Column(name = "em_superior_code", length = 50)
    private String superiorCode;

    @Column(name = "Dimension1", length = 100)
    private String dimension1;

    @Column(name = "Dimension2", length = 100)
    private String dimension2;

    @Column(name = "Dimension3", length = 100)
    private String dimension3;

    @Column(name = "em_Designation_Code", length = 10)
    private String designationCode;

    @Column(name = "em_FirstName", length = 50)
    private String firstName;

    @Column(name = "em_MiddleName", length = 50)
    private String middleName;

    @Column(name = "em_LastName", length = 50)
    private String lastName;

    @Column(name = "em_DOJ")
    private LocalDate dateOfJoining;

    @Column(name = "em_DOB")
    private LocalDate dateOfBirth;

    @Column(name = "em_DOS")
    private LocalDate dateOfSeparation;

    @Column(name = "em_DOR")
    private LocalDate dateOfRetirement;

    @Column(name = "em_MobileNo", length = 50)
    private String mobileNo;

    @Column(name = "em_RoleId")
    private Integer roleId;

    @Column(name = "CreatedBy", length = 10)
    private String createdBy;

    @Column(name = "CreatedOn")
    private LocalDateTime createdOn;

    @Column(name = "ModifiedBy", length = 10)
    private String modifiedBy;

    @Column(name = "ModifiedOn")
    private LocalDateTime modifiedOn;

    @Column(name = "em_Division", length = 10)
    private String division;

    @Column(name = "em_passport_no", length = 50)
    private String passportNo;

    @Column(name = "em_passport_IssueDate", length = 50)
    private String passportIssueDate;

    @Column(name = "em_passport_IssuedAt", length = 50)
    private String passportIssuedAt;

    @Column(name = "em_passport_expiryDate", length = 50)
    private String passportExpiryDate;

    @Column(name = "em_contactno", length = 50)
    private String contactNo;

    @Column(name = "em_FullName", length = 255)
    private String fullName;

    @Column(name = "em_Grade", length = 50)
    private String grade;

    @Column(name = "em_ecode", length = 50)
    private String ecode;

    @Column(name = "em_DepartmentCode", length = 50)
    private String departmentCode;

    @Column(name = "em_RegionName", length = 250)
    private String regionName;

    @Column(name = "em_Location", length = 100)
    private String location;

    @Column(name = "em_HOD", length = 50)
    private String hod;

    @Column(name = "em_EmpType", length = 100)
    private String empType;

    @Column(name = "em_Country", length = 100)
    private String country;

    @Column(name = "em_Band", length = 10)
    private String band;

    @Column(name = "em_ExtensionNo", length = 10)
    private String extensionNo;

    @Column(name = "Superior", length = 50)
    private String superior;

    @Column(name = "Division", length = 50)
    private String division2;

    @Column(name = "Appraiser", length = 50)
    private String appraiser;

    @Column(name = "DateOfBirth")
    private LocalDateTime dob;

    @Column(name = "LocationCode", length = 50)
    private String locationCode;

    @Column(name = "em_CostPerDay", precision = 18, scale = 2)
    private BigDecimal costPerDay;

    @Column(name = "em_CostPerHour", precision = 18, scale = 2)
    private BigDecimal costPerHour;

    @Column(name = "em_emp_id", precision = 18, scale = 4)
    private BigDecimal empId;

    @Column(name = "Workflow_ID", length = 50)
    private String workflowId;

    @Column(name = "INITIATOR", length = 50)
    private String initiator;

    @Column(name = "CalenderCode", length = 10)
    private String calenderCode;

    @Column(name = "LocationTrackingEnabled", length = 2)
    private String locationTrackingEnabled;

    @Column(name = "SessionID")
    private Integer sessionId;

    @Column(name = "CheckInTime")
    private LocalDateTime checkInTime;

    @Column(name = "CheckOutTime")
    private LocalDateTime checkOutTime;

    @Column(name = "Address", length = 510)
    private String address;

    @Column(name = "Gender", length = 20)
    private String gender;

    @Column(name = "GroupOwner", length = 100)
    private String groupOwner;

    @Column(name = "DeletedYN", length = 5)
    private String deletedYN;

    @Column(name = "DeletedBy", length = 50)
    private String deletedBy;

    @Column(name = "DeletedOn")
    private LocalDateTime deletedOn;

    @Column(name = "em_State", length = 100)
    private String state;

    @Column(name = "em_City", length = 100)
    private String city;

    @Column(name = "em_PINCode", length = 100)
    private String pinCode;

    @Column(name = "em_TimeZone", length = 250)
    private String timeZone;

    @Column(name = "DocStatus", length = 100)
    private String docStatus;

    @Column(name = "PendingWith", length = 100)
    private String pendingWith;
}

