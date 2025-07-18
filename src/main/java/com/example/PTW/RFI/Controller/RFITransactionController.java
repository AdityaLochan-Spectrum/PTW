package com.example.PTW.RFI.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.PTW.RFI.Entity.RFIProcessing;
import com.example.PTW.RFI.Entity.RFITransaction;
import com.example.PTW.RFI.Service.RFITransactionService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rfi")
public class RFITransactionController {

    private final RFITransactionService service;

    @Autowired
    private RFITransactionService rfitranscation;

    public RFITransactionController(RFITransactionService service) {
        this.service = service;
    }

    /* ─────────────── CREATE ─────────────── */
    @PostMapping("/save/RFI")
    public ResponseEntity<RFITransaction> create(@RequestBody RFITransaction rfi) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(rfi));
    }

    /* ─────────────── READ ─────────────── */
    @GetMapping("getAll")
    public ResponseEntity<List<RFITransaction>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RFITransaction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /* ─────────────── UPDATE ─────────────── */
    // @PutMapping("/update/{id}")
    // public ResponseEntity<RFITransaction> update(@PathVariable Long id,
    // @RequestBody RFITransaction rfi) {
    // return ResponseEntity.ok(service.update(id, rfi));
    // }
    @PutMapping("/update/{id}")
    public ResponseEntity<RFITransaction> update(@PathVariable Long id,
            @RequestBody RFITransaction rfi,
            @RequestParam String processId) {

        if (processId == null || processId.trim().isEmpty()) {
            throw new RuntimeException("Process Id is Important");
        }
        return ResponseEntity.ok(service.update(id, rfi, processId));
    }

    /* ─────────────── DELETE ─────────────── */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{docStatus}")
    public ResponseEntity<List<RFITransaction>> getByStatus(@PathVariable String docStatus) {

        List<RFITransaction> list = service.getByDocStatus(docStatus);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/pending/{pendingwith}")
    public ResponseEntity<List<RFITransaction>> getByPendingWith(@PathVariable String pendingwith) {

        List<RFITransaction> list = service.getByPendingWith(pendingwith);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/pendingClient/{pendingwith}")
    public ResponseEntity<List<RFITransaction>> getByPendingWithQA(@PathVariable String pendingwith) {

        List<RFITransaction> list = service.getByPendingWithforQA(pendingwith);
        return ResponseEntity.ok(list);
    }

}
