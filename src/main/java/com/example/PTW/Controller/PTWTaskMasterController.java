package com.example.PTW.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.PTW.Model.PTWTaskMaster;
import com.example.PTW.Model.PTWTaskMasterProcessing;
import com.example.PTW.Service.PTWTaskMasterService;

@RestController
@RequestMapping("/api/ptw")
public class PTWTaskMasterController {

    @Autowired
    private PTWTaskMasterService ptwTaskMasterService;

    // Create a new PTWTaskMaster
    // @PostMapping("/save")
    // public PTWTaskMaster savePTWTaskMaster(@RequestBody PTWTaskMaster ptwTaskMaster) {
        
    //     return ptwTaskMasterService.savePTWTaskMaster(ptwTaskMaster);
    // }

     @PostMapping("/save/{loginNumber}")
    public PTWTaskMaster savePTWTaskMaster(@RequestBody PTWTaskMaster ptwTaskMaster,@PathVariable String loginNumber) {
        
        return ptwTaskMasterService.savePTWTaskMaster(ptwTaskMaster,loginNumber);
    }

    // Get all PTWTaskMasters
    @GetMapping("/all")
    public List<PTWTaskMaster> getAllPTWTaskMasters() {
        return ptwTaskMasterService.getAllPTWTaskMasters();
    }

    // Get a PTWTaskMaster by ID
    @GetMapping("/getBy/{id}")
    public PTWTaskMaster getPTWTaskMasterById(@PathVariable BigDecimal id) {
        return ptwTaskMasterService.getPTWTaskMasterById(id);
    }

    // Delete a PTWTaskMaster by ID
    @DeleteMapping("/Delete/{id}")
    public String deletePTWTaskMaster(@PathVariable BigDecimal id) {
        ptwTaskMasterService.deletePTWTaskMaster(id);
        return "Deleted Sucessfully";
    }


    // @PutMapping("/update/{ptwId}")
    // public ResponseEntity<String> updatePTWTaskMaster(@PathVariable BigDecimal ptwId, @RequestBody PTWTaskMaster updatedTask) {
    //     try {
    //         ptwTaskMasterService.updatePTWTaskMaster(ptwId, updatedTask);
    //         return ResponseEntity.ok("PTWTaskMaster updated successfully.");
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.notFound().build();
    //     }
    // }


    @PatchMapping("/{id}/basic")
    public ResponseEntity<PTWTaskMaster> patchBasic(
            @PathVariable BigDecimal id,
            @RequestBody PTWTaskMaster body) {

        PTWTaskMaster updated = ptwTaskMasterService.updatePTWTaskMaster(id, body);
        return ResponseEntity.ok(updated);
    }

    /* -------------------------------------------------------------
       PATCH  /api/ptw/{id}/others
       – updates all remaining mutable columns
       ------------------------------------------------------------- */
    // @PatchMapping("/{id}/others")
    // public ResponseEntity<PTWTaskMaster> patchOthers(
    //         @PathVariable BigDecimal id,
    //         @RequestBody PTWTaskMaster body) {

    //     PTWTaskMaster updated = ptwTaskMasterService.updateRemainingFields(id, body);
    //     return ResponseEntity.ok(updated);
    // }

    @PatchMapping("/{id}/others")
    public ResponseEntity<PTWTaskMaster> patchOthers(
            @PathVariable BigDecimal id,
            @RequestBody PTWTaskMaster body,
            @RequestParam String loginNumber) {

                

        PTWTaskMaster updated = ptwTaskMasterService.updateRemainingFields(id, body,loginNumber);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/getbyPendingWith")
    public ResponseEntity<List<PTWTaskMaster>> list(
            @RequestParam(required = false) String pendingWith) {

        List<PTWTaskMaster> result = (pendingWith == null || pendingWith.isBlank())
                ? ptwTaskMasterService.getAllPTWTaskMasters()
                : ptwTaskMasterService.getByPendingWith(pendingWith.trim());

        return ResponseEntity.ok(result);
    }
    
    // @PutMapping("/close-permit/{id}")
    // public ResponseEntity<PTWTaskMaster> updateRemarksAtClosePermit(
    //         @PathVariable BigDecimal id,
    //         @RequestBody PTWTaskMaster updatedTask) {

    //     PTWTaskMaster updated = ptwTaskMasterService.updateRemarksAtClosePermit(id, updatedTask);
    //     return ResponseEntity.ok(updated);
    // }

    // @GetMapping("/pending/{ptwId}")
    // public ResponseEntity<List<PTWTaskMaster>> getPendingPTWTasks(@PathVariable Integer ptwId) {
    //     List<PTWTaskMaster> tasks = ptwTaskMasterService.getPendingTasksByPTWId(ptwId);
    //     if (tasks.isEmpty()) {
    //         return ResponseEntity.noContent().build();
    //     }
    //     return ResponseEntity.ok(tasks);
    // }


    @GetMapping("/pending/{ptwId}/{employee}")
    public ResponseEntity<PTWTaskMaster> getTopPendingTask(
        @PathVariable Integer ptwId,
        @PathVariable String employee) {

        return ptwTaskMasterService.getTopPendingTask(ptwId, employee)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
