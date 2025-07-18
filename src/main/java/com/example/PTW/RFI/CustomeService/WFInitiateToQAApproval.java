package com.example.PTW.RFI.CustomeService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

/**
 * Creates the two WorkflowProgressionTracking rows
 * 1️⃣ QA Approval → P000738
 * 2️⃣ Modify RFI → P000763
 * and the corresponding WorkflowTranTrack header row
 * whenever a new RFI (identified by its primary‑key long rfiId)
 * is created.
 */
@Service
public class WFInitiateToQAApproval {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /* ─────────── Workflow constants ─────────── */
    private static final String WF_ID = "W000171";
    private static final String WF_DESCRIPTION = "RFI";

    /* QA‑Approval step */
    private static final String PROC_ID_QA = "P000738";
    private static final String PROC_DESC_QA = "QA Approval";
    private static final String PRIME_EMP_QA = "8610559975";

    /* Modify‑RFI step */
    private static final String PROC_ID_MOD = "P000763";
    private static final String PROC_DESC_MOD = "Modify RFI Details";
    private static final String PRIME_EMP_MOD = "6380379124";

    /* Common metadata */
    private static final String REFERENCE_EMPLOYEE = "6380379124";
    private static final String DIM1 = "Waaree";
    private static final String DIM2 = "180 MW Leap Green Energy";
    private static final String DIM3 = "Site Engineer";

    private static final BigDecimal LEAD_TIME = new BigDecimal("10.00");
    private static final int VERSION = 1;

    /* Tran‑Track constants */
    private static final String INPUT_KEY1_NAME = "RFIID";
    private static final String EMPTY = "";
    private static final String STATUS_CLOSED = "CLOSED";
    private static final String TIME_ZONE = "India Standard Time";
    /* ─────────────────────────────────────────── */

    /*------------------------------------------------------------*/
    @Transactional
    public void createWorkflowForRfi(Long rfiId) {

        /* —— WorkflowProgressionTracking —— */
        BigDecimal nextSrNo = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(wpt_tracking_sr_no),0) + 1 " +
                        "FROM [qbo].WorkflowProgressionTracking",
                BigDecimal.class);

        insertTrackingRow(nextSrNo, PROC_ID_QA, PROC_DESC_QA, rfiId, PRIME_EMP_QA);
        insertTrackingRow(nextSrNo.add(BigDecimal.ONE), PROC_ID_MOD, PROC_DESC_MOD, rfiId, PRIME_EMP_MOD);

        /* —— WorkflowTranTrack —— */
        insertWorkflowTranTrack(rfiId);
    }

    /*------------------------------------------------------------*/
    private void insertTrackingRow(BigDecimal trackingSrNo,
            String processId,
            String processDesc,
            Long rfiId,
            String primeEmployee) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime edc = now.plusDays(1);

        // 25 question‑marks ↔ 25 parameters
        String sql = """
                INSERT INTO [qbo].WorkflowProgressionTracking (
                wpt_tracking_sr_no, wpt_wf_id, wpt_wf_description,
                wpt_process_id,    wpt_process_description,
                wpt_input_key1, wpt_input_key2, wpt_input_key3,
                wpt_input_value1, wpt_input_value2, wpt_input_value3,
                wpt_start_date, wpt_lead_time, wpt_EDC,
                wpt_prime_resp_employee, wpt_reference_employee,
                wpt_dim1, wpt_dim2, wpt_dim3,
                wpt_wf_EDC,
                wm_version, wm_revision, wpt_process_version,
                wpt_Priority, wpt_TransactionError_Ind)
                VALUES (?,?,?,?,?,          --  1‑5   ← five placeholders now!
                ?,?,?, ?,?,?,       --  6‑11  (6 placeholders)
                ?,?,? ,             -- 12‑14  (3)
                ?,?, ?,?,?,         -- 15‑19  (5)
                ?,                  -- 20     (1)
                ?,?,? ,             -- 21‑23  (3)
                ?,?)                -- 24‑25  (2)
                """;

        jdbcTemplate.update(sql,
                trackingSrNo, // 1
                WF_ID, WF_DESCRIPTION, // 2‑3
                processId, processDesc, // 4‑5

                INPUT_KEY1_NAME, EMPTY, EMPTY, // 6‑8
                rfiId, EMPTY, EMPTY, // 9‑11

                Timestamp.valueOf(now), // 12
                LEAD_TIME, // 13
                Timestamp.valueOf(edc), // 14

                primeEmployee, // 15
                REFERENCE_EMPLOYEE, // 16
                DIM1, DIM2, DIM3, // 17‑19

                Timestamp.valueOf(edc), // 20
                VERSION, VERSION, VERSION, // 21‑23
                1, // 24
                null); // 25
    }

    /*------------------------------------------------------------*/
    private void insertWorkflowTranTrack(Long rfiId) {

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        String sql = """
                INSERT INTO [qbo].WorkflowTranTrack (
                    wf_id,
                    InputKey1Name, InputKey1Value,
                    InputKey2Name, InputKey2Value,
                    InputKey3Name, InputKey3Value,
                    Workflow_ID,   Reference_Employee,
                    Dimension1, Dimension2, Dimension3,
                    Status, Workflow_StartDate, TimeZone)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;

        jdbcTemplate.update(sql,
                null, // wf_id (unused)
                INPUT_KEY1_NAME, rfiId,
                EMPTY, EMPTY,
                EMPTY, EMPTY,
                WF_ID,
                REFERENCE_EMPLOYEE,
                DIM1, DIM2, DIM3,
                STATUS_CLOSED,
                now,
                TIME_ZONE);
    }
}