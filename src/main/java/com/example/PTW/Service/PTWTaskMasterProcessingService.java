package com.example.PTW.Service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import com.example.PTW.Model.PTWTaskMaster;
import com.example.PTW.Model.PTWTaskMasterProcessing;
import com.example.PTW.Model.PTWTaskMasterProcessingId;
import com.example.PTW.Repo.PTWTaskMasterProcessingRepo;
import com.example.PTW.Repo.PtwmasterRepo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PTWTaskMasterProcessingService {

    private final PTWTaskMasterProcessingRepo processingRepo;
    private final PtwmasterRepo masterRepo;
    private final JdbcTemplate jdbcTemplate;
    private final WFissuertoReviwer wFissuertoReviwer;
    private final WFclosePermittoExtension wfclosePermittoExtension;
    private final PtwmasterRepo ptwmasterRepo;
    private final WFExtensiontoIssuer wfExtensiontoIssuer;
    private final WfExtensioIssuerToReviwer wfExtensioIssuerToReviwer;
    private final WEReviewExtensionToNextProcess weReviewExtensionToNextProcess;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PTWTaskMasterProcessing saveForPtw(BigDecimal ptwId, PTWTaskMasterProcessing payload,String loginNumber) {

        PTWTaskMaster parent = masterRepo.findById(ptwId)
                .orElseThrow(() -> new EntityNotFoundException("No PTW found with ID " + ptwId));

        Long nextSerial = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(RemarkSerialNo), 0) + 1 " +
                        "FROM PTWTaskMasterProcessing WITH (UPDLOCK, HOLDLOCK) " +
                        "WHERE PTWId = ?",
                Long.class,
                ptwId);
        PTWTaskMasterProcessingId pk = new PTWTaskMasterProcessingId(ptwId, nextSerial);
        payload.setId(pk);
        payload.setPtwTaskMaster(parent);
       
        

        String rawFlag = payload.getApproved(); // may be null / "Y" / "F" / …
        String flag = rawFlag == null ? "" : rawFlag.trim().toUpperCase();
        if ("Y".equalsIgnoreCase(flag)) {
            wFissuertoReviwer.startIssuerToReviewer(ptwId,loginNumber);
            payload.setProcess("PTW Issuer");
           



            updatePTWIssuer(ptwId);
        } else if ("N".equalsIgnoreCase(flag)) {
            wFissuertoReviwer.startClosePermitOnly(ptwId);
            updatePTWIssuerWhenApprovedisNO(ptwId);

        }

        return processingRepo.save(payload);
    }

    public List<PTWTaskMasterProcessing> findAllByPtwId(BigDecimal ptwId) {
        return processingRepo.findAll().stream()
                .filter(e -> e.getId().getPtwId().equals(ptwId))
                .toList();
    }

    @Transactional
    public void updatePTWIssuer(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'Y',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000759'          -- Client Approval
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }

    @Transactional
    public void updatePTWIssuerWhenApprovedisNO(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'N',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000759'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }

    /**
     * Returns <code>true</code> when a row exists in
     * <code>WorkflowProgressionTracking</code> that tells us the PTW is
     * **currently in “Reviewer Approval” (process P000731)** and that step has
     * not been closed yet (<code>wpt_output1 IS NULL</code>).
     *
     * @param ptwId the PTW header ID
     */
    private boolean isReviewerApprovalPending(BigDecimal ptwId) {

        final String CHECK_REVIEWER_SQL = """
                SELECT COUNT(*)
                FROM qbo.WorkflowProgressionTracking
                WHERE wpt_process_id   = 'P000731'
                  AND wpt_input_key1   = 'PTWId'
                  AND wpt_input_value1 = ?
                  AND wpt_output1 IS NULL
                """;

        Integer count = jdbcTemplate.queryForObject(
                CHECK_REVIEWER_SQL, Integer.class, ptwId);

        return count != null && count > 0;
    }

    @Transactional
    public void updateReviewApprovaltoSummary(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'Y',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000731'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }

    @Transactional
    public void advanceIfReviewerApproved(BigDecimal ptwId) {

        if (isReviewerApprovalPending(ptwId)) {
            /* 1️⃣ Update the existing Reviewer row to “done” */
            updateReviewApprovaltoSummary(ptwId);

            /* 2️⃣ Fire the next workflow step (Summary → Close Permit, etc.) */
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PTWTaskMasterProcessing saveforReviewr(BigDecimal ptwId, PTWTaskMasterProcessing payload) {

        PTWTaskMaster parent = masterRepo.findById(ptwId)
                .orElseThrow(() -> new EntityNotFoundException("No PTW found with ID " + ptwId));

        Long nextSerial = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(RemarkSerialNo), 0) + 1 " +
                        "FROM PTWTaskMasterProcessing WITH (UPDLOCK, HOLDLOCK) " +
                        "WHERE PTWId = ?",
                Long.class,
                ptwId);
        payload.setProcess("Reviewer Approval");
        PTWTaskMasterProcessingId pk = new PTWTaskMasterProcessingId(ptwId, nextSerial);
        payload.setId(pk);
        payload.setPtwTaskMaster(parent);
        payload.getPtwTaskMaster().setExtraRemarkSerial(nextSerial);


        String rawFlag = payload.getApproved(); // may be null / "Y" / "F" / …
        String flag = rawFlag == null ? "" : rawFlag.trim().toUpperCase();
        if ("Y".equalsIgnoreCase(flag) // approval flag is "Y"
                && isReviewerApprovalPending(ptwId) // Reviewer row still open
        ) {
            updateReviewApprovaltoSummary(ptwId); // 1️⃣ mark Reviewer step done
        }

        if ("N".equalsIgnoreCase(flag) // approval flag is "N"
                && isReviewerApprovalPending(ptwId) // Reviewer row still open
        ) {
            updateReviewApprovaltoSummaryforNoalso(ptwId);// 1️⃣ mark Reviewer step done
        }

        return processingRepo.save(payload);
    }

    public List<PTWTaskMasterProcessing> getAlll() {
        return processingRepo.findAll();
    }

    public List<PTWTaskMasterProcessing> getAll() {
        return processingRepo.findAllNative(); // ← native SQL “select *”
    }

    @Transactional
    public void updateReviewApprovaltoSummaryforNoalso(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'N',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000731'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }

    public List<PTWTaskMasterProcessing> getByProcess(String process) {
        return processingRepo.findByProcess(process);
    }

    //////////////////////////////////////////////
    // at close permit
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PTWTaskMasterProcessing saveAtVlosePerit(BigDecimal ptwId, PTWTaskMasterProcessing payload) {

        PTWTaskMaster parent = masterRepo.findById(ptwId)
                .orElseThrow(() -> new EntityNotFoundException("No PTW found with ID " + ptwId));

        Long nextSerial = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(RemarkSerialNo), 0) + 1 " +
                        "FROM PTWTaskMasterProcessing WITH (UPDLOCK, HOLDLOCK) " +
                        "WHERE PTWId = ?",
                Long.class,
                ptwId);

        PTWTaskMasterProcessingId pk = new PTWTaskMasterProcessingId(ptwId, nextSerial);
        payload.setId(pk);
        payload.setPtwTaskMaster(parent);
        payload.getPtwTaskMaster().setExtraRemarkSerial(nextSerial);


        String rawFlag = payload.getApproved(); // may be null / "Y" / "F" / …
        String flag = rawFlag == null ? "" : rawFlag.trim().toUpperCase();
        if ("Close Permit".equalsIgnoreCase(flag) // approval flag is "Y"
        // Reviewer row still open
        ) {
            payload.setProcess("Close Permit");
            updateClosePermittoSummary(ptwId);
            // 1️⃣ mark Reviewer step done
        }

        if ("Send for Extension".equalsIgnoreCase(flag) // approval flag is "N"

        ) {

            payload.setProcess("Extension");
            System.out.println("HI I am calling");
            wfclosePermittoExtension.startPTWExtensionWorkflow(ptwId);
            System.out.println("HI I am called:" + ptwId);
            updateClosePermittoExtension(ptwId);
        }

        return processingRepo.save(payload);
    }

    @Transactional
    public void updateClosePermittoExtension(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'A',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000757'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }

    @Transactional
    public void updateClosePermittoSummary(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'B',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000757'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }

    public PTWTaskMaster updateRemarksAtClosePermit(BigDecimal id, PTWTaskMaster updatedRemarks,String LoginNumber) {
        // 1. Fetch and update PTWTaskMaster
        PTWTaskMaster existing = ptwmasterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("PTWTaskMaster not found with id: " + id));

        existing.setExRemarks1(updatedRemarks.getExRemarks1());
        existing.setExRemarks2(updatedRemarks.getExRemarks2());
        existing.setExRemarks3(updatedRemarks.getExRemarks3());
        existing.setExRemarks4(updatedRemarks.getExRemarks4());
        existing.setExRemarks5(updatedRemarks.getExRemarks5());
        existing.setExRemarks6(updatedRemarks.getExRemarks6());
        existing.setExRemarks7(updatedRemarks.getExRemarks7());
        existing.setExRemarks8(updatedRemarks.getExRemarks8());
        existing.setExRemarks9(updatedRemarks.getExRemarks9());
        existing.setExRemarks10(updatedRemarks.getExRemarks10());
        existing.setExRemarks11(updatedRemarks.getExRemarks11());
        existing.setExtensionDate(updatedRemarks.getExtensionDate());
        // extensionToHrs
        existing.setExtensionToHrs(updatedRemarks.getExtensionToHrs());
        // extensionDate

        // Save PTWTaskMaster changes
        ptwmasterRepo.save(existing);

        // 2. Generate next serial number for remarks
        Long nextSerial = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(RemarkSerialNo), 0) + 1 FROM PTWTaskMasterProcessing WHERE PTWId = ?",
                Long.class,
                id);

        // 3. Prepare PTWTaskMasterProcessingId (Composite Key)
        PTWTaskMasterProcessingId pk = new PTWTaskMasterProcessingId();
        pk.setPtwId(id);
        pk.setRemarkSerialNo(nextSerial);


        PTWTaskMasterProcessing remark = new PTWTaskMasterProcessing();
        remark.setId(pk); // Composite key with PTWId + serial
        remark.setPtwTaskMaster(existing);
        remark.getPtwTaskMaster().setExtraRemarkSerial(nextSerial);
        // Set only what's needed to match your desired output
        remark.setRemarkDate(LocalDate.parse("1900-01-01"));
        remark.setProcess("Extension");
        remark.setRemarks(""); // instead of null
        remark.setRemarksBy(""); // instead of null
        remark.setApproved(""); // if applicable
        remark.setImgString("");
        processingRepo.save(remark); // Insert the new processing row
        wfExtensiontoIssuer.startPTWExtensionWorkflow(id,LoginNumber);
        updateExtension(id);

        return existing;
    }

    @Transactional
    public void updateExtension(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'Y',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000762'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
//Issuer to 
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PTWTaskMasterProcessing saveAtExtensionIssuer(BigDecimal ptwId, PTWTaskMasterProcessing payload) {

        PTWTaskMaster parent = masterRepo.findById(ptwId)
                .orElseThrow(() -> new EntityNotFoundException("No PTW found with ID " + ptwId));

        Long nextSerial = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(RemarkSerialNo), 0) + 1 " +
                        "FROM PTWTaskMasterProcessing WITH (UPDLOCK, HOLDLOCK) " +
                        "WHERE PTWId = ?",
                Long.class,
                ptwId);

        PTWTaskMasterProcessingId pk = new PTWTaskMasterProcessingId(ptwId, nextSerial);
        payload.setId(pk);
        payload.setPtwTaskMaster(parent);
        payload.getPtwTaskMaster().setExtraRemarkSerial(nextSerial);


        String rawFlag = payload.getApproved(); // may be null / "Y" / "F" / …
        String flag = rawFlag == null ? "" : rawFlag.trim().toUpperCase();
        if ("Y".equalsIgnoreCase(flag) // approval flag is "Y"
        // Reviewer row still open
        ) {
            parent.setExtensionP1("Is Night shift TBT conducted with involved workmen?");
            parent.setExtensionP2("Is the illumination level > 200 Lux & no shadow formation is there?");
            parent.setExtensionP3("No lone worker is working at night");
            parent.setExtensionP4("Is emergency vehicle with driver available at site?");
            parent.setExtensionP5("Is Paramedic staff available at site?");
            parent.setExtensionP6("Dedicated work supervision from WRTL is available?");
            parent.setExtensionP7("Dedicated safety officer from WRTL is available?");
            parent.setExtensionP8("Are all workers wearing job-specific PPEs at the site?");
            parent.setExtensionP9("Are all workers wearing job-specific PPEs at the site?");
            parent.setExtensionP10("Separate workers group deployed at night shift work?");
            parent.setExtensionP11("Water facilities are checked and found available");
            payload.setProcess("Reviwer Approval");
            wfExtensioIssuerToReviwer.startReviewerApprovalWorkflow(ptwId);
            updateIssuerExtension(ptwId);
            // 1️⃣ mark Reviewer step done
        }

        if ("N".equalsIgnoreCase(flag) // approval flag is "N"

        ) {
            payload.setProcess("Issue for Extension");
            wfExtensioIssuerToReviwer.startClosePermitWorkflow(ptwId);

        }

        return processingRepo.save(payload);
    }

    @Transactional
    public void updateIssuerExtension(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'Y',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000766'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }   

    //////////////////////////////////////////////////////////////
    // for last approval 

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PTWTaskMasterProcessing saveAtReviwerExtension(BigDecimal ptwId, PTWTaskMasterProcessing payload) {

        PTWTaskMaster parent = masterRepo.findById(ptwId)
                .orElseThrow(() -> new EntityNotFoundException("No PTW found with ID " + ptwId));

        Long nextSerial = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(RemarkSerialNo), 0) + 1 " +
                        "FROM PTWTaskMasterProcessing WITH (UPDLOCK, HOLDLOCK) " +
                        "WHERE PTWId = ?",
                Long.class,
                ptwId);

        PTWTaskMasterProcessingId pk = new PTWTaskMasterProcessingId(ptwId, nextSerial);
        payload.setId(pk);
        payload.setPtwTaskMaster(parent);
        payload.getPtwTaskMaster().setExtraRemarkSerial(nextSerial);

        String rawFlag = payload.getApproved(); // may be null / "Y" / "F" / …
        String flag = rawFlag == null ? "" : rawFlag.trim().toUpperCase();
        if ("Y".equalsIgnoreCase(flag) // approval flag is "Y"
        // Reviewer row still open
        ) {
            payload.setProcess("Reviewer Approval for Extension");
            weReviewExtensionToNextProcess.startWorkflow(ptwId);
            updateRevierExtension(ptwId);
            // 1️⃣ mark Reviewer step done
        }

        if ("N".equalsIgnoreCase(flag) // approval flag is "N"

        ) {
            System.out.println("Hello i have called:"+ptwId);
            weReviewExtensionToNextProcess.startWorkflow(ptwId);
            System.out.println("Hello i have called:"+ptwId);

            updateRevierExtensionToNo(ptwId);
        }

        return processingRepo.save(payload);
    }
    
    @Transactional
    public void updateRevierExtension(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'Y',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000765'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }   

    @Transactional
    public void updateRevierExtensionToNo(BigDecimal ptwId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'N',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000765'
                   AND wpt_input_key1     = 'PTWId'
                   AND wpt_input_value1   = ?
                """;

        jdbcTemplate.update(sql, ptwId);
    }   


}