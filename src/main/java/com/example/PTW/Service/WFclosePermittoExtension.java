package com.example.PTW.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class WFclosePermittoExtension {

    private static final String WF_ID        = "W000170";
    private static final String WF_DESC      = "PTWTaskTemplate";
    private static final String PROCESS_ID   = "P000762";
    private static final String PROCESS_DESC = "Extension";
    private static final String INPUT_KEY1   = "PTWId";

    @Autowired
    private JdbcTemplate jdbc;

    @Transactional
    public void startPTWExtensionWorkflow(BigDecimal ptwId) {

        // 1️⃣ Get next tracking number
        BigDecimal nextNo = jdbc.queryForObject(
            "SELECT ISNULL(MAX(wpt_tracking_sr_no),0)+1 FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class);

        // 2️⃣ Get prime employee from latest Task Plan
        String primeEmp = jdbc.queryForObject(
            """
            SELECT TOP 1 wpt_prime_resp_employee
            FROM qbo.WorkflowProgressionTracking
            WHERE wpt_input_key1 = 'PTWId' AND wpt_input_value1 = ?
              AND wpt_process_description = 'Task Plan'
            ORDER BY wpt_tracking_sr_no DESC
            """,
            new Object[]{ptwId},
            String.class
        );

        if (primeEmp == null || primeEmp.isEmpty()) {
            throw new IllegalStateException("❌ Task Plan prime employee not found for PTWId: " + ptwId);
        }

        // 3️⃣ Get dimensions from EmployeeMaster
        String[] dims = jdbc.queryForObject(
            """
            SELECT Dimension1, Dimension2, Dimension3
            FROM qbo.EmployeeMaster
            WHERE em_emp_code = ?
            """,
            new Object[]{primeEmp},
            (rs, rowNum) -> new String[]{
                rs.getString("Dimension1"),
                rs.getString("Dimension2"),
                rs.getString("Dimension3")
            }
        );

        // 4️⃣ Timestamps
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Timestamp edc = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

        // 5️⃣ Insert Extension row
        String sql = """
            INSERT INTO [qbo].WorkflowProgressionTracking (
                wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description, wpt_wf_iteration,
                wpt_process_id, wpt_process_description, wpt_process_iterations,
                wpt_input_key1, wpt_input_key2, wpt_input_key3,
                wpt_input_value1, wpt_input_value2, wpt_input_value3,
                wpt_output1, wpt_output2, wpt_output3,
                wpt_earliest_start_date, wpt_start_date, wpt_lead_time, wpt_EDC, wpt_ADC,
                wpt_prime_resp_employee, wpt_secondary_resp_employee,
                wpt_notification_date, wpt_last_alert_date, wpt_reference_employee,
                wpt_dim1, wpt_dim2, wpt_dim3,
                wpt_remarks, wpt_wf_EDC, wm_version, wm_revision, wpt_process_version,
                wpt_EscalationDate, wpt_ReminderDate, wpt_Priority,
                wpt_DelayRemarks, wpt_TransactionError_Ind, wpt_DelayReasonCode, wpt_EscalationLevel
            )
            VALUES (?, ?, ?, NULL, ?, ?, NULL, ?, '', '',
                    ?, '', '', NULL, NULL, NULL,
                    ?, ?, ?, ?, NULL,
                    ?, NULL, NULL, NULL, ?,
                    ?, ?, ?,
                    NULL, NULL, 1, 1, 1,
                    NULL, NULL, 1,
                    NULL, 'E', NULL, NULL)
        """;

        jdbc.update(sql,
            nextNo,                      // Tracking number
            WF_ID, WF_DESC,              // Workflow ID & description
            PROCESS_ID, PROCESS_DESC,    // Step details
            INPUT_KEY1,                  // Key name
            ptwId,                       // Key value
            now, now,                    // Start dates
            new BigDecimal("10.00"),     // Lead time
            edc,                         // EDC
            primeEmp,                    // Prime employee
            primeEmp,                    // Reference employee
            dims[0], dims[1], dims[2]    // Dimension1/2/3
        );

        System.out.println("✅ Extension workflow inserted for PTWId " + ptwId + " with primeEmp: " + primeEmp);
    }
}
