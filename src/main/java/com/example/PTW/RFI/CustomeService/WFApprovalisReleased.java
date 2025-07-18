package com.example.PTW.RFI.CustomeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WFApprovalisReleased {

    private static final String WF_ID        = "W000171";
    private static final String PROCESS_ID   = "P000739";
    private static final String WF_DESC      = "RFI";
    private static final String PROCESS_DESC = "AE Presentation Approval";
    private static final String INPUT_KEY1   = "RFIID";
    private static final String PRIME_EMP    = "8766425964";
    private static final String REF_EMP      = "6380379124";
    private static final String DIM1         = "Waaree";
    private static final String DIM2         = "180 MW Leap Green Energy";
    private static final String DIM3         = "DIM3";

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

    @Transactional
    public void startWorkflow(Long rfiId) {
        insertWorkflowProgression(rfiId);
        insertWorkflowTranTrack(rfiId);
    }

    /* ───────────────────────── WorkflowProgressionTracking ───────────────────────── */
    private void insertWorkflowProgression(Long rfiId) {

        BigDecimal nextNo = jdbc.queryForObject(
            "SELECT ISNULL(MAX(wpt_tracking_sr_no),0)+1 FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class);

        /* 12 placeholders → 12 arguments */
        String sql =
            "INSERT INTO [qbo].WorkflowProgressionTracking ( " +
            "  wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description, " +        // 1‑3
            "  wpt_process_id, wpt_process_description, " +                  // 4‑5
            "  wpt_input_key1,  wpt_input_key2, wpt_input_key3, " +          // 6  + literals '', ''
            "  wpt_input_value1, wpt_input_value2, wpt_input_value3, " +     // 7‑9  (value2/3 are '')
            "  wpt_prime_resp_employee, wpt_reference_employee, " +          // 10‑11
            "  wpt_dim1, wpt_dim2, wpt_dim3, " +                             // 12‑14
            "  wpt_start_date, wpt_EDC, wpt_lead_time, " +
            "  wm_version, wm_revision, wpt_process_version, wpt_priority " +
            ") VALUES ( ?, ?, ?, ?, ?, ?, '', '', ?, '', '', ?, ?, ?, ?, ?, " +
            "          GETDATE(), DATEADD(day,2,GETDATE()), 10.00, 1, 1, 1, 1)";

        jdbc.update(sql,
            nextNo,            // 1
            WF_ID,             // 2
            WF_DESC,           // 3
            PROCESS_ID,        // 4
            PROCESS_DESC,      // 5
            INPUT_KEY1,        // 6
            rfiId,             // 7  (wpt_input_value1)
            PRIME_EMP,         // 8  (prime resp)
            REF_EMP,           // 9  (reference emp)
            DIM1,              // 10
            DIM2,              // 11
            DIM3               // 12
        );

        System.out.println("✅ Progression row OK (tracking=" + nextNo + ", RFIID=" + rfiId + ")");
    }

    /* ──────────────────────────── WorkflowTranTrack ───────────────────────────── */
    private void insertWorkflowTranTrack(Long rfiId) {

        String start = LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        /* 7 placeholders → 7 arguments */
        String sql =
            "INSERT INTO qbo.WorkflowTranTrack ( " +
            "  wf_id, InputKey1Name, InputKey1Value, " +
            "  InputKey2Name, InputKey2Value, InputKey3Name, InputKey3Value, " +
            "  Workflow_ID, Reference_Employee, Dimension1, Dimension2, Dimension3, " +
            "  Status, Workflow_StartDate, TimeZone) " +
            "VALUES (NULL, ?, ?, '', '', '', '', ?, ?, ?, ?, ?, 'CLOSED', ?, 'India Standard Time')";

        jdbc.update(sql,
            INPUT_KEY1,   // 1  "RFIID"
            rfiId,        // 2
            WF_ID,        // 3
            REF_EMP,      // 4
            DIM1,         // 5
            DIM2,         // 6
            DIM3,         // 7
            start         // 8
        );

        System.out.println("✅ TranTrack row OK (RFIID=" + rfiId + ")");
    }
}
