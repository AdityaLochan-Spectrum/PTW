package com.example.PTW.RFI.FromQAApprovalToClientApproval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Creates the “Summary (Released)” leg of workflow W000171 when an RFI is
 * fully released by the client / AE stage.
 *
 * Mirrors the format of the row you posted:
 *
 *  wpt_tracking_sr_no | wpt_wf_id | wpt_wf_description | … | wpt_process_id=P000740
 *  wpt_process_description=Summary | … | wpt_remarks=RELEASED
 */
@Service
public class WhenReleased {

    /* ────────── workflow meta ────────── */
    private static final String WF_ID   = "W000171";
    private static final String WF_DESC = "RFI";

    /* Summary / Released */
    private static final String PROC_ID_SUM   = "P000740";
    private static final String PROC_DESC_SUM = "Summary";
    private static final String PRIME_SUM     = "6380379124";

    /* key & common metadata */
    private static final String INPUT_KEY1 = "RFIID";
    private static final String REF_EMP    = "6380379124";
    private static final String DIM1       = "Waaree";
    private static final String DIM2       = "180 MW Leap Green Energy";
    private static final String DIM3       = "Site Engineer";

    @Autowired
    private JdbcTemplate jdbc;

    /* ───────────────── public façade ───────────────── */

    /** Creates the Summary‑Released progression row for the given RFI. */
    @Transactional
    public void startWorkflow(Long rfiId) {

        BigDecimal nextSr = jdbc.queryForObject(
            "SELECT COALESCE(MAX(wpt_tracking_sr_no),0) + 1 " +
            "FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime edc = now.plusDays(1);        // same +1‑day lead as reject leg

        insertTrackingRow(nextSr,
                          PROC_ID_SUM, PROC_DESC_SUM, PRIME_SUM,
                          rfiId, now, edc,
                          "RELEASED");
    }

    /* ───────────────── internal insert ───────────────── */

    private void insertTrackingRow(BigDecimal sr,
                                   String     procId,
                                   String     procDesc,
                                   String     primeEmp,
                                   Long       rfiId,
                                   LocalDateTime start,
                                   LocalDateTime edc,
                                   String     remarks) {

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
            rfiId,        "", "",
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
