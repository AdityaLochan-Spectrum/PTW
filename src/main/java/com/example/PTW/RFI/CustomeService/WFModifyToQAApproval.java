package com.example.PTW.RFI.CustomeService;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class WFModifyToQAApproval {

    private static final String WF_ID = "W000171";
    private static final String WF_DESC = "RFI";

    private static final String PROC_ID = "P000738";
    private static final String PROC_DESC = "QA Approval";

    private static final String INPUT_KEY1 = "RFIID";
    private static final String REF_EMP = "6380379124";
    private static final String PRIME_EMP = "8610559975";

    private static final String DIM1 = "Waaree";
    private static final String DIM2 = "180 MW Leap Green Energy";
    private static final String DIM3 = "Site Engineer";

    @Autowired
    private JdbcTemplate jdbc;

    @Transactional
    public void triggerQAApprovalStep(Long rfiId) {
        BigDecimal sr = jdbc.queryForObject(
                "SELECT COALESCE(MAX(wpt_tracking_sr_no),0) + 1 FROM [qbo].WorkflowProgressionTracking",
                BigDecimal.class);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime edc = startDate.plusDays(2); // EDC 2 days later, adjust if needed

        insertTrackingRow(sr, PROC_ID, PROC_DESC, PRIME_EMP, rfiId, startDate, edc, null);
    }

    private void insertTrackingRow(BigDecimal sr,
            String procId,
            String procDesc,
            String primeEmp,
            Long rfiId,
            LocalDateTime start,
            LocalDateTime edc,
            String remarks) {

        String sql = """
                INSERT INTO [qbo].WorkflowProgressionTracking (
                    wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description,
                    wpt_process_id,    wpt_process_description,
                    wpt_input_key1, wpt_input_key2, wpt_input_key3,
                    wpt_input_value1, wpt_input_value2, wpt_input_value3,
                    wpt_earliest_start_date, wpt_start_date, wpt_lead_time,
                    wpt_EDC,
                    wpt_prime_resp_employee, wpt_secondary_resp_employee,
                    wpt_reference_employee,
                    wpt_dim1, wpt_dim2, wpt_dim3,
                    wpt_wf_EDC,
                    wm_version, wm_revision, wpt_process_version,
                    wpt_Priority, wpt_remarks)
                VALUES (?,?,?,?,?,  ?,?, ?, ?,?,?,  ?,?, ?,  ?,  ?,?, ?,  ?,?, ?,  ?,  ?,?,?,  ?,?)""";

        jdbc.update(sql,
                sr,
                WF_ID, WF_DESC,
                procId, procDesc,
                INPUT_KEY1, "", "",
                rfiId, "", "",
                Timestamp.valueOf(start),
                Timestamp.valueOf(start),
                new BigDecimal("10.00"),
                Timestamp.valueOf(edc),
                primeEmp, null,
                REF_EMP,
                DIM1, DIM2, DIM3,
                Timestamp.valueOf(edc),
                1, 1, 1,
                1,
                remarks);
    }
}
