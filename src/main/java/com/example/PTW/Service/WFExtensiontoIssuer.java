package com.example.PTW.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class WFExtensiontoIssuer {

    private static final String WF_ID        = "W000170";
    private static final String WF_DESC      = "PTWTaskTemplate";
    private static final String PROCESS_ID   = "P000766";
    private static final String PROCESS_DESC = "Issue for Extension";
    private static final String INPUT_KEY1   = "PTWId";

    private static final String PRIME_EMP_CODE = "8766425964";

    @Autowired
    private JdbcTemplate jdbc;

    @Transactional
    public void startPTWExtensionWorkflow(BigDecimal ptwId, String loginNumber) {
        insertWorkflowProgression(ptwId, loginNumber);
    }

    private void insertWorkflowProgression(BigDecimal ptwId, String loginNumber) {
        // 1️⃣ Get next tracking number
        BigDecimal nextNo = jdbc.queryForObject(
            "SELECT ISNULL(MAX(wpt_tracking_sr_no), 0) + 1 FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class
        );

        // 2️⃣ Fetch Dimensions for Prime Employee from EmployeeMaster
        String[] dims = jdbc.queryForObject(
            "SELECT Dimension1, Dimension2, Dimension3 FROM qbo.EmployeeMaster WHERE em_emp_code = ?",
            new Object[]{PRIME_EMP_CODE},
            (rs, rowNum) -> new String[]{
                rs.getString("Dimension1"),
                rs.getString("Dimension2"),
                rs.getString("Dimension3")
            }
        );

        // 3️⃣ Prepare timestamps
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Timestamp edc = Timestamp.valueOf(now.toLocalDateTime().plusDays(1));

        // 4️⃣ Prepare and execute insert query
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
            VALUES (?, ?, ?, NULL, ?, ?, NULL, ?, '', '', ?, '', '', NULL, NULL, NULL,
                    ?, ?, ?, ?, NULL, ?, NULL, NULL, NULL, ?, ?, ?, ?, NULL, NULL, 1, 1, 1,
                    NULL, NULL, 1, NULL, 'E', NULL, NULL)
        """;

        jdbc.update(sql,
            nextNo,                // wpt_tracking_sr_no
            WF_ID, WF_DESC,        // workflow ID & description
            PROCESS_ID, PROCESS_DESC,    // process ID & description
            INPUT_KEY1, ptwId,     // input key & value
            now, now,              // start dates
            new BigDecimal("10.00"),  // lead time
            edc,                   // EDC
            PRIME_EMP_CODE,        // prime employee
            loginNumber,           // reference employee
            dims[0], dims[1], dims[2]  // dimensions
        );

        System.out.println("✅ PTW Extension Issuer row inserted (tracking=" + nextNo + ", PTWId=" + ptwId + ")");
    }
}
