package com.example.PTW.RFI.CustomeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class WFApprovalisReleaseAndDevitation {

    /* ─────────── workflow meta ─────────── */
    private static final String WF_ID   = "W000171";
    private static final String WF_DESC = "RFI";

    private static final String PROC_ID   = "P000739";
    private static final String PROC_DESC = "AE Presentation Approval";
    private static final String PRIME_EMP = "8766425964";

    /* shared key / dims */
    private static final String INPUT_KEY1 = "RFIID";
    private static final String REF_EMP    = "6380379124";
    private static final String DIM1       = "Waaree";
    private static final String DIM2       = "180 MW Leap Green Energy";
    private static final String DIM3       = "Site Engineer";

    @Autowired
    private JdbcTemplate jdbc;

    /* ───────── public entry point ───────── */

    @Transactional
    public void startReleaseAndDeviation(Long rfiId) {
        insertProgressionRow(rfiId);   // step row
        insertTranTrackRow(rfiId);     // header row
    }

    /* ───────────────── helpers ───────────────── */

    private void insertProgressionRow(Long rfiId) {

        BigDecimal srNo = jdbc.queryForObject(
            "SELECT COALESCE(MAX(wpt_tracking_sr_no),0)+1 " +
            "FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime edc = now.plusDays(1);           // +1 day SLA

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
                wpt_Priority)
            VALUES (?,?,?,?,?,  ?,?, ?,  ?,?, ?,  ?,?, ?,  ?,  ?,?, ?,  ?,?, ?,  ?,  ?,?,?,  ?)
            """;

        jdbc.update(sql,
            srNo,
            WF_ID, WF_DESC,
            PROC_ID, PROC_DESC,
            INPUT_KEY1, "", "",          // three key names
            rfiId,        "", "",        // three values
            Timestamp.valueOf(now),      // earliest start
            Timestamp.valueOf(now),      // start
            new java.math.BigDecimal("10.00"),
            Timestamp.valueOf(edc),      // step EDC
            PRIME_EMP, null,             // prime / secondary
            REF_EMP,
            DIM1, DIM2, DIM3,
            Timestamp.valueOf(edc),      // wf‑level EDC
            1, 1, 1,                     // versions
            1);                           // priority
    }

    private void insertTranTrackRow(Long rfiId) {

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbc.update("""
            INSERT INTO [qbo].WorkflowTranTrack (
                wf_id,
                InputKey1Name, InputKey1Value,
                InputKey2Name, InputKey2Value,
                InputKey3Name, InputKey3Value,
                Workflow_ID,   Reference_Employee,
                Dimension1, Dimension2, Dimension3,
                Status, Workflow_StartDate, TimeZone)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)""",
            null,
            INPUT_KEY1, rfiId,
            "", "", "", "",
            WF_ID, REF_EMP,
            DIM1, DIM2, DIM3,
            "OPEN",                       // or "CLOSED" once done
            now,
            "India Standard Time");
    }
}
