package com.example.PTW.RFI.FromQAApprovalToClientApproval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Creates the “Modification / Re‑work” step in workflow W000171 (process P000741)
 * for a given RFI and can later mark the row completed (ADC).
 */
@Service
public class WhenRework {

    /* ────────── workflow metadata ────────── */
    private static final String WF_ID   = "W000171";
    private static final String WF_DESC = "RFI";

    private static final String DIM1    = "Waaree";
    private static final String DIM2    = "180 MW Leap Green Energy";
    private static final String DIM3    = "Site Engineer";

    private static final String REF_EMP = "6380379124";      // also prime
    private static final String INPUT_KEY1 = "RFIID";

    /* ────────── step metadata ────────── */
    private static final String PROC_ID   = "P000741";
    private static final String PROC_DESC = "Modification";
    private static final String PRIME_EMP = "6380379124";

    @Autowired
    private JdbcTemplate jdbc;

    /* ==============================================================
       PUBLIC API
       ============================================================== */

    /** Inserts the P000741 “Modification” progression row. */
    @Transactional
    public void startReworkWorkflow(Long rfiId) {
        insertProgressionRow(rfiId);
        // insertTranTrackRow(rfiId);
    }

    /** Sets wpt_ADC = NOW() for the Modification row once work is finished. */
    @Transactional
    public void stampActualDateCompleted(Long rfiId) {
        jdbc.update("""
            UPDATE [qbo].WorkflowProgressionTracking
               SET wpt_ADC = GETDATE()
             WHERE wpt_process_id   = ?
               AND wpt_input_key1   = ?
               AND wpt_input_value1 = ?
        """, PROC_ID, INPUT_KEY1, rfiId);
    }

    /* ==============================================================
       INTERNAL HELPERS
       ============================================================== */

    private void insertProgressionRow(Long rfiId) {

        BigDecimal nextSr = jdbc.queryForObject(
            "SELECT COALESCE(MAX(wpt_tracking_sr_no),0) + 1 " +
            "FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime edc = now.plusDays(1);   // 10‑hour lead → +1 day, per sample

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
            VALUES (?,?,?,?,?,
                    ?, '', '',
                    ?, '', '',
                    ?, ?, 10.00,
                    ?,
                    ?, NULL,
                    ?,
                    ?, ?, ?,
                    ?,
                    1, 1, 1,
                    1, NULL)
        """;

        jdbc.update(sql,
            nextSr,
            WF_ID, WF_DESC,
            PROC_ID, PROC_DESC,
            INPUT_KEY1,
            rfiId,
            Timestamp.valueOf(now),
            Timestamp.valueOf(now),
            Timestamp.valueOf(edc),
            PRIME_EMP,
            REF_EMP,
            DIM1, DIM2, DIM3,
            Timestamp.valueOf(edc)
        );
    }

    private void insertTranTrackRow(Long rfiId) {

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        String sql = """
            INSERT INTO qbo.WorkflowTranTrack (
                wf_id,
                InputKey1Name, InputKey1Value,
                InputKey2Name, InputKey2Value,
                InputKey3Name, InputKey3Value,
                Workflow_ID,   Reference_Employee,
                Dimension1, Dimension2, Dimension3,
                Status, Workflow_StartDate, TimeZone)
            VALUES (NULL,
                    ?, ?,
                    '', '', '', '',
                    ?, ?, ?, ?, ?,
                    'CLOSED', ?, 'India Standard Time')
        """;

        jdbc.update(sql,
            INPUT_KEY1, rfiId,
            WF_ID, REF_EMP,
            DIM1, DIM2, DIM3,
            now
        );
    }
}
