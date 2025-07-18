package com.example.PTW.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.PTW.Model.PTWTaskMaster;
import com.example.PTW.Repo.PtwmasterRepo;

import jakarta.transaction.Transactional;

@Service
public class PTWTaskMasterService {

    @Autowired
    private PtwmasterRepo ptwmasterRepo;

    @Autowired
    private WorkFlowService wr;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WFSPlanToIssuer var1;


    // Save a PTWTaskMaster
    // @Transactional
    // public PTWTaskMaster savePTWTaskMaster(PTWTaskMaster ptwTaskMaster) {
        
    //     PTWTaskMaster var2  = ptwmasterRepo.save(ptwTaskMaster);
    //     System.out.println("this is my primary key"+var2.getPtwId());
    //     wr.insertWorkflowProgression(var2.getPtwId());
    //     wr.insertWorkflowTranTrack(var2.getPtwId());
    //      callUpdatePTWTemplateTask(var2.getPtwId());

       
    //     return var2;


    // }

    private void callUpdatePTWTemplateTask(BigDecimal ptwId) {
        String sql = "EXEC [dbo].[UpdatePTWTemplateTask] @PTWId = ?";
        
        try {
            System.out.println("DEBUG: Attempting to execute stored procedure with PTWId=" + ptwId);
            
            // Add parameter logging
            System.out.println("DEBUG: SQL to execute: " + sql);
            System.out.println("DEBUG: Parameter value: " + ptwId);
            
            int rowsAffected = jdbcTemplate.update(sql, ptwId);
            System.out.println("DEBUG: Stored procedure executed, rows affected: " + rowsAffected);
            
            System.out.println("Successfully executed UpdatePTWTemplateTask for PTWId: " + ptwId);
        } catch (Exception e) {
            System.err.println("ERROR executing UpdatePTWTemplateTask: " + e.getMessage());
            e.printStackTrace(); // Add stack trace
            throw e;
        }
    }

    // @Transactional
    // public PTWTaskMaster savePTWTaskMaster(PTWTaskMaster ptwTaskMaster) {
    //     // 1. First save the entity
       
    //     PTWTaskMaster savedEntity = ptwmasterRepo.save(ptwTaskMaster);
        
    //     // 2. Flush to ensure insert is executed
    //     ptwmasterRepo.flush();

        
    //     System.out.println("Saved entity with ID: " + savedEntity.getPtwId());
    //     // 3. Now execute dependent operations
    //     executeDependentOperations(savedEntity.getPtwId());
       
    //     return savedEntity;
    // }

    @Transactional
    public PTWTaskMaster savePTWTaskMaster(PTWTaskMaster ptwTaskMaster, String loginNumber) {
        // 1. First save the entity
       
        PTWTaskMaster savedEntity = ptwmasterRepo.save(ptwTaskMaster);
        savedEntity.setPendingWith(loginNumber);
        // 2. Flush to ensure insert is executed
        ptwmasterRepo.flush();

        
        System.out.println("Saved entity with ID: " + savedEntity.getPtwId());
        // 3. Now execute dependent operations
        executeDependentOperations(savedEntity.getPtwId(),loginNumber);
       
        return savedEntity;
    }
    
    private void executeDependentOperations(BigDecimal ptwId,String loginNumber) {
        try {

            wr.insertWorkflowProgression(ptwId,loginNumber);
            wr.insertWorkflowTranTrack(ptwId,loginNumber);
            
            // Modified procedure call with explicit flush
            callUpdatePTWTemplateTask(ptwId);
        } catch (Exception e) {
            throw new RuntimeException("Dependent operation failed", e);
        }
    }

    // Get all PTWTaskMasters
    public List<PTWTaskMaster> getAllPTWTaskMasters() {
        return ptwmasterRepo.findAll();
    }

    // Get a single PTWTaskMaster by ID
    public PTWTaskMaster getPTWTaskMasterById(BigDecimal ptwId) {
        return ptwmasterRepo.findById(ptwId).orElseThrow(()-> new RuntimeException("Not Found the ID"+ptwId));
    }

