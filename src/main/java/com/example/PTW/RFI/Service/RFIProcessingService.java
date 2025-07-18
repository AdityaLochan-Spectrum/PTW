package com.example.PTW.RFI.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.PTW.RFI.CustomeService.WFApprovalIsReject;
import com.example.PTW.RFI.CustomeService.WFApprovalIsRework;
import com.example.PTW.RFI.CustomeService.WFApprovalisReleaseAndDevitation;
import com.example.PTW.RFI.CustomeService.WFApprovalisReleased;
import com.example.PTW.RFI.Entity.RFIProcessing;
import com.example.PTW.RFI.Entity.RFITransaction;
import com.example.PTW.RFI.Entity.RfiRemarkId;
import com.example.PTW.RFI.FromQAApprovalToClientApproval.WhenReleased;
import com.example.PTW.RFI.FromQAApprovalToClientApproval.WhenRework;
import com.example.PTW.RFI.Repositoary.RFIProcessingRepository;
import com.example.PTW.RFI.Repositoary.RFITransactionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Lombok injects all final fields
@Transactional // one Tx for each public method
public class RFIProcessingService {

    private final JdbcTemplate jdbc;
    private final RFIProcessingRepository repository;
    private final RFITransactionRepository transactionRepo;
    private final JdbcTemplate jdbcTemplate;

    private final WFApprovalisReleased wfReleased;
    private final WFApprovalIsRework wfRework;
    private final WFApprovalIsReject wfReject;
    private final WFApprovalisReleaseAndDevitation wfApprovalReleaseDeviation;
    private final WhenReleased var1;
    private final WhenRework var2;
    private static final String FINAL_PROCESS_ID = "P000739"; // AERepresentationApproval
    private static final String WF_ID = "W000171";

    /* ───────────────────────── Basic CRUD ────────────────────────── */

    public List<RFIProcessing> getAll() {
        return repository.findAll();
    }

    // public Optional<RFIProcessing> getById(RfiRemarkId id) {

    // return repository.findById(id);
    // }

