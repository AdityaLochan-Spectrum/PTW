package com.example.PTW.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class WEReviewExtensionToNextProcess {

    private static final String WF_ID = "W000170";
    private static final String WF_DESC = "PTWTaskTemplate";

    private static final String PROC_ID = "P000757";
    private static final String PROC_DESC = "Close Permit";

    private static final String INPUT_KEY1 = "PTWId";

    private static final String DIM1 = "Bank";
    private static final String DIM2 = "West";
    private static final String DIM3 = "quickbpm";

    @Autowired
    private JdbcTemplate jdbc;

    @Transactional
    public void startWorkflow(BigDecimal ptwId) {
        // Get the next serial number
        BigDecimal nextSr = jdbc.queryForObject(
            "SELECT COALESCE(MAX(wpt_tracking_sr_no), 0) + 1 FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class
        );

        // Fetch prime and reference employee from most recent 'Task Plan' entry
        String employeeCode = jdbc.queryForObject(
            """
            SELECT TOP 1 wpt_prime_resp_employee
            FROM qbo.WorkflowProgressionTracking
            WHERE wpt_input_key1 = 'PTWId'
              AND wpt_input_value1 = ?
              AND wpt_process_description = 'Task Plan'
            ORDER BY wpt_tracking_sr_no DESC
            """,
            new Object[]{ptwId},
            String.class
        );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime edc = now.plusDays(1);

        insertTrackingRow(nextSr, ptwId, now, edc, employeeCode);
    }

    private void insertTrackingRow(BigDecimal sr,
                                   BigDecimal ptwId,
                                   LocalDateTime start,
                                   LocalDateTime edc,
                                   String employeeCode) {

        String sql = """
            INSERT INTO [qbo].WorkflowProgressionTracking (
                wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description,
                wpt_process_id, wpt_process_description,
                wpt_input_key1, wpt_input_key2, wpt_input_key3,
                wpt_input_value1, wpt_input_value2, wpt_input_value3,
                wpt_output1, wpt_output2, wpt_output3,
                wpt_earliest_start_date, wpt_start_date, wpt_lead_time,
                wpt_EDC, wpt_ADC,
                wpt_prime_resp_employee, wpt_secondary_resp_employee,
                wpt_notification_date, wpt_last_alert_date,
                wpt_reference_employee,
                wpt_dim1, wpt_dim2, wpt_dim3,
                wpt_remarks,
                wpt_wf_EDC,
                wm_version, wm_revision, wpt_process_version,
                wpt_EscalationDate, wpt_ReminderDate, wpt_Priority,
                wpt_DelayRemarks, wpt_TransactionError_Ind, wpt_DelayReasonCode, wpt_EscalationLevel
            ) VALUES (?,?,?,?,?, ?,?,?, ?,?,?, ?,?,?, ?,?,?, ?,?, ?,?,?, ?,?, ?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbc.update(sql,
            sr, // 1
            WF_ID, WF_DESC, // 2,3
            PROC_ID, PROC_DESC, // 4,5
            INPUT_KEY1, "", "", // 6,7,8
            ptwId, "", "", // 9,10,11
            null, null, null, // 12,13,14
            Timestamp.valueOf(start), // 15
            Timestamp.valueOf(start), // 16
            new BigDecimal("10.00"), // 17
            Timestamp.valueOf(edc), // 18
            null, // 19 (ADC)
            employeeCode, // 20 (prime_resp_employee)
            null, // 21
            null, // 22
            null, // 23
            employeeCode, // 24 (reference_employee)
            DIM1, DIM2, DIM3, // 25,26,27
            "CLOSED", // 28
            Timestamp.valueOf(edc), // 29
            1, 1, 1, // 30,31,32
            null, null, // 33,34
            1, // 35
            null, null, null, null // 36–39
        );
    }
}