    // Delete PTWTaskMaster by ID
    public void deletePTWTaskMaster(BigDecimal ptwId) {
        PTWTaskMaster var1=ptwmasterRepo.findById(ptwId).orElseThrow(()-> new RuntimeException("Not Found the ID"+ptwId));
        ptwmasterRepo.deleteById(ptwId);
    }

    // public PTWTaskMaster updatePTWTaskMaster(BigDecimal ptwId, PTWTaskMaster updatedTask) {
    //     Optional<PTWTaskMaster> existingTaskOptional = ptwmasterRepo.findById(ptwId);

    //     if (existingTaskOptional.isPresent()) {
    //         PTWTaskMaster existingTask = existingTaskOptional.get();
            
    //         // Update fields
    //         //existingTask.setNameofProject(updatedTask.getNameofProject());
    //         existingTask.setTemplateCode(updatedTask.getTemplateCode());
    //         //existingTask.setDetailsOfWork(updatedTask.getDetailsOfWork());
    //         existingTask.setLocation(updatedTask.getLocation());
    //         existingTask.setNameOfCompanyPerson(updatedTask.getNameOfCompanyPerson());
    //         // existingTask.setPermitDate(updatedTask.getPermitDate());
    //         existingTask.setPtwStartDate(updatedTask.getPtwStartDate());

    //         return ptwmasterRepo.save(existingTask);
    //     } else {
    //         throw new RuntimeException("PTWTaskMaster with ID " + ptwId + " not found.");
    //     }
    // }


    public PTWTaskMaster updatePTWTaskMaster(BigDecimal ptwId, PTWTaskMaster inputTask) {

        Optional<PTWTaskMaster> existingTaskOptional = ptwmasterRepo.findById(ptwId);
    
        if (existingTaskOptional.isPresent()) {
            PTWTaskMaster task = existingTaskOptional.get();
    
            // Update required fields from input entity
            task.setTemplateCode(inputTask.getTemplateCode());
            task.setLocation(inputTask.getLocation());
            task.setNameOfCompanyPerson(inputTask.getNameOfCompanyPerson());
            task.setSelectWork(inputTask.getSelectWork());
    
            task.setDim1(inputTask.getDim1());
            task.setDim2(inputTask.getDim2());
            task.setDim3(inputTask.getDim3());
    
            task.setPermitDateNew(inputTask.getPermitDateNew());
            task.setDocStatus(inputTask.getDocStatus());
            task.setDetailsOfWork(inputTask.getDetailsOfWork());
    
            // Optionally update audit fields if needed
            task.setModifiedOn(LocalDateTime.now());
            task.setModifiedBy(inputTask.getModifiedBy()); // Make sure you send this
    
            // Will auto-set PTWEndDate from PTWStartDate
            return ptwmasterRepo.save(task);
    
        } else {
            throw new RuntimeException("PTWTaskMaster with ID " + ptwId + " not found.");
        }
    }

