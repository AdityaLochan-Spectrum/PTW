package com.example.PTW.Controller;

import com.example.PTW.Model.DocumentMaster;
import com.example.PTW.Service.DocumentMasterService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentMasterController {

    private final DocumentMasterService service;

    /*
     * ------------------------------------------------------------------
     * Upload a new document
     * ------------------------------------------------------------------
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentMaster> upload(@RequestPart("file") MultipartFile file,
            @RequestParam String documentCreatedBy,
            @RequestParam String documentKey1,
            @RequestParam String documentType) throws IOException {

        if (documentCreatedBy == null || documentCreatedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("DocumentCreatedBy must not be empty");
        }

        if (documentKey1 == null || documentKey1.trim().isEmpty()) {
            throw new IllegalArgumentException("DocumentKey1 must not be empty");
        }

        if (documentType == null || documentType.trim().isEmpty()) {
            throw new IllegalArgumentException("DocumentType must not be empty");
        }
        System.out.println(documentCreatedBy + ":Hello DocumentCreatedBy");

        // Validate documentKey1 is numeric
        if (!documentKey1.matches("\\d+")) {
            throw new IllegalArgumentException("DocumentKey1 must be a numeric string");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException("Uploaded file must have a name");
        }

        // Extract extension (e.g. ".pdf" or ".png")
        String fileExtension = "";
        int dot = originalFileName.lastIndexOf('.');
        if (dot != -1 && dot < originalFileName.length() - 1) {
            fileExtension = originalFileName.substring(dot);
        }

        DocumentMaster doc = new DocumentMaster();
        doc.setDocumentName(originalFileName);
        doc.setDocumentDescription(originalFileName);
        doc.setFileNamePart1(originalFileName);
        doc.setFileNameExtension(fileExtension);
        doc.setFileType(file.getContentType());
        // doc.setFileContent(Base64.getEncoder().encodeToString(file.getBytes()).getBytes());
        doc.setFileContent(file.getBytes());

        // Keys & metadata supplied by caller / rules
        doc.setDocumentCreatedBy(documentCreatedBy);
        doc.setDocumentKey1(documentKey1);
        doc.setDocumentKey2("");
        doc.setDocumentKey3("");
        doc.setDocumentKey4("");
        doc.setTransactionNumber("1");
        doc.setStatus(true);

        // Version & revision depend on documentType
        if ("D000151".equalsIgnoreCase(documentType)) {
            doc.setVersionNumber(new BigDecimal("1.0000"));
            doc.setRevisionNumber(new BigDecimal("1.0000"));
        } else if ("D000152".equalsIgnoreCase(documentType)) {
            doc.setVersionNumber(new BigDecimal("2.0000"));
            doc.setRevisionNumber(new BigDecimal("2.0000"));
        } else if ("D000153".equalsIgnoreCase(documentType)) {
            doc.setVersionNumber(new BigDecimal("3.0000"));
            doc.setRevisionNumber(new BigDecimal("3.0000"));
        } else if ("D000154".equalsIgnoreCase(documentType)) {
            doc.setVersionNumber(new BigDecimal("4.0000"));
            doc.setRevisionNumber(new BigDecimal("4.0000"));
        }

        doc.setCicoIndication("I");
        doc.setSequenceNumber(new BigDecimal("1.0000"));

        // Persist incoming documentType
        doc.setDocumentType(documentType);

        // Timestamp
        doc.setDocumentCreatedDate(LocalDateTime.now());

        return ResponseEntity.ok(service.save(doc));
    }

    /*
     * ------------------------------------------------------------------
     * Download raw file bytes (Base64 is decoded before streaming)
     * ------------------------------------------------------------------
     */
    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        DocumentMaster doc = service.get(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(Base64.getDecoder().decode(doc.getFileContent()));
    }

    /*
     * ------------------------------------------------------------------
     * Get single document metadata
     * ------------------------------------------------------------------
     */
    @GetMapping("/{id}")
    public DocumentMaster get(@PathVariable String id) {
        return service.get(id);
    }

    /*
     * ------------------------------------------------------------------
     * List all documents
     * ------------------------------------------------------------------
     */
    @GetMapping
    public List<DocumentMaster> list() {
        return service.list();
    }

    /*
     * ------------------------------------------------------------------
     * Delete a document
     * ------------------------------------------------------------------
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
