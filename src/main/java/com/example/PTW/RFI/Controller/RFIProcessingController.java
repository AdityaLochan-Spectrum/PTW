package com.example.PTW.RFI.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.PTW.RFI.Entity.RFIProcessing;
import com.example.PTW.RFI.Entity.RfiRemarkId;
import com.example.PTW.RFI.Service.RFIProcessingService;

import java.util.List;

/**
 * REST controller for {@link RFIProcessing}.
 *
 * <p>
 * <b>Endpoints</b>
 * </p>
 * <ul>
 * <li>GET /api/rfi-processing                               — list all</li>
 * <li>GET /api/rfi-processing/{rfiId}/{serial}              — single
 * record</li>
 * <li>DELETE /api/rfi-processing/{rfiId}/{serial}              — delete</li>
 * <li>PUT /api/rfi-processing/{rfiId}/{serial}              — update</li>
 * <li>POST /api/rfi/{rfiId}/processing                      — create inside
 * given RFI</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor // injects final fields
public class RFIProcessingController {

    private final RFIProcessingService service;

    /*
     * ─────────────────────────────── Generic LIST ───────────────────────────────
     */

    @GetMapping("/rfi-processing")
    public ResponseEntity<List<RFIProcessing>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /*
     * ───────────────────── Endpoints that use the composite key ─────────────────
     */

    @GetMapping("/get/rfi-processing")
    public ResponseEntity<RFIProcessing> getById(@RequestParam(required = false) Long rfiId,
            @RequestParam(required = false) Long serial) {

        if (rfiId == null) {
            throw new RuntimeException("RfiID is null");
        } else if (serial == null) {
            throw new RuntimeException("serial ID is null");
        } else if (rfiId <= 0) {
            throw new RuntimeException("RfiID is either zero or minus value");

        } else if (serial <= 0) {
            throw new RuntimeException("SerialID is either zero or minus value");
        }
        RfiRemarkId id = new RfiRemarkId(rfiId, serial);
        RFIProcessing record = service.getById(id);

        return ResponseEntity.ok(record);
    }

    @DeleteMapping("/rfi-processing/{rfiId}/{serial}")
    public ResponseEntity<Void> delete(@PathVariable Long rfiId,
            @PathVariable Long serial) {

        service.delete(new RfiRemarkId(rfiId, serial));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/rfi-processing/{rfiId}/{serial}")
    public ResponseEntity<RFIProcessing> update(@PathVariable Long rfiId,
            @PathVariable Long serial,
            @RequestBody RFIProcessing body) {

        // if(rfiId==null){
        // throw new RuntimeException("RfiID is null");
        // }else if(serial==null){
        // throw new RuntimeException("serial ID is null");
        // }

        RfiRemarkId id = new RfiRemarkId(rfiId, serial);
        return ResponseEntity.ok(service.update(id, body));
    }

    /*
     * ───────────── Create a remark *inside* a given RFI (auto‑numbers serial)
     * ─────────────
     */

    /**
     * POST /api/rfi/{rfiId}/processing
     * Body contains all remark fields <b>except</b> the composite key.
     * The service will calculate the next&nbsp;<code>remarkSerialNo</code>
     * for that RFI and trigger the appropriate workflow.
     */
    @PostMapping("/rfi/{rfiId}/processing")
    public ResponseEntity<RFIProcessing> createForRfi(@PathVariable Long rfiId,
            @RequestBody RFIProcessing body) {

        return ResponseEntity.ok(service.saveForRfi(rfiId, body));
    }

    @PutMapping("/updateRfiProcess")
    public ResponseEntity<RFIProcessing> updateRfiProcessing(
        @RequestParam(required = false) Long rfiId,
        @RequestParam(required = false) Long remarkSerialNo,
            @RequestBody RFIProcessing updatedRfiProcessing) {
                if (rfiId == null) {
                    throw new RuntimeException("RfiID is null");
                } else if (remarkSerialNo == null) {
                    throw new RuntimeException("remarkSerialNo ID is null");
                } else if (rfiId <= 0) {
                    throw new RuntimeException("RfiID is either zero or minus value");
        
                } else if (remarkSerialNo <= 0) {
                    throw new RuntimeException("remarkSerialNo is either zero or minus value");
                }
               

        RFIProcessing updated = service.updateRFIProcessing(rfiId, remarkSerialNo, updatedRfiProcessing);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/pendingwithClient/{user}")
    public ResponseEntity<List<RFIProcessing>> getAllPendingWith(
            @PathVariable("user") String pendingWith) {

        List<RFIProcessing> result = service.getAllPendingWith(pendingWith);
        return ResponseEntity.ok(result);
    }
}
