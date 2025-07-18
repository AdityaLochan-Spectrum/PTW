package com.example.PTW.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WFissuertoReviwer {

    private static final String WF_ID = "W000170";
    private static final String WF_DESC = "PTWTaskTemplate";
    private static final String INPUT_KEY1 = "PTWId";
    private static final BigDecimal LEAD_HOURS = new BigDecimal("10.00");
    private static final int PRIORITY = 1;

    private static final String PROC_ID_REVIEWER = "P000731";
    private static final String PROC_DESC_REVIEWER = "Reviewer Approval";

    private static final String PROC_ID_SUMMARY = "P000732";
    private static final String PROC_DESC_SUMMARY = "Summary";

    private static final String PROC_ID_CLOSE = "P000757";
    private static final String PROC_DESC_CLOSE = "Close Permit";

    @Autowired
    private JdbcTemplate jdbc;

    // ────────────────────────────────────────────────
    // Start Issuer to Reviewer (Reviewer, Summary, Close Permit)
    // ────────────────────────────────────────────────
    @Transactional
    public void startIssuerToReviewer(BigDecimal ptwId, String loginNumber) {
        try {
            BigDecimal baseSr = jdbc.queryForObject(
                "SELECT COALESCE(MAX(wpt_tracking_sr_no), 0) + 1 FROM [qbo].WorkflowProgressionTracking",
                BigDecimal.class);

            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime edc = now.plusHours(LEAD_HOURS.intValue());

            // 1. Get Reviewer (Approver)
            String reviewerEmpCode = jdbc.queryForObject(
                "SELECT TOP 1 em_emp_code FROM qbo.EmployeeMaster WHERE em_designation_code = 'Approver'",
                String.class);

            if (reviewerEmpCode == null) {
                throw new IllegalStateException("❌ No Approver found in EmployeeMaster.");
            }

            // 2. Get Prime Employee from Task Plan
            String taskPlanPrimeEmp = jdbc.queryForObject(
                "SELECT TOP 1 wpt_prime_resp_employee " +
                "FROM qbo.WorkflowProgressionTracking " +
                "WHERE wpt_input_key1 = 'PTWId' AND wpt_input_value1 = ? AND wpt_process_description = 'Task Plan'",
                new Object[]{ptwId},
                String.class);

            if (taskPlanPrimeEmp == null) {
                throw new IllegalStateException("❌ Task Plan record not found for PTWId: " + ptwId);
            }

            // 3. Fetch Dimensions
            String[] reviewerDims = getEmployeeDimensions(reviewerEmpCode);
            String[] taskPlanDims = getEmployeeDimensions(taskPlanPrimeEmp);

            // 4. Insert Workflow Rows
      // Step 1: Reviewer Approval
insertTrackingRow(
    baseSr,
    PROC_ID_REVIEWER,
    PROC_DESC_REVIEWER,
    reviewerEmpCode,              // Prime: Approver
    ptwId,
    now,
    edc,
    null,
    2,                            // Process Version
    loginNumber,                 // Reference Emp = Login user
    reviewerDims[0], reviewerDims[1], reviewerDims[2]
);

// Step 2: Summary
insertTrackingRow(
    baseSr.add(BigDecimal.ONE),
    PROC_ID_SUMMARY,
    PROC_DESC_SUMMARY,
    taskPlanPrimeEmp,             // Prime: From Task Plan
    ptwId,
    now,
    edc,
    null,
    1,                            // Process Version
    taskPlanPrimeEmp,            // ✅ Reference Emp = same as prime
    taskPlanDims[0], taskPlanDims[1], taskPlanDims[2]
);

// Step 3: Close Permit
insertTrackingRow(
    baseSr.add(BigDecimal.valueOf(2)),
    PROC_ID_CLOSE,
    PROC_DESC_CLOSE,
    taskPlanPrimeEmp,             // Prime: From Task Plan
    ptwId,
    now,
    edc,
    null,
    1,                            // Process Version
    taskPlanPrimeEmp,            // ✅ Reference Emp = same as prime
    taskPlanDims[0], taskPlanDims[1], taskPlanDims[2]
);


            System.out.println("✅ Issuer-to-Reviewer workflow started for PTWId: " + ptwId);

        } catch (Exception e) {
            System.err.println("❌ Error in startIssuerToReviewer: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // ─────────────────────────────
    // Start only Close Permit flow
    // ─────────────────────────────
    @Transactional
    public void startClosePermitOnly(BigDecimal ptwId) {
        try {
            BigDecimal sr = jdbc.queryForObject(
                "SELECT COALESCE(MAX(wpt_tracking_sr_no), 0) + 1 FROM [qbo].WorkflowProgressionTracking",
                BigDecimal.class);

            String taskPlanPrimeEmp = jdbc.queryForObject(
                "SELECT TOP 1 wpt_prime_resp_employee " +
                "FROM qbo.WorkflowProgressionTracking " +
                "WHERE wpt_input_key1 = 'PTWId' AND wpt_input_value1 = ? AND wpt_process_description = 'Task Plan'",
                new Object[]{ptwId},
                String.class);

            if (taskPlanPrimeEmp == null) {
                throw new IllegalStateException("❌ No Task Plan record found for PTWId: " + ptwId);
            }

            String[] dims = getEmployeeDimensions(taskPlanPrimeEmp);

            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime edc = now.plusHours(LEAD_HOURS.intValue());

            insertTrackingRow(sr,
                PROC_ID_CLOSE, PROC_DESC_CLOSE,
                taskPlanPrimeEmp, ptwId, now, edc,
                null, 1, taskPlanPrimeEmp,
                dims[0], dims[1], dims[2]);

            System.out.println("✅ Close Permit inserted for PTWId: " + ptwId);

        } catch (Exception e) {
            System.err.println("❌ Error in startClosePermitOnly: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // ─────────────────────────────
    // Shared INSERT for workflow row
    // ─────────────────────────────
    private void insertTrackingRow(BigDecimal sr,
                                   String procId,
                                   String procDesc,
                                   String primeEmp,
                                   BigDecimal ptwId,
                                   LocalDateTime start,
                                   LocalDateTime edc,
                                   String remarks,
                                   int processVersion,
                                   String referenceEmp,
                                   String dim1,
                                   String dim2,
                                   String dim3) {

        String sql = """
            INSERT INTO [qbo].WorkflowProgressionTracking (
                wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description,
                wpt_process_id, wpt_process_description, wpt_process_iterations,
                wpt_input_key1, wpt_input_key2, wpt_input_key3,
                wpt_input_value1, wpt_input_value2, wpt_input_value3,
                wpt_output1, wpt_output2, wpt_output3,
                wpt_earliest_start_date, wpt_start_date, wpt_lead_time,
                wpt_EDC, wpt_ADC,
                wpt_prime_resp_employee, wpt_secondary_resp_employee,
                wpt_notification_date, wpt_last_alert_date, wpt_reference_employee,
                wpt_dim1, wpt_dim2, wpt_dim3, wpt_remarks,
                wpt_wf_EDC, wm_version, wm_revision, wpt_process_version,
                wpt_EscalationDate, wpt_ReminderDate, wpt_Priority,
                wpt_DelayRemarks, wpt_TransactionError_Ind,
                wpt_DelayReasonCode, wpt_EscalationLevel)
            VALUES (?,?,?,?,?, NULL,
                    ?, '', '',
                    ?, '', '',
                    NULL, NULL, NULL,
                    ?, ?, ?, ?, NULL,
                    ?, NULL,
                    NULL, NULL, ?,
                    ?, ?, ?, ?,
                    ?, 1, 1, ?,
                    ?, ?, ?,
                    NULL, 'E',
                    NULL, NULL)
            """;

        jdbc.update(sql,
            sr,
            WF_ID, WF_DESC,
            procId, procDesc,
            INPUT_KEY1,
            ptwId,
            Timestamp.valueOf(start),
            Timestamp.valueOf(start),
            LEAD_HOURS,
            Timestamp.valueOf(edc),
            primeEmp,
            referenceEmp,
            dim1, dim2, dim3,
            remarks,
            Timestamp.valueOf(edc),
            processVersion,
            Timestamp.valueOf(edc.minusHours(1)),
            Timestamp.valueOf(edc.minusHours(2)),
            PRIORITY);
    }

    // ─────────────────────────────
    // Utility: Get Dimension1/2/3 by emp_code
    // ─────────────────────────────
    private String[] getEmployeeDimensions(String empCode) {
        return jdbc.queryForObject(
            "SELECT Dimension1, Dimension2, Dimension3 FROM qbo.EmployeeMaster WHERE em_emp_code = ?",
            new Object[]{empCode},
            (rs, rowNum) -> new String[]{
                rs.getString("Dimension1"),
                rs.getString("Dimension2"),
                rs.getString("Dimension3")
            });
    }
}
