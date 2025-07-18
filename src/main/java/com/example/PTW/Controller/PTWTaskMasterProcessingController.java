package com.example.PTW.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PTW.Model.PTWTaskMaster;
import com.example.PTW.Model.PTWTaskMasterProcessing;
import com.example.PTW.Service.PTWTaskMasterProcessingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ptw-processing")
@RequiredArgsConstructor
public class PTWTaskMasterProcessingController {

    private final PTWTaskMasterProcessingService processingService;

    // @PostMapping("/{ptwId}")
    // public ResponseEntity<PTWTaskMasterProcessing> addRemark(
    //         @PathVariable BigDecimal ptwId,
    //         @RequestBody PTWTaskMasterProcessing payload) {

    //     PTWTaskMasterProcessing saved = processingService.saveForPtw(ptwId, payload);
    //     return ResponseEntity.ok(saved);
    // }

    @PostMapping("/{ptwId}")
    public ResponseEntity<PTWTaskMasterProcessing> addRemark(
            @PathVariable BigDecimal ptwId,
            @RequestBody PTWTaskMasterProcessing payload,
            @RequestParam String loginNumber) {



        PTWTaskMasterProcessing saved = processingService.saveForPtw(ptwId, payload,loginNumber);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/{ptwId}")
    public ResponseEntity<List<PTWTaskMasterProcessing>> getAllRemarks(@PathVariable BigDecimal ptwId) {
        List<PTWTaskMasterProcessing> list = processingService.findAllByPtwId(ptwId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<PTWTaskMasterProcessing>> getAlll() {
        List<PTWTaskMasterProcessing> list = processingService.getAlll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PTWTaskMasterProcessing>> getAll() {
        return ResponseEntity.ok(processingService.getAll());
    }

    
    @PostMapping("/saveforReviewer/{ptwId}")
    public ResponseEntity<PTWTaskMasterProcessing> addRemarkForReviewer(
            @PathVariable BigDecimal ptwId,
            @RequestBody PTWTaskMasterProcessing payload) {

        PTWTaskMasterProcessing saved = processingService.saveforReviewr(ptwId, payload);
        return ResponseEntity.ok(saved);
    }

     @GetMapping("/getAllByProcess")
    public ResponseEntity<List<PTWTaskMasterProcessing>> getAllOrByProcess(
            @RequestParam(required = false) String process) {

        List<PTWTaskMasterProcessing> result =
                (process == null || process.isBlank())
                        ? processingService.getAll()
                        : processingService.getByProcess(process.trim());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/close-permit-extension/{ptwId}")
    public ResponseEntity<PTWTaskMasterProcessing> saveAtClosePermit(
            @PathVariable BigDecimal ptwId,
            @RequestBody PTWTaskMasterProcessing payload) {

        PTWTaskMasterProcessing saved = processingService.saveAtVlosePerit(ptwId, payload);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/close-permit/{id}")
    public ResponseEntity<PTWTaskMaster> updateRemarksAtClosePermit(
            @PathVariable("id") BigDecimal id,
            @RequestBody PTWTaskMaster updatedRemarks,
            @RequestParam String LoginNumber) {

        PTWTaskMaster updated = processingService.updateRemarksAtClosePermit(id, updatedRemarks,LoginNumber);
        return ResponseEntity.ok(updated);
    }


    @PostMapping("/issuer-reviewer/{ptwId}")
    public ResponseEntity<PTWTaskMasterProcessing> handleExtensionIssuer(
            @PathVariable BigDecimal ptwId,
            @RequestBody PTWTaskMasterProcessing payload,
            @RequestParam String loginNumber) {

        PTWTaskMasterProcessing result = processingService.saveAtExtensionIssuer(ptwId, payload);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reviewer-extension/{ptwId}")
    public ResponseEntity<PTWTaskMasterProcessing> saveReviewerExtension(
            @PathVariable BigDecimal ptwId,
            @RequestBody PTWTaskMasterProcessing payload) {

        PTWTaskMasterProcessing saved = processingService.saveAtReviwerExtension(ptwId, payload);
        return ResponseEntity.ok(saved);
    }
    // @PostMapping("/reviewer-extension/{ptwId}")
    // public ResponseEntity<PTWTaskMasterProcessing> saveReviewerExtension(
    //         @PathVariable BigDecimal ptwId,
    //         @RequestBody PTWTaskMasterProcessing payload,
    //         @RequestParam String loginNumber) {

    //     PTWTaskMasterProcessing saved = processingService.saveAtReviwerExtension(ptwId, payload,loginNumber);
    //     return ResponseEntity.ok(saved);
    // }
}