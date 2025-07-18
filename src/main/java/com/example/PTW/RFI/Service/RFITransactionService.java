package com.example.PTW.RFI.Service;


import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PTW.RFI.CustomeService.WFInitiateToQAApproval;
import com.example.PTW.RFI.CustomeService.WFModifyToQAApproval;
import com.example.PTW.RFI.CustomeService.WFModifytoSummary;
import com.example.PTW.RFI.Entity.RFITransaction;
import com.example.PTW.RFI.Repositoary.RFITransactionRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RFITransactionService {
    @Autowired
    private  RFITransactionRepository repo;

    @Autowired
    private WFInitiateToQAApproval wfqa;

    @Autowired
   private  WFModifyToQAApproval wfmodify;
    @Autowired
   private WFModifytoSummary wfSummary;


    @Autowired
    private JdbcTemplate jdbc;

    public List<RFITransaction> getByPendingWith(String pending) {
        return repo.findByPendingWith(pending);
    }

    public RFITransaction create(RFITransaction rfi) {
       
        RFITransaction var1  = repo.save(rfi);
        var1.setPendingWith("8610559975");
        wfqa.createWorkflowForRfi(rfi.getRfiId());
        // wfqa.cr(rfi.getRfiId());
        rfi.setExtraL6(rfi.getRfiId().toString());
        return var1;

    }

   
    // @Transactional
    // public RFITransaction update(Long id, RFITransaction patch) {
    
    //     // 1️⃣ fetch existing row (or fail)
    //     RFITransaction existing = repo.findById(id)
    //         .orElseThrow(() ->
    //             new EntityNotFoundException("RFITransaction ID " + id + " not found"));
    
    //     // 2️⃣ copy values one-by-one with getters / setters
    //     // ---- basic info ----
    //     existing.setRfiNo(patch.getRfiNo());
    //     existing.setRequestForInspection(patch.getRequestForInspection());
    //     existing.setRfiPass(patch.getRfiPass());
    //     existing.setSubContractorName(patch.getSubContractorName());
    
    //     // ---- dates & times ----
    //     existing.setOfferedDate(patch.getOfferedDate());
    //     existing.setSiteReachingTime(patch.getSiteReachingTime());
    //     existing.setInspectionStartTime(patch.getInspectionStartTime());
    //     existing.setInspectionEndTime(patch.getInspectionEndTime());
    
    //     // ---- work details ----
    //     existing.setDetailsOfWork(patch.getDetailsOfWork());
    //     existing.setLocationArea(patch.getLocationArea());
    //     existing.setConstructionA(patch.getConstructionA());
    //     existing.setHoldDetails(patch.getHoldDetails());
    //     existing.setDrawingNo(patch.getDrawingNo());
    
    //     // ---- status / workflow ----
    //     existing.setDocStatus(patch.getDocStatus());
    //     existing.setPendingWith(patch.getPendingWith());
    
    //     // ---- dimensions & extras ----
    //     existing.setDim1(patch.getDim1());
    //     existing.setDim2(patch.getDim2());
    //     existing.setDim3(patch.getDim3());
    //     existing.setExtraL1(patch.getExtraL1());
    //     existing.setExtraL2(patch.getExtraL2());
    //     existing.setExtraL3(patch.getExtraL3());
    //     existing.setExtraL4(patch.getExtraL4());
    //     existing.setExtraL5(patch.getExtraL5());
    //     existing.setExtraL6(patch.getExtraL6());
    //     existing.setExtraL7(patch.getExtraL7());
    
    //     existing.setRemarks(patch.getRemarks());
    //     existing.setNextStep(patch.getNextStep());
    //     existing.setActionTaken(patch.getActionTaken());
    //     existing.setResp(patch.getResp());
    //     existing.setTimeLine(patch.getTimeLine());
    //     existing.setExtraV1(patch.getExtraV1());
    //     existing.setRfiHoldNo(patch.getRfiHoldNo());

    //     String processId = getLatestProcessIdByRfiId(id);
    //     if ("P000741".equalsIgnoreCase(processId)) {
    //         wfmodify.triggerQAApprovalStep(id);
    //         updateModify(id);
    //     } else if ("P000763".equalsIgnoreCase(processId)) {
    //         wfSummary.insertModifyToSummaryRow(id);
    //         updateMOdifyToSummary(id);
    //     }
    
    //     // 3️⃣ save & return
    //     return repo.save(existing);
    // }    


    @Transactional
public RFITransaction update(Long id, RFITransaction patch, String processId) {

    // 1️⃣ fetch existing row (or fail)
    RFITransaction existing = repo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("RFITransaction ID " + id + " not found"));

    // 2️⃣ copy values one-by-one with getters / setters
    existing.setRfiNo(patch.getRfiNo());
    existing.setRequestForInspection(patch.getRequestForInspection());
    existing.setRfiPass(patch.getRfiPass());
    existing.setSubContractorName(patch.getSubContractorName());
    existing.setOfferedDate(patch.getOfferedDate());
    existing.setSiteReachingTime(patch.getSiteReachingTime());
    existing.setInspectionStartTime(patch.getInspectionStartTime());
    existing.setInspectionEndTime(patch.getInspectionEndTime());
    existing.setDetailsOfWork(patch.getDetailsOfWork());
    existing.setLocationArea(patch.getLocationArea());
    existing.setConstructionA(patch.getConstructionA());
    existing.setHoldDetails(patch.getHoldDetails());
    existing.setDrawingNo(patch.getDrawingNo());
    existing.setDocStatus(patch.getDocStatus());
    existing.setPendingWith(patch.getPendingWith());
    existing.setDim1(patch.getDim1());
    existing.setDim2(patch.getDim2());
    existing.setDim3(patch.getDim3());
    existing.setExtraL1(patch.getExtraL1());
    existing.setExtraL2(patch.getExtraL2());
    existing.setExtraL3(patch.getExtraL3());
    existing.setExtraL4(patch.getExtraL4());
    existing.setExtraL5(patch.getExtraL5());
    existing.setExtraL6(patch.getExtraL6());
    existing.setExtraL7(patch.getExtraL7());
    existing.setRemarks(patch.getRemarks());
    existing.setNextStep(patch.getNextStep());
    existing.setActionTaken(patch.getActionTaken());
    existing.setResp(patch.getResp());
    existing.setTimeLine(patch.getTimeLine());
    existing.setExtraV1(patch.getExtraV1());
    existing.setRfiHoldNo(patch.getRfiHoldNo());

    // 3️⃣ use processId parameter to determine workflow transition
    if (processId != null && !processId.isBlank()) {
        switch (processId.toUpperCase()) {
            case "P000741" -> {
                wfmodify.triggerQAApprovalStep(id);
                existing.setPendingWith("8610559975");
                updateModify(id);

            }
            case "P000763" -> {
                wfSummary.insertModifyToSummaryRow(id);
                existing.setPendingWith("Summary");
                updateMOdifyToSummary(id);

            }
            default -> {
                System.out.println("⚠️ Unknown processId: " + processId + " — no workflow triggered.");
            }
        }
    } else {
        System.out.println("⚠️ No processId passed — workflow transition skipped.");
    }

    // 4️⃣ save & return
    return repo.save(existing);
}

    
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("RFITransaction ID " + id + " not found");
        }
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public RFITransaction getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RFITransaction ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<RFITransaction> getAll() {
        return repo.findAll();
    }

    public List<RFITransaction> getByDocStatus(String docStatus) {
        return repo.findByDocStatus(docStatus);
    }

     public List<RFITransaction> getByPendingWithforQA(String pending) {
        return repo.findByPendingWith(pending);
    }

    @Transactional
    public void updateModify(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'Y',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000741'        
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

    @Transactional
    public void updateMOdifyToSummary(Long rfiId) {

        String sql = """
                UPDATE [qbo].WorkflowProgressionTracking
                   SET wpt_output1        = 'Y',
                       wpt_output2        = '',
                       wpt_output3        = '',
                       wpt_ADC            = GETDATE(),
                       wpt_DelayRemarks   = '',
                       wpt_DelayReasonCode= ''
                 WHERE wpt_process_id     = 'P000763'        
                   AND wpt_input_key1     = 'RFIID'
                   AND wpt_input_value1   = ?
                """;

        jdbc.update(sql, rfiId);
    }

  
    
 
}
