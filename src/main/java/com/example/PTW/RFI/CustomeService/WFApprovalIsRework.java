package com.example.PTW.RFI.CustomeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WFApprovalIsRework {

    /* ───────── common workflow metadata ───────── */
    private static final String WF_ID   = "W000171";
    private static final String WF_DESC = "RFI";
    private static final String DIM1    = "Waaree";
    private static final String DIM2    = "180 MW Leap Green Energy";
    private static final String DIM3    = "Site Engineer";
    private static final String REF_EMP = "6380379124";   // also prime
    private static final String INPUT_KEY1 = "RFIID";

    /* ───────── step‑specific metadata ───────── */
    private static final String PROCESS_ID   = "P000741";
    private static final String PROCESS_DESC = "Modification";

    @Autowired
    private JdbcTemplate jdbc;


    @Transactional
    public void stampActualDateCompleted(Long rfiId) {
        /* We identify the row by the same key/value pair we just inserted:
           wpt_input_key1 = 'RFIID' AND wpt_input_value1 = :rfiId  */
        String sql =
            "UPDATE [qbo].WorkflowProgressionTracking " +
            "SET    wpt_ADC = GETDATE() " +            // ← current date‑time
            "WHERE  wpt_input_key1  = ? " +             //   (RFIID)
            "  AND  wpt_input_value1 = ?";
    
        jdbc.update(sql, INPUT_KEY1, rfiId);
    }

    /* ============================================================= */
    @Transactional
    public void startReworkWorkflow(Long rfiId) {
        insertProgressionRow(rfiId);
        insertTranTrackRow(rfiId);
    }

    /* ------------------------------------------------------------------
       1) qbo.WorkflowProgressionTracking
       ------------------------------------------------------------------ */
    private void insertProgressionRow(Long rfiId) {

        BigDecimal nextNo = jdbc.queryForObject(
            "SELECT ISNULL(MAX(wpt_tracking_sr_no),0)+1 " +
            "FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class);

        /* 12 placeholders → 12 params */
        String sql =
            "INSERT INTO [qbo].WorkflowProgressionTracking (" +
            "  wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description, " +
            "  wpt_process_id, wpt_process_description, " +
            "  wpt_input_key1, wpt_input_key2, wpt_input_key3, " +
            "  wpt_input_value1, wpt_input_value2, wpt_input_value3, " +
            "  wpt_prime_resp_employee, wpt_reference_employee, " +
            "  wpt_dim1, wpt_dim2, wpt_dim3, " +
            "  wpt_earliest_start_date, wpt_start_date, wpt_lead_time, " +
            "  wpt_EDC, wpt_ADC, wm_version, wm_revision, wpt_process_version, " +
            "  wpt_priority, wpt_wf_EDC ) " +
            "VALUES ( ?, ?, ?, ?, ?, ?, '', '', ?, '', '', ?, ?, ?, ?, ?, " +
            "          NULL, GETDATE(), 10.00, " +
            "          DATEADD(day,1,GETDATE()), NULL, 1, 1, 1, 1, " +
            "          DATEADD(day,1,GETDATE()) )";

        jdbc.update(sql,
            nextNo,           // 1
            WF_ID,            // 2
            WF_DESC,          // 3
            PROCESS_ID,       // 4
            PROCESS_DESC,     // 5
            INPUT_KEY1,       // 6
            rfiId,            // 7 (wpt_input_value1)
            REF_EMP,          // 8  prime resp
            REF_EMP,          // 9  reference emp
            DIM1, DIM2, DIM3  // 10‑12
        );
    }

    /* ------------------------------------------------------------------
       2) qbo.WorkflowTranTrack
       ------------------------------------------------------------------ */
    private void insertTranTrackRow(Long rfiId) {

        String start = LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        /* 7 placeholders → 7 params */
        String sql =
            "INSERT INTO qbo.WorkflowTranTrack (" +
            "  wf_id, InputKey1Name, InputKey1Value, " +
            "  InputKey2Name, InputKey2Value, InputKey3Name, InputKey3Value, " +
            "  Workflow_ID, Reference_Employee, Dimension1, Dimension2, Dimension3, " +
            "  Status, Workflow_StartDate, TimeZone) " +
            "VALUES (NULL, ?, ?, '', '', '', '', ?, ?, ?, ?, ?, 'CLOSED', ?, 'India Standard Time')";

        jdbc.update(sql,
            INPUT_KEY1,   // 1
            rfiId,        // 2
            WF_ID,        // 3
            REF_EMP,      // 4
            DIM1, DIM2, DIM3, // 5‑7
            start         // 8
        );
    }
}