    /**
 * Updates all remaining columns of PTWTaskMaster that are NOT part of the
 * “basic” patch. A null in the incoming entity leaves the existing value intact.
 */
@Transactional
public PTWTaskMaster updateRemainingFields(BigDecimal ptwId,
                                           PTWTaskMaster src,String loginNumber) {

    PTWTaskMaster t = ptwmasterRepo.findById(ptwId)
            .orElseThrow(() -> new RuntimeException("PTW " + ptwId + " not found"));

    /* ---------- header / status ---------- */
    if (src.getPtwCode()          != null) t.setPtwCode(src.getPtwCode());
    if (src.getAcivityCompleted() != null) t.setAcivityCompleted(src.getAcivityCompleted());
    if (src.getActualEndDate()    != null) t.setActualEndDate(src.getActualEndDate());
    if (src.getDuration()         != null) t.setDuration(src.getDuration());
    if (src.getRemark()           != null) t.setRemark(src.getRemark());
    if (src.getPtwDate()          != null) t.setPtwDate(src.getPtwDate());
    if (src.getApproved()         != null) t.setApproved(src.getApproved());
    if (src.getPendingWith()      != null) t.setPendingWith(src.getPendingWith());
    if (src.getLongDescription()  != null) t.setLongDescription(src.getLongDescription());
    if (src.getPoDate()           != null) t.setPoDate(src.getPoDate());

    /* ---------- template tasks (1-10) ---------- */
    if (src.getTemplateTask1()  != null) t.setTemplateTask1(src.getTemplateTask1());
    if (src.getTemplateTask2()  != null) t.setTemplateTask2(src.getTemplateTask2());
    if (src.getTemplateTask3()  != null) t.setTemplateTask3(src.getTemplateTask3());
    if (src.getTemplateTask4()  != null) t.setTemplateTask4(src.getTemplateTask4());
    if (src.getTemplateTask5()  != null) t.setTemplateTask5(src.getTemplateTask5());
    if (src.getTemplateTask6()  != null) t.setTemplateTask6(src.getTemplateTask6());
    if (src.getTemplateTask7()  != null) t.setTemplateTask7(src.getTemplateTask7());
    if (src.getTemplateTask8()  != null) t.setTemplateTask8(src.getTemplateTask8());
    if (src.getTemplateTask9()  != null) t.setTemplateTask9(src.getTemplateTask9());
    if (src.getTemplateTask10() != null) t.setTemplateTask10(src.getTemplateTask10());

    /* ---------- durations (1-25) ---------- */
    if (src.getDuration1()  != null) t.setDuration1(src.getDuration1());
    if (src.getDuration2()  != null) t.setDuration2(src.getDuration2());
    if (src.getDuration3()  != null) t.setDuration3(src.getDuration3());
    if (src.getDuration4()  != null) t.setDuration4(src.getDuration4());
    if (src.getDuration5()  != null) t.setDuration5(src.getDuration5());
    if (src.getDuration6()  != null) t.setDuration6(src.getDuration6());
    if (src.getDuration7()  != null) t.setDuration7(src.getDuration7());
    if (src.getDuration8()  != null) t.setDuration8(src.getDuration8());
    if (src.getDuration9()  != null) t.setDuration9(src.getDuration9());
    if (src.getDuration10() != null) t.setDuration10(src.getDuration10());
    if (src.getDuration11() != null) t.setDuration11(src.getDuration11());
    if (src.getDuration12() != null) t.setDuration12(src.getDuration12());
    if (src.getDuration13() != null) t.setDuration13(src.getDuration13());
    if (src.getDuration14() != null) t.setDuration14(src.getDuration14());
    if (src.getDuration15() != null) t.setDuration15(src.getDuration15());
    if (src.getDuration16() != null) t.setDuration16(src.getDuration16());
    if (src.getDuration17() != null) t.setDuration17(src.getDuration17());
    if (src.getDuration18() != null) t.setDuration18(src.getDuration18());
    if (src.getDuration19() != null) t.setDuration19(src.getDuration19());
    if (src.getDuration20() != null) t.setDuration20(src.getDuration20());
    if (src.getDuration21() != null) t.setDuration21(src.getDuration21());
    if (src.getDuration22() != null) t.setDuration22(src.getDuration22());
    if (src.getDuration23() != null) t.setDuration23(src.getDuration23());
    if (src.getDuration24() != null) t.setDuration24(src.getDuration24());
    if (src.getDuration25() != null) t.setDuration25(src.getDuration25());

    /* ---------- equipment ---------- */
    if (src.getLoadToBeLifted()      != null) t.setLoadToBeLifted(src.getLoadToBeLifted());
    if (src.getNameOfEquipment()     != null) t.setNameOfEquipment(src.getNameOfEquipment());
    if (src.getCapacityOfEquipment() != null) t.setCapacityOfEquipment(src.getCapacityOfEquipment());
    if (src.getCertificateOfEquipment() != null)
         t.setCertificateOfEquipment(src.getCertificateOfEquipment());

    /* ---------- remarks (1-25) ---------- */
    if (src.getRemarks1()  != null) t.setRemarks1(src.getRemarks1());
    if (src.getRemarks2()  != null) t.setRemarks2(src.getRemarks2());
    if (src.getRemarks3()  != null) t.setRemarks3(src.getRemarks3());
    if (src.getRemarks4()  != null) t.setRemarks4(src.getRemarks4());
    if (src.getRemarks5()  != null) t.setRemarks5(src.getRemarks5());
    if (src.getRemarks6()  != null) t.setRemarks6(src.getRemarks6());
    if (src.getRemarks7()  != null) t.setRemarks7(src.getRemarks7());
    if (src.getRemarks8()  != null) t.setRemarks8(src.getRemarks8());
    if (src.getRemarks9()  != null) t.setRemarks9(src.getRemarks9());
    if (src.getRemarks10() != null) t.setRemarks10(src.getRemarks10());
    if (src.getRemarks11() != null) t.setRemarks11(src.getRemarks11());
    if (src.getRemarks12() != null) t.setRemarks12(src.getRemarks12());
    if (src.getRemarks13() != null) t.setRemarks13(src.getRemarks13());
    if (src.getRemarks14() != null) t.setRemarks14(src.getRemarks14());
    if (src.getRemarks15() != null) t.setRemarks15(src.getRemarks15());
    if (src.getRemarks16() != null) t.setRemarks16(src.getRemarks16());
    if (src.getRemarks17() != null) t.setRemarks17(src.getRemarks17());
    if (src.getRemarks18() != null) t.setRemarks18(src.getRemarks18());
    if (src.getRemarks19() != null) t.setRemarks19(src.getRemarks19());
    if (src.getRemarks20() != null) t.setRemarks20(src.getRemarks20());
    if (src.getRemarks21() != null) t.setRemarks21(src.getRemarks21());
    if (src.getRemarks22() != null) t.setRemarks22(src.getRemarks22());
    if (src.getRemarks23() != null) t.setRemarks23(src.getRemarks23());
    if (src.getRemarks24() != null) t.setRemarks24(src.getRemarks24());
    if (src.getRemarks25() != null) t.setRemarks25(src.getRemarks25());

    /* ---------- timing & permit window ---------- */
    if (src.getFromHours()       != null) t.setFromHours(src.getFromHours());
    if (src.getToHours()         != null) t.setToHours(src.getToHours());
    if (src.getToUndertakeJob()  != null) t.setToUndertakeJob(src.getToUndertakeJob());
    if (src.getPermitIssueTo()   != null) t.setPermitIssueTo(src.getPermitIssueTo());
    if (src.getTotalHrs()        != null) t.setTotalHrs(src.getTotalHrs());
    if (src.getPermitNo()        != null) t.setPermitNo(src.getPermitNo());
    if (src.getVoltage()         != null) t.setVoltage(src.getVoltage());
    if (src.getSafetyClearance() != null) t.setSafetyClearance(src.getSafetyClearance());

    /* ---------- Ex-check flags 1-11 ---------- */
    if (src.getExCheck1()  != null) t.setExCheck1(src.getExCheck1());
    if (src.getExCheck2()  != null) t.setExCheck2(src.getExCheck2());
    if (src.getExCheck3()  != null) t.setExCheck3(src.getExCheck3());
    if (src.getExCheck4()  != null) t.setExCheck4(src.getExCheck4());
    if (src.getExCheck5()  != null) t.setExCheck5(src.getExCheck5());
    if (src.getExCheck6()  != null) t.setExCheck6(src.getExCheck6());
    if (src.getExCheck7()  != null) t.setExCheck7(src.getExCheck7());
    if (src.getExCheck8()  != null) t.setExCheck8(src.getExCheck8());
    if (src.getExCheck9()  != null) t.setExCheck9(src.getExCheck9());
    if (src.getExCheck10() != null) t.setExCheck10(src.getExCheck10());
    if (src.getExCheck11() != null) t.setExCheck11(src.getExCheck11());

    /* ---------- extension data ---------- */
    if (src.getExtensionFrom() != null) t.setExtensionFrom(src.getExtensionFrom());
    if (src.getExtensionTo()   != null) t.setExtensionTo(src.getExtensionTo());

    if (src.getExtensionP1()  != null) t.setExtensionP1(src.getExtensionP1());
    if (src.getExtensionP2()  != null) t.setExtensionP2(src.getExtensionP2());
    if (src.getExtensionP3()  != null) t.setExtensionP3(src.getExtensionP3());
    if (src.getExtensionP4()  != null) t.setExtensionP4(src.getExtensionP4());
    if (src.getExtensionP5()  != null) t.setExtensionP5(src.getExtensionP5());
    if (src.getExtensionP6()  != null) t.setExtensionP6(src.getExtensionP6());
    if (src.getExtensionP7()  != null) t.setExtensionP7(src.getExtensionP7());
    if (src.getExtensionP8()  != null) t.setExtensionP8(src.getExtensionP8());
    if (src.getExtensionP9()  != null) t.setExtensionP9(src.getExtensionP9());
    if (src.getExtensionP10() != null) t.setExtensionP10(src.getExtensionP10());
    if (src.getExtensionP11() != null) t.setExtensionP11(src.getExtensionP11());

    if (src.getSupervisorName() != null) t.setSupervisorName(src.getSupervisorName());
    if (src.getExtensionDate()  != null) t.setExtensionDate(src.getExtensionDate());

    /* ---------- Ex-remarks 1-11 ---------- */
    if (src.getExRemarks1()  != null) t.setExRemarks1(src.getExRemarks1());
    if (src.getExRemarks2()  != null) t.setExRemarks2(src.getExRemarks2());
    if (src.getExRemarks3()  != null) t.setExRemarks3(src.getExRemarks3());
    if (src.getExRemarks4()  != null) t.setExRemarks4(src.getExRemarks4());
    if (src.getExRemarks5()  != null) t.setExRemarks5(src.getExRemarks5());
    if (src.getExRemarks6()  != null) t.setExRemarks6(src.getExRemarks6());
    if (src.getExRemarks7()  != null) t.setExRemarks7(src.getExRemarks7());
    if (src.getExRemarks8()  != null) t.setExRemarks8(src.getExRemarks8());
    if (src.getExRemarks9()  != null) t.setExRemarks9(src.getExRemarks9());
    if (src.getExRemarks10() != null) t.setExRemarks10(src.getExRemarks10());
    if (src.getExRemarks11() != null) t.setExRemarks11(src.getExRemarks11());

    /* ---------- audit ---------- */
    if (src.getModifiedBy() != null) t.setModifiedBy(src.getModifiedBy());
    t.setModifiedOn(LocalDateTime.now());
        //   if(t.getPendingWith().equals("8766425964")){
        //     t.setPendingWith("9908442275");
        //   } else {
        //     t.setPendingWith("8766425964");
        //   }                                 
                                            
     
    var1.insertWorkflowProgression(ptwId,loginNumber);
    System.out.println("Hello called");
    updateINPlantPlan(ptwId);
    System.out.println("Hello calling over:"+ptwId);

    /* ptwEndDate is re-calculated by @PreUpdate */
    return ptwmasterRepo.save(t);
}

@Transactional
public void updateINPlantPlan(BigDecimal ptwId) {

    String sql = """
            UPDATE [qbo].WorkflowProgressionTracking
               SET wpt_output1        = 'I',
                   wpt_output2        = '',
                   wpt_output3        = '',
                   wpt_ADC            = GETDATE(),
                   wpt_DelayRemarks   = '',
                   wpt_DelayReasonCode= ''
             WHERE wpt_process_id     = 'P000730'          -- Client Approval
               AND wpt_input_key1     = 'PTWId'
               AND wpt_input_value1   = ?
            """;

            jdbcTemplate.update(sql, ptwId);
}

public List<PTWTaskMaster> getByPendingWith(String pendingWith) {
    return ptwmasterRepo.findByPendingWith(pendingWith);
}

public Optional<PTWTaskMaster> getTopPendingTask(Integer ptwId, String employee) {
    return ptwmasterRepo.findTopPendingTaskByPTWIdAndEmployee(ptwId, employee);
}

    
}