    public RFIProcessing getById(RfiRemarkId id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "RFI‑processing not found for id: " + id));
    }

    public void delete(RfiRemarkId id) {
        repository.deleteById(id);
    }

    public RFIProcessing update(RfiRemarkId id, RFIProcessing updated) {
        return repository.findById(id)
                .map(existing -> {
                    updated.setId(id); // preserve PK
                    updated.setRfiTransaction(existing.getRfiTransaction());
                    return repository.save(updated);
                })
                .orElseThrow(() -> new EntityNotFoundException("No remark " + id));
    }

    /* ───────────────────── Add a NEW remark row ───────────────────── */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RFIProcessing saveForRfi(Long rfiId, RFIProcessing payload) {

        /* 1. Verify parent RFI exists */
        RFITransaction parent = transactionRepo.findById(rfiId)
                .orElseThrow(() -> new EntityNotFoundException("No RFITransaction #" + rfiId));

        /* 2. Determine the next serial number for this RFI (thread‑safe) */
        Long nextSerial = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(RemarkSerialNo),0)+1 " +
                        "FROM RFIProcessing WITH (UPDLOCK, HOLDLOCK) WHERE RFIID = ?",
                Long.class,
                rfiId);

        /* 3. Build composite key and link parent */
        RfiRemarkId pk = new RfiRemarkId(rfiId, nextSerial);
        payload.setId(pk);
        payload.setRfiTransaction(parent);
        payload.getRfiTransaction().setExtraL6(String.valueOf(rfiId));
        payload.getRfiTransaction().setExtraVar3(nextSerial);
        payload.setExtraDateFromTrasncation(payload.getRfiTransaction().getOfferedDate());

        /* 4. Persist */
        RFIProcessing saved = repository.save(payload);

        /* 5. Trigger workflow according to decision */
        String decision = saved.getApproved();
        if ("Released".equalsIgnoreCase(decision)) {
            payload.getRfiTransaction().setPendingWith("8766425964");
            payload.setProcess("QA Approval");
            wfReleased.startWorkflow(rfiId);
            updateQaApproval(rfiId);

            //www
            // wfReleased.stampActualDateCompleted(rfiId);
        } else if ("Rework".equalsIgnoreCase(decision)) {
            payload.getRfiTransaction().setPendingWith("6380379124");
            payload.setProcess("QA Approval");

            payload.getRfiTransaction().setDocStatus("Modification");

            wfRework.startReworkWorkflow(rfiId);

            updateRework(rfiId);
            // wfReleased.stampActualDateCompleted(rfiId);
        } else if ("Reject".equalsIgnoreCase(decision)) {
            wfReject.startRejectWorkflow(rfiId);
            payload.getRfiTransaction().setPendingWith("Summary");
            payload.setProcess("QA Approval");

            payload.getRfiTransaction().setDocStatus("Summary");


            updateReject(rfiId);

            // wfReject.stampActualDateCompleted(rfiId);
        } else if ("Release and Deviation".equalsIgnoreCase(decision)) {
            payload.getRfiTransaction().setPendingWith("8766425964");
            payload.setProcess("QA Approval");


            wfApprovalReleaseDeviation.startReleaseAndDeviation(rfiId);
            updateQaReleaseWithDevation(rfiId);
        }

        return saved;
    }

    /**
     * Stamps the QA‑Approval row (P000738) for the given RFI:
     * wpt_output1 = 'G'
     * wpt_output2/3 = ''
     * wpt_ADC = current date‑time
     * wpt_DelayRemarks = ''
     * wpt_DelayReasonCode= ''
     */
    @Transactional
    public void updateQaApproval(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'G',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000738'          -- QA Approval
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    @Transactional
    public void updateQaReleaseWithDevation(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'H',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000738'          -- QA Approval
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    @Transactional
    public void updateReject(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'J',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000738'          -- QA Approval
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    @Transactional
    public void updateRework(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'I',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000738'          -- QA Approval
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isFinalAeRelease(Long rfiId) {

        String latestProcessForRfi = jdbc.queryForObject("""
                SELECT TOP 1 wpt_process_id
                  FROM qbo.WorkflowProgressionTracking
                 WHERE wpt_wf_id        = ?
                   AND wpt_input_key1   = 'RFIID'
                   AND wpt_input_value1 = ?
                 ORDER BY wpt_tracking_sr_no DESC
                """,
                String.class,
                WF_ID,
                rfiId);

        return FINAL_PROCESS_ID.equalsIgnoreCase(latestProcessForRfi);
    }

    public RFIProcessing updateRFIProcessing(Long rfiId, Long remarkSerialNo, RFIProcessing updated) {

        RfiRemarkId pk = new RfiRemarkId(rfiId, remarkSerialNo);

        RFIProcessing existing = repository.findById(pk)
                .orElseThrow(() -> new EntityNotFoundException("RFIProcessing not found for ID: " + pk));

        // Update fields manually (only those allowed to be updated)
        existing.setRemarkDate(updated.getRemarkDate());
        existing.setRemarks(updated.getRemarks());
        existing.setRemarksBy(updated.getRemarksBy());
        existing.setApproved(updated.getApproved());
        existing.setProcess(updated.getProcess());
        existing.setOfferWorkAsPer(updated.getOfferWorkAsPer());
        existing.setRfiPass(updated.getRfiPass());
        existing.setOfferedTime(updated.getOfferedTime());
        existing.setSiteReachingTime(updated.getSiteReachingTime());
        existing.setInspectionStart(updated.getInspectionStart());
        existing.setInspectionEnd(updated.getInspectionEnd());
        existing.setObservedDefectState(updated.getObservedDefectState());
        existing.setNextStep(updated.getNextStep());
        existing.setAction(updated.getAction());
        existing.setResp(updated.getResp());
        existing.setTimeline(updated.getTimeline());
        existing.setExtra1(updated.getExtra1());
        existing.setExtra2(updated.getExtra2());
        existing.setHoldRfiNo(updated.getHoldRfiNo());
        existing.setStartTime(updated.getStartTime());
        existing.setSiteTime(updated.getSiteTime());

        if ("Released".equalsIgnoreCase(updated.getApproved())) {
            existing.getRfiTransaction().setPendingWith("Summary");
            existing.setProcess("AE Presentation Approval");

            var1.startWorkflow(rfiId); // ← call this service
            updateReleasedInClientApproval(rfiId);
        }

        if ("Release and Deviation".equalsIgnoreCase(updated.getApproved())) {
            existing.getRfiTransaction().setPendingWith("Summary");
            existing.getRfiTransaction().setDocStatus("Summary");
            existing.setProcess("AE Presentation Approval");


            var1.startWorkflow(rfiId); // ← call this service
            updateReleasedAndDevationInClientApproval(rfiId);
        }

        if ("Reject".equalsIgnoreCase(updated.getApproved())) {
            existing.getRfiTransaction().setPendingWith("Summary");
            existing.setProcess("AE Presentation Approval");

            existing.getRfiTransaction().setDocStatus("Summary");

            var1.startWorkflow(rfiId); // ← call this service
            updateReleasedAndDevationInClientApproval(rfiId);
        }

        if ("Rework".equalsIgnoreCase(updated.getApproved())) {
            existing.getRfiTransaction().setPendingWith("6380379124");
            existing.setProcess("AE Presentation Approval");

            existing.getRfiTransaction().setDocStatus("Rework");

            var2.startReworkWorkflow(rfiId);
            updateReworkClientApproval(rfiId); // ← call this service
        }

        return repository.save(existing);
    }

    @Transactional
    public void updateReleasedInClientApproval(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'I',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000739'          -- Client Approval
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    @Transactional
    public void updateReleasedAndDevationInClientApproval(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'L',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000739'          -- Client Approval
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    @Transactional
    public void updateRejectedInClientApproval(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'O',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000739'          -- Client Approval
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    @Transactional
    public void updateReworkClientApproval(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'M',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000739'          -- Client Approval
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    // public void updateAndTriggerWorkflow(Long rfiId, Long remarkSerialNo,
    // RFIProcessing input) {

    // RFIProcessing updated = updateRFIProcessing(rfiId, remarkSerialNo, input);

    // }

    public List<RFIProcessing> getAllPendingWith(String pendingWith) {
        return repository.findByRfiTransaction_PendingWith(pendingWith);
    }

}
