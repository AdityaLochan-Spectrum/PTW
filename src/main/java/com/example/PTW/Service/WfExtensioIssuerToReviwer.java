package com.example.PTW.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class WfExtensioIssuerToReviwer {

    private static final String WF_ID = "W000170";
    private static final String WF_DESC = "PTWTaskTemplate";
    private static final String PROCESS_ID_REVIEWER = "P000765";
    private static final String PROCESS_DESC_REVIEWER = "Reviewer Approval for Extension";
    private static final String PROCESS_ID_CLOSE = "P000757";
    private static final String PROCESS_DESC_CLOSE = "Close Permit";
    private static final String INPUT_KEY1 = "PTWId";

    @Autowired
    private JdbcTemplate jdbc;

    private Map<String, Object> fetchApproverFromEmployeeMaster() {
        String sql = "SELECT TOP 1 em_emp_code, Dimension1, Dimension2, Dimension3 " +
                     "FROM qbo.employeemaster WHERE em_designation_code = 'Approver'";
        return jdbc.queryForMap(sql);
    }

    private String fetchPrimeRespFromTaskPlan(BigDecimal ptwId) {
        String sql = "SELECT TOP 1 wpt_prime_resp_employee " +
                     "FROM qbo.WorkflowProgressionTracking " +
                     "WHERE wpt_input_key1 = 'PTWId' AND wpt_input_value1 = ? " +
                     "AND wpt_process_description = 'Task Plan'";
        return jdbc.queryForObject(sql, String.class, ptwId);
    }

    @Transactional
    public void startReviewerApprovalWorkflow(BigDecimal ptwId) {
        BigDecimal nextNo = jdbc.queryForObject(
            "SELECT ISNULL(MAX(wpt_tracking_sr_no), 0) + 1 FROM qbo.WorkflowProgressionTracking",
            BigDecimal.class
        );

        // Fetch employees
        Map<String, Object> approver = fetchApproverFromEmployeeMaster();
        String primeResp = (String) approver.get("em_emp_code");
        String dim1 = (String) approver.get("Dimension1");
        String dim2 = (String) approver.get("Dimension2");
        String dim3 = (String) approver.get("Dimension3");

        String referenceResp = fetchPrimeRespFromTaskPlan(ptwId);

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Timestamp edc = Timestamp.valueOf(now.toLocalDateTime().plusDays(1));

        String sql = "INSERT INTO qbo.WorkflowProgressionTracking (" +
            "wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description, wpt_wf_iteration, " +
            "wpt_process_id, wpt_process_description, wpt_process_iterations, " +
            "wpt_input_key1, wpt_input_key2, wpt_input_key3, " +
            "wpt_input_value1, wpt_input_value2, wpt_input_value3, " +
            "wpt_output1, wpt_output2, wpt_output3, " +
            "wpt_earliest_start_date, wpt_start_date, wpt_lead_time, wpt_EDC, wpt_ADC, " +
            "wpt_prime_resp_employee, wpt_secondary_resp_employee, " +
            "wpt_notification_date, wpt_last_alert_date, wpt_reference_employee, " +
            "wpt_dim1, wpt_dim2, wpt_dim3, " +
            "wpt_remarks, wpt_wf_EDC, wm_version, wm_revision, wpt_process_version, " +
            "wpt_EscalationDate, wpt_ReminderDate, wpt_Priority, " +
            "wpt_DelayRemarks, wpt_TransactionError_Ind, wpt_DelayReasonCode, wpt_EscalationLevel" +
            ") VALUES (?, ?, ?, NULL, ?, ?, NULL, ?, '', '', ?, '', '', NULL, NULL, NULL, " +
            "?, ?, ?, ?, NULL, ?, NULL, NULL, NULL, ?, ?, ?, ?, NULL, NULL, 1, 1, 1, " +
            "NULL, NULL, 1, NULL, 'E', NULL, NULL)";

        jdbc.update(sql,
            nextNo,
            WF_ID,
            WF_DESC,
            PROCESS_ID_REVIEWER,
            PROCESS_DESC_REVIEWER,
            INPUT_KEY1,
            ptwId,
            null,
            now,
            new BigDecimal("10.00"),
            edc,
            primeResp,
            referenceResp,
            dim1, dim2, dim3
        );

        System.out.println("✅ Reviewer Approval inserted (tracking=" + nextNo + ", PTWId=" + ptwId + ")");
    }

    @Transactional
    public void startClosePermitWorkflow(BigDecimal ptwId) {
        BigDecimal nextNo = jdbc.queryForObject(
            "SELECT ISNULL(MAX(wpt_tracking_sr_no), 0) + 1 FROM qbo.WorkflowProgressionTracking",
            BigDecimal.class
        );

        String primeAndRefEmp = fetchPrimeRespFromTaskPlan(ptwId);

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Timestamp edc = Timestamp.valueOf(now.toLocalDateTime().plusDays(2));

        // You can reuse dimensions from employee master or task plan if available
        Map<String, Object> approver = fetchApproverFromEmployeeMaster();
        String dim1 = (String) approver.get("Dimension1");
        String dim2 = (String) approver.get("Dimension2");
        String dim3 = (String) approver.get("Dimension3");

        String sql = "INSERT INTO qbo.WorkflowProgressionTracking (" +
            "wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description, wpt_wf_iteration, " +
            "wpt_process_id, wpt_process_description, wpt_process_iterations, " +
            "wpt_input_key1, wpt_input_key2, wpt_input_key3, " +
            "wpt_input_value1, wpt_input_value2, wpt_input_value3, " +
            "wpt_output1, wpt_output2, wpt_output3, " +
            "wpt_earliest_start_date, wpt_start_date, wpt_lead_time, wpt_EDC, wpt_ADC, " +
            "wpt_prime_resp_employee, wpt_secondary_resp_employee, " +
            "wpt_notification_date, wpt_last_alert_date, wpt_reference_employee, " +
            "wpt_dim1, wpt_dim2, wpt_dim3, " +
            "wpt_remarks, wpt_wf_EDC, wm_version, wm_revision, wpt_process_version, " +
            "wpt_EscalationDate, wpt_ReminderDate, wpt_Priority, " +
            "wpt_DelayRemarks, wpt_TransactionError_Ind, wpt_DelayReasonCode, wpt_EscalationLevel" +
            ") VALUES (?, ?, ?, NULL, ?, ?, NULL, ?, '', '', ?, '', '', NULL, NULL, NULL, " +
            "?, ?, ?, ?, NULL, ?, NULL, NULL, NULL, ?, ?, ?, ?, NULL, NULL, 1, 1, 1, " +
            "NULL, NULL, 1, NULL, NULL, NULL, NULL)";

        jdbc.update(sql,
            nextNo,
            WF_ID,
            WF_DESC,
            PROCESS_ID_CLOSE,
            PROCESS_DESC_CLOSE,
            INPUT_KEY1,
            ptwId,
            now,
            now,
            new BigDecimal("10.00"),
            edc,
            primeAndRefEmp, // prime
            primeAndRefEmp, // reference
            dim1, dim2, dim3
        );

        System.out.println("✅ Close Permit workflow inserted (tracking=" + nextNo + ", PTWId=" + ptwId + ")");
    }
}
